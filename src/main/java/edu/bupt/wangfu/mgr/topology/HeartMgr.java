package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.msg.udp.MsgHello;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lenovo on 2016-6-22.
 */
public class HeartMgr extends SysInfo {
	private SendTask sendTask; //发送hello消息的计时器
	private Timer helloTimer; //hello消息的计时器

	public HeartMgr() {
		/*neighbors = new ConcurrentHashMap<>();//TODO 这个可以在initGroup里面，把自己localSwitchid的邻居写进去

		//TODO 这里可以变成从管理员读取，那么就需要向管理员请求信息
		Properties props = new Properties();
		String propertiesPath = "DtConfig.properties";
		try {
			props.load(new FileInputStream(propertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		threshold = Long.parseLong(props.getProperty("threshold"));//判断失效阀值
		sendPeriod = Long.parseLong(props.getProperty("sendPeriod"));//发送周期*/
	}

	public void startSendTask() {
		if (sendTask != null)
			sendTask.cancel();
		sendTask = new SendTask();
		helloTimer = new Timer();
		helloTimer.schedule(sendTask, 0, sendPeriod);
	}

	private void sendHello(String port, String topic) {
		MsgHello hello = new MsgHello();
		MultiHandler handler = new MultiHandler(uPort, topic);

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
//			WsnGlobleUtil.getTopoInfo();//更新switchSet，outSwtMap

			for (String out : outSwtMap.keySet()) {//定时执行时outPorts内容可能每次都不同
				String topic = WsnGlobleUtil.getSysTopicMap().get("hello");
				Flow outFlow = FlowHandler.getInstance().generateFlow(localSwtId, portWsn2Swt, out, topic, 0, 1);
				FlowHandler.downFlow(localCtl, outFlow, "Add");

				sendHello(out, topic);

				FlowHandler.deleteFlow(localCtl, outFlow);//这里不删的话，后面就会在匹配的时候混乱
			}
		}
	}

}
