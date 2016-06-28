package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Port;
import edu.bupt.wangfu.info.msg.udp.GroupUnit;
import edu.bupt.wangfu.info.msg.udp.MsgHello;
import edu.bupt.wangfu.info.msg.udp.MsgHello_;
import edu.bupt.wangfu.mgr.base.RtMgr;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lenovo on 2016-6-22.
 */
public class DtMgr extends SysInfo {
	private RtMgr rtMgr;

	private long threshold;//失效阀值的缺省值
	private long sendPeriod;//发送频率的缺省值
	private SendTask sendTask; //发送hello消息的计时器

	private Timer helloTimer; //hello消息的计时器

	public DtMgr(RtMgr rtMgr) {
		this.rtMgr = rtMgr;
		neighbors = new ConcurrentHashMap<>();
		helloTimer = new Timer();

		//lcw 这里可以变成从管理员读取，那么就需要向管理员请求信息
		Properties props = new Properties();
		String propertiesPath = "DtConfig.properties";
		try {
			props.load(new FileInputStream(propertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		threshold = Long.parseLong(props.getProperty("threshold"));//判断失效阀值
		sendPeriod = Long.parseLong((props.getProperty("sendPeriod")));//发送周期
	}

	public void startSendTask() {
		if (sendTask != null)
			sendTask.cancel();
		sendTask = new SendTask();
		helloTimer.schedule(sendTask, sendPeriod, sendPeriod);
	}

	private void sendHello(String port) {
		MsgHello hello = new MsgHello();
		String addr = WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello");
		MultiHandler handler = new MultiHandler(uPort, addr);

		hello.indicator = localAddr;
		hello.helloInterval = sendPeriod;
		hello.deadInterval = threshold;
		hello.srcPort = port;

		handler.v6Send(hello);
	}

	private void replyHello(MsgHello mh) {
		MsgHello_ reply = new MsgHello_();
		String addr = WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello_");
		MultiHandler handler = new MultiHandler(uPort, addr);

		reply.srcSwitch = localSwitch;
		reply.srcGroup = groupName;

		reply.dstPort = mh.srcPort;
		reply.dstGroup = mh.indicator;

		handler.v6Send(reply);
	}

	public void onHello(MsgHello mh) {
		for (String port : outPorts.keySet()) {
			Flow flow = FlowHandler.generateFlow(localSwitch, wsn2swt, port,
					WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello_"), "");
			FlowHandler.downFlow(localAddr, flow, "update");

			replyHello(mh);

			FlowHandler.deleteFlow(localAddr, flow);
		}
	}

	public void onReply(MsgHello_ mh_) {
		//这条消息是针对 本集群groupName 的 对外端口dstPort 的回复
		if (groupName.equals(mh_.dstGroup) && outPorts.keySet().contains(mh_.dstPort)) {
			//更新邻居信息
			if (!neighbors.containsKey(mh_.dstPort)) {
				GroupUnit groupUnit = new GroupUnit(mh_.srcGroup);
				neighbors.put(mh_.dstPort, groupUnit);
			}
			//更新对外端口信息
			Port port = outPorts.get(mh_.dstPort);
			if (port.getRemoteSwitchId() == null) {
				port.setRemoteSwitchId(mh_.srcSwitch);
				outPorts.put(mh_.dstPort, port);
			}
		}
		Thread lost = new Thread(new LostTask(System.currentTimeMillis()));
		lost.start();
	}

	private class LostTask implements Runnable {
		//收到任意一个端口的回复，就启动这个线程，检查所有outports，看是否有端口超时
		long curTime;

		LostTask(long curTime) {
			this.curTime = curTime;
		}

		@Override
		public void run() {
			for (Port port : outPorts.values()) {
				if (curTime - port.getLastUse() >= threshold) {
					neighbors.remove(port.getPort());
				} else {
					port.setLastUse(curTime);//这样应该修改的是neighbors里面的那个port对象
				}
			}
		}
	}

	//向节点的邻居发送hello消息
	private class SendTask extends TimerTask {
		@Override
		public void run() {
			WsnGlobleUtil.initGroup(localAddr, localSwitch);//更新switchSet，outPorts

			for (String port : outPorts.keySet()) {//定时执行时outPorts内容可能每次都不同
				Flow flow = FlowHandler.generateFlow(localSwitch, wsn2swt, port,
						WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello"), "");
				FlowHandler.downFlow(localAddr, flow, "add");

				sendHello(port);

				FlowHandler.deleteFlow(localAddr, flow);
			}
		}
	}

}
