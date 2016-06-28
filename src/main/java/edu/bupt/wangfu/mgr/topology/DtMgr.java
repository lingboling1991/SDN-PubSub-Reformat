package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.msg.udp.MsgHello;
import edu.bupt.wangfu.info.msg.udp.MsgHello_;
import edu.bupt.wangfu.mgr.base.RtMgr;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lenovo on 2016-6-22.
 */
public class DtMgr extends SysInfo {
	private RtMgr rtMgr;

	private long threshold;//失效阀值的缺省值
	private long sendPeriod;//发送频率的缺省值
	private SendTask sendTask; //发送hello消息的计时器

	private Timer helloTimer; //hello消息的计时器
	private Timer lostTimer;//邻居丢失计时器

	//丢失处理
//	private LostTask[] lostTask; // 当邻居丢失时需要发生的动作
//	private ConcurrentHashMap<String, Integer> nbName2index; // 记录邻居集群名称到他们所在LostTask项的对应
//	private List<Integer> avlNum; // 记录可用的losttask坐标

	public DtMgr(RtMgr rtMgr) {
		this.rtMgr = rtMgr;
		neighbors = new ArrayList<>();
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

	public void onMsg(MsgHello mh) {
		for (String port : outPorts.keySet()) {
			Flow flow = FlowHandler.generateFlow(localSwitch, wsn2swt, port,
					WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello_"), "");
			FlowHandler.downFlow(localAddr, flow, "update");

			replyHello(mh);

			FlowHandler.deleteFlow(localAddr, flow);
		}
	}

	//向节点的邻居发送hello消息
	private class SendTask extends TimerTask {
		@Override
		public void run() {
			for (String port : outPorts.keySet()) {//定时执行时outPorts内容可能每次都不同
				Flow flow = FlowHandler.generateFlow(localSwitch, wsn2swt, port,
						WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello"), "");
				FlowHandler.downFlow(localAddr, flow, "add");

				sendHello(port);

				FlowHandler.deleteFlow(localAddr, flow);
			}
			WsnGlobleUtil.initGroup(localAddr, localSwitch);//更新switchSet，outPorts
		}
	}

}
