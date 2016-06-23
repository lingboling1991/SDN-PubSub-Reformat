package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.mgr.base.RtMgr;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.info.msg.udp.MsgHello;
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

	private Timer timerForHellos; //为邻居们记录hello消息的计时器

	public DtMgr(RtMgr rtMgr) {
		this.rtMgr = rtMgr;
		neighbors = new ArrayList<>();
		timerForHellos = new Timer();

		//这里可以变成从管理员读取，那么就需要向管理员请求信息
		Properties props = new Properties();
		String propertiesPath = "DtConfig.properties";
		try {
			props.load(new FileInputStream(propertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		threshold = Long.parseLong(props.getProperty("threshold"));
		sendPeriod = Long.parseLong((props.getProperty("sendPeriod")));
	}

	public void startSendTask() {
		if (sendTask != null)
			sendTask.cancel();
		sendTask = new SendTask();
		timerForHellos.schedule(sendTask, sendPeriod, sendPeriod);
	}

	private void sendAction() {
		//发MsgHello
		MsgHello hello = new MsgHello();
		String addr = WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello");
		MultiHandler handler = new MultiHandler(rtMgr.getuPort(), addr);

		hello.indicator = rtMgr.getLocalAddr();

		hello.helloInterval = sendPeriod;
		hello.deadInterval = threshold;

		//sent to other groups
		handler.v6Send(hello);
		//TODO lcw 这里不需要给集群内发hello消息了，直接轮询控制器就可以
	}

	//DtMgr主要作用是发送心跳，这个target就是目的地址集合
	public void addTarget(String indicator) {

	}

	//remove the detection of a neighbor
	public void removeTarget(String indicator) {

	}

	//邻居超时未回复，后面要采取的行动
//	private class LostTask extends TimerTask {
//		String groupName;
//
//		public LostTask(String groupName) {
//			this.groupName = groupName;
//		}
//
//		@Override
//		public void run() {
//			removeTarget(groupName);
////			rtMgr.lost(groupName);
//		}
//	}

	//向节点的邻居发送hello消息
	private class SendTask extends TimerTask {
		@Override
		public void run() {
			sendAction();
		}
	}
}
