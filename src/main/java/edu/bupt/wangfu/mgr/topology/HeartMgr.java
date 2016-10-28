package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.udp.MsgHello;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.topology.graph.Edge;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lenovo on 2016-6-22.
 */
//只有localCtl == groupCtl时，才启动这个
public class HeartMgr extends SysInfo {
	private SendTask sendTask; //发送hello消息的计时器
	private Timer helloTimer; //hello消息的计时器

	public HeartMgr() {
		for (Switch swt : switchMap.values()) {
			for (String port : swt.portSet) {
				if (!port.equals("LOCAL")) {
					//计算从groupCtl节点的wsn2swt到outPort的路径
					List<String> route = RouteMgr.calHelloRoute(swt.id, port);
					for (int i = 0; i < route.size() - 1; i++) {
						Switch start = switchMap.get(route.get(i));
						Switch finish = switchMap.get(route.get(i + 1));
						String inPort = null, outPort = null;
						inPort = i == 0 ? portWsn2Swt :;
						for (Edge e : edges) {
							if (e.getStart().equals(start) && e.getFinish().equals(finish)) {
								outPort = e.startPort;
							}
						}

						FlowHandler.getInstance().generateFlow(route.get(i), inPort, outPort, "detect", 1, 10);
					}
				}
			}
		}

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
