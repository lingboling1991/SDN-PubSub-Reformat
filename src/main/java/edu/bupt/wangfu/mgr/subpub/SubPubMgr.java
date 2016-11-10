package edu.bupt.wangfu.mgr.subpub;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.SubPubInfo;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.subpub.rcver.PubReceiver;
import edu.bupt.wangfu.mgr.subpub.rcver.SubReceiver;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LCW on 2016-7-19.
 */
public class SubPubMgr extends SysInfo {
	private static CheckSplit splitTask = new CheckSplit(splitThreshold);
	private static Timer splitTimer = new Timer();

	public SubPubMgr() {
		new Thread(new SubPubRegister(tPort)).start();//接收新发布者和订阅者的注册

		new Thread(new SubReceiver()).start();
		new Thread(new PubReceiver()).start();
		splitTimer.schedule(splitTask, checkSplitPeriod, checkSplitPeriod);
	}

	public static boolean subscribe(String topic) {
		String[] topicPath = topic.split(":");
		//查看是否已订阅该主题的父主题
		String cur = topicPath[0];

		for (int i = 1; i < topicPath.length; i++) {
			if (localSubTopic.contains(cur))
				return false;
			else
				cur += ":" + topicPath[i];
		}
		if (needUnite(topic)) {
			String father = getTopicFather(topic);
			joinedSubTopics.add(father);
			subscribe(father);
			unsubscribeSons(father);
			return true;
		} else {
			//更新本地订阅
			localSubTopic.add(cur);
			if (joinedSubTopics.contains(cur))
				joinedSubTopics.remove(cur);
			//更新集群订阅
			Set<String> groupSub = groupSubMap.get(cur) == null ? new HashSet<String>() : groupSubMap.get(cur);
			groupSub.add(localSwtId + ":" + portWsn2Swt);
			groupSubMap.put(topic, groupSub);
			//全网广播
			spreadSPInfo(cur, "sub", Action.SUB);
			return true;
		}
	}

	private static void unsubscribeSons(String father) {
		for (String topic : localSubTopic) {
			if (topic.contains(father) && topic.length() > father.length()) {
				unsubscribe(topic);
				joinedUnsubTopics.add(topic);
			}
		}
	}

	//取得该主题的父主题
	private static String getTopicFather(String topic) {
		String[] topicPath = topic.split(":");
		String fatherTopic = topicPath[0];
		for (int i = 1; i < topicPath.length - 1; i++) {
			fatherTopic += ":" + topicPath[i];
		}
		return fatherTopic;
	}

	private static boolean needUnite(String topic) {
		//TODO 先确定主题是如何存储的，再完成这块
		return true;
	}

	public static boolean unsubscribe(String topic) {
		if (joinedSubTopics.contains(topic))//若这个订阅是聚合而成的，那么不能取消，因为并不是真实订阅
			return false;
		if (groupSubMap.get(topic) == null)
			return false;//本地没有这个订阅

		localSubTopic.remove(topic);

		Set<String> groupSub = groupSubMap.get(topic);
		groupSub.remove(localSwtId + ":" + portWsn2Swt);
		groupSubMap.put(topic, groupSub);

		//TODO 删除本地交换机上的这条进流表,outport==portWsn2Swt,topic==topic

		spreadSPInfo(topic, "sub", Action.UNSUB);

		return true;
	}

	public static boolean publish(String topic) {
		//更新本集群发布
		Set<String> groupPub = groupPubMap.get(topic) == null ? new HashSet<String>() : groupPubMap.get(topic);

		if (groupPub.contains(localSwtId))
			return false;

		groupPub.add(localSwtId + ":" + portWsn2Swt);
		groupPubMap.put(topic, groupPub);
		//全网广播
		spreadSPInfo(topic, "pub", Action.PUB);
		return true;
	}

	public static boolean unPublish(String topic) {
		if (groupPubMap.get(topic) == null)
			return false;

		Set<String> groupPub = groupPubMap.get(topic);
		groupPub.remove(localSwtId + ":" + portWsn2Swt);
		groupPubMap.put(topic, groupPub);

		//TODO 删除本地的这条发出流表,inport==portWsn2Swt,topic==topic

		spreadSPInfo(topic, "pub", Action.UNPUB);

		return true;
	}

	private static void spreadSPInfo(String topic, String type, Action action) {
		SubPubInfo nsp = new SubPubInfo();
		MultiHandler handler = new MultiHandler(uPort, type, "sys");

		nsp.action = action;
		nsp.group = groupName;
		nsp.swtId = localSwtId;
		nsp.hostMac = localMac;
		nsp.hostIP = localAddr;
		nsp.port = portWsn2Swt;

		nsp.topic = topic;

		handler.v6Send(nsp);
	}

	//下发注册流表，有了这个流表，之后如果wsn要产生什么订阅或者发布，就可以通过这个流表扩散到全网
	public static void downSubPubFlow() {
		for (Switch swt : switchMap.values()) {
			for (String port : swt.neighbors.keySet()) {//swt上连接着集群内其他swt或者host的端口
				Flow floodFlow = FlowHandler.getInstance().generateFlow(swt.id, port, "flood", "sub", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
				FlowHandler.downFlow(localCtl, floodFlow, "add");
				floodFlow = FlowHandler.getInstance().generateFlow(swt.id, port, "flood", "pub", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
				FlowHandler.downFlow(localCtl, floodFlow, "add");
			}
			for (String port : swt.portSet) {
				if (!port.equals("LOCAL")) {//swt上连接着集群外swt的端口
					Flow floodFlow = FlowHandler.getInstance().generateFlow(swt.id, port, "flood", "sub", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
					FlowHandler.downFlow(localCtl, floodFlow, "add");
					floodFlow = FlowHandler.getInstance().generateFlow(swt.id, port, "flood", "pub", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
					FlowHandler.downFlow(localCtl, floodFlow, "add");
				}
			}
		}
	}

	private static class CheckSplit extends TimerTask {
		int splitThreshold = 1;//TODO 需要动态设置？

		public CheckSplit(int splitThreshold) {
			this.splitThreshold = splitThreshold;
		}

		@Override
		public void run() {
			for (String father : joinedSubTopics) {
				if (getCurFlowStatus(father) > splitThreshold) {
					unsubscribe(father);
					for (String son : joinedUnsubTopics) {
						if (son.contains(father)) {
							subscribe(son);
						}
					}
				}
			}
		}

		private int getCurFlowStatus(String father) {
			//TODO 需要查询groupCtl，逻辑要问牛琳琳
			return 1;//TODO 返回的是百分比？20/100就返回20？
		}
	}
}
