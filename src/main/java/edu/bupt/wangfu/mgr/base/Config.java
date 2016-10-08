package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Host;
import edu.bupt.wangfu.info.msg.udp.MsgDetectGroupCtl;
import edu.bupt.wangfu.info.msg.udp.MsgDetectGroupCtl_;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lenovo on 2016-6-22.
 */
public class Config extends SysInfo {
	private static DetectTask detectTask; //广播获取groupCtl消息的计时器
	private static Timer detectTimer; //广播获取groupCtl消息的计时器
	private static int count = 0;

	public static void configure() {
		//初始化topic和对应的编码
		WsnGlobleUtil.initSysTopicMap();
		//TODO 这里还得把ldap的入口搞好
//		WsnGlobleUtil.initNotifyTopicList();

		Properties props = new Properties();
		String propertiesPath = "RtConfig.properties";
		try {
			props.load(new FileInputStream(propertiesPath));
		} catch (FileNotFoundException e) {
			System.out.println("找不到公共配置文件");
		} catch (IOException e) {
			System.out.println("读取公共配置文件时发生IOException");
		}

		adminAddr = props.getProperty("adminAddress");
		adminPort = Integer.valueOf(props.getProperty("adminPort"));
		groupName = props.getProperty("localGroupName");
		localAddr = props.getProperty("localAddress");
		tPort = Integer.valueOf(props.getProperty("tPort"));
		uPort = Integer.valueOf(props.getProperty("uPort"));

		long detectPeriod = Long.parseLong(props.getProperty("detectPeriod"));

		groups = new ConcurrentHashMap<>();
		neighbors = new ConcurrentHashMap<>();
		hostSet = new HashSet<>();
		switchSet = new HashSet<>();
		subTable = new ArrayList<>();
		lsaSeqNum = 0;
		lsdb = new ConcurrentHashMap<>();
		localCtl = new Controller(localAddr);
		groupCtl = null;
		outPorts = new ConcurrentHashMap<>();

		//TODO 向谁获知控制器地址，因为要从controlllers里选集群控制器
		//向周围广播，请求本集群代表，然后向主控制器所在主机注册，节点自己维护controllers
		Host node = new Host(localAddr);
		String hostMac = node.getMac();

		//起线程定时查询groupCtl，直到groupCtl被赋值，再cancel这个TimerTask
		String topic = WsnGlobleUtil.getSysTopicMap().get("groupCtl");
		Flow floodOutFlow = FlowHandler.getInstance().generateFlow(localSwitch, portWsn2Swt, "flood", topic, 0, 1);
		FlowHandler.downFlow(localCtl, floodOutFlow, "update");

		for (String out : outPorts.keySet()) {
			Flow inFlow = FlowHandler.getInstance().generateFlow(localSwitch, out, portWsn2Swt, topic, 0, 1);
			FlowHandler.downFlow(localCtl, inFlow, "add");
		}

		detectTask = new DetectTask();
		detectTimer = new Timer();
		detectTimer.schedule(detectTask, detectPeriod, detectPeriod);

		//开始配置，获得当前控制器连接的所有switch和host，以及其中对外连接的port
		localSwitch = WsnGlobleUtil.getLinkedSwtId(hostMac);
		WsnGlobleUtil.initGroup(localSwitch);//初始化了outPorts, hostSet, switchSet
	}

	private static void getGroupCtl() {
		//这里是一跳，但应该已经满足需要了
		String topic = WsnGlobleUtil.getSysTopicMap().get("groupCtl");
		MultiHandler handler = new MultiHandler(uPort, topic);
		MsgDetectGroupCtl msg = new MsgDetectGroupCtl(groupName);

		handler.v6Send(msg);

		//这里会阻塞，没收到就一直挂起，直到到时间被GC;收到的第一个回复决定了这个集群的集群控制器是谁
		Object res = handler.v6Receive();
		MsgDetectGroupCtl_ mdgc_ = (MsgDetectGroupCtl_) res;
		if (mdgc_.groupName.equals(groupName)) {//因为是广播出去的，所以要确定一下这条信息是否自己的集群伙伴
			groupCtl = ((MsgDetectGroupCtl_) res).groupCtl;
			//TODO 这里需要加向groupCtl设置set-controller的过程
		}
	}

	private static class DetectTask extends TimerTask {
		@Override
		public void run() {
			if (localSwitch != null && portWsn2Swt != null) {
				count++;

				//五次探测且都没有回应，说明自己是集群内唯一的节点，那么groupCtl就是本地的Ctl
				if (count >= 5 && groupCtl == null) {
					groupCtl = localCtl;
				}
				if (groupCtl != null) {
					detectTask.cancel();
					detectTimer.cancel();
				}
				getGroupCtl();
			}
		}
	}
}
