package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.msg.udp.MsgHello;
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
	private SendTask sendTask; //发送hello消息的计时器
	private Timer helloTimer; //hello消息的计时器

	public DtMgr() {
		neighbors = new ConcurrentHashMap<>();

		//TODO 这里可以变成从管理员读取，那么就需要向管理员请求信息
		Properties props = new Properties();
		String propertiesPath = "DtConfig.properties";
		try {
			props.load(new FileInputStream(propertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		threshold = Long.parseLong(props.getProperty("threshold"));//判断失效阀值
		sendPeriod = Long.parseLong(props.getProperty("sendPeriod"));//发送周期
	}

	public void startSendTask() {
		if (sendTask != null)
			sendTask.cancel();
		sendTask = new SendTask();
		helloTimer = new Timer();
		helloTimer.schedule(sendTask, 0, sendPeriod);
	}

	private void sendHello(String port) {
		MsgHello hello = new MsgHello();
		String addr = WsnGlobleUtil.getSysTopicMap().get("hello");
		MultiHandler handler = new MultiHandler(uPort, addr);

		hello.indicator = localAddr;
		hello.helloInterval = sendPeriod;
		hello.deadInterval = threshold;
		hello.srcPort = port;

		handler.v6Send(hello);
	}


	//向节点的邻居发送hello消息
	private class SendTask extends TimerTask {
		@Override
		public void run() {
			WsnGlobleUtil.initGroup(localSwitch);//更新switchSet，outPorts

			for (String port : outPorts.keySet()) {//定时执行时outPorts内容可能每次都不同
				Flow flow = FlowHandler.getInstance().generateFlow(localSwitch, portWsn2Swt, port,
						WsnGlobleUtil.getSysTopicMap().get("hello"), 0, 1);
				FlowHandler.downFlow(localCtl, flow, "add");

				sendHello(port);

				FlowHandler.deleteFlow(localCtl, flow);//这里不删的话，后面就会在匹配的时候混乱
			}
		}
	}

}
