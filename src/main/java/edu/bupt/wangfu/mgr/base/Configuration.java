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
public class Configuration extends SysInfo {
	private static DetectTask detectTask; //广播获取groupCtl消息的计时器
	private static Timer detectTimer; //广播获取groupCtl消息的计时器

	private static long detectPeriod;

	public static void configure() {
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

		detectPeriod = Long.parseLong(props.getProperty("detectPeriod"));//广播请求信息周期

		groups = new ConcurrentHashMap<>();
		neighbors = new ConcurrentHashMap<>();
		hostSet = new HashSet<>();
		switchSet = new HashSet<>();
		subTable = new ArrayList<>();
		lsaSeqNum = 0;
		lsdb = new ConcurrentHashMap<>();
		localCtl = new Controller(localAddr);
		groupCtl = null;

		//TODO 向谁获知控制器地址，因为要从controlllers里选集群控制器
		//向周围广播，请求本集群代表，然后向主控制器所在主机注册，节点自己维护controllers
		Host node = new Host(localAddr);
		String hostMac = node.getMac();

		//起线程定时查询，直到groupCtl被赋值再cancel
		detectTask = new DetectTask();
		detectTimer = new Timer();
		detectTimer.schedule(detectTask, detectPeriod, detectPeriod);

		//开始配置，获得当前控制器连接的所有switch和host，以及其中对外连接的port
		localSwitch = WsnGlobleUtil.getLinkedSwtId(hostMac);
		WsnGlobleUtil.initGroup(localSwitch);//初始化了outPorts, hostSet, switchSet

		//针对switch向外的连接，下发流表（hello类的流表）
		//这里先假设一个集群只有一个交换机
		for (String port : outPorts.keySet()) {
			//这里是“向外的端口进消息，wsn收消息”的流表
			Flow flow = FlowHandler.getInstance().generateFlow(localSwitch, port, portWsn2Swt,
					WsnGlobleUtil.getSysTopicMap().get("hello"), 0, 1);
			//TODO out_port重复，流表会覆盖吗？如果会，那么这里就要注意是修改已有流表而不是新增一条，因为出端口都是wsn2swt，进端口会变多
			FlowHandler.downFlow(localCtl, flow, "update");
		}
	}

	private static void getGroupController() {
		//TODO 这里还需要细化
		MultiHandler handler = new MultiHandler(uPort, WsnGlobleUtil.getSysTopicMap().get("groupCtl"));
		MsgDetectGroupCtl msg = new MsgDetectGroupCtl(groupName);
		handler.v6Send(msg);

		Object res = handler.v6Receive();

		groupCtl = ((MsgDetectGroupCtl_) res).groupCtl;
	}

	private static class DetectTask extends TimerTask {
		@Override
		public void run() {
			if (localSwitch != null && portWsn2Swt != null) {
				if (groupCtl != null) {
					detectTask.cancel();
					detectTimer.cancel();
				}
				//生成一条flood流表，用来向周围请求groupController信息
				Flow flow = FlowHandler.getInstance().generateFlow(localSwitch, portWsn2Swt, "flood",
						WsnGlobleUtil.getSysTopicMap().get("groupCtl"), 0, 1);
				FlowHandler.downFlow(localCtl, flow, "update");

				getGroupController();
			}
		}
	}
}
