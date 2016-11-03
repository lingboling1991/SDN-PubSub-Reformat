package edu.bupt.wangfu.mgr.subpub;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.udp.SubPubInfo;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LCW on 2016-7-19.
 */
public class SubPubMgr extends SysInfo {
	SubPubMgr() {
		new Thread(new SubReceiver()).start();
		new Thread(new PubReceiver()).start();
	}

	//TODO 后面测试时要考虑程序如何通知wsn本地产生新订阅
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
			String fatherTopic = getTopicFather(topic);


			return true;
		} else {
			//更新本地订阅
			localSubTopic.add(cur);
			//更新本集群订阅
			Set<String> groupSub = groupSubMap.get(cur) == null ? new HashSet<String>() : groupSubMap.get(cur);
			groupSub.add(localSwtId);
			groupSubMap.put(topic, groupSub);
			//全网广播
			spreadSPInfo(cur, "sub", Action.SUB);
			return true;
		}
	}

	//切出该主题的父主题
	private static String getTopicFather(String topic) {
		String[] topicPath = topic.split(":");
		String fatherTopic = topicPath[0];
		for (int i = 1; i < topicPath.length - 1; i++) {
			fatherTopic += ":" + topicPath[i];
		}
		return fatherTopic;
	}

	private static boolean needUnite(String topic) {
		//TODO 冠群那里有算法
		return true;
	}

	public static boolean unSubscribe(String topic) {
		localSubTopic.remove(topic);
		if (true)//TODO 这里要判断这个主题订阅是否是聚合而成的，如果是，那么不能取消订阅（下面还有若干子主题，取消了就都收不到了）
			return false;
		if (groupSubMap.get(topic) == null)
			return false;//本地没有这个订阅
		Set<String> groupSub = groupSubMap.get(topic);
		groupSub.remove(localSwtId);
		groupSubMap.put(topic, groupSub);
		spreadSPInfo(topic, "sub", Action.UNSUB);

		return true;
	}

	//TODO 同subscribe()
	public static boolean publish(String topic) {
		//更新本集群发布
		Set<String> groupPub = groupPubMap.get(topic) == null ? new HashSet<String>() : groupPubMap.get(topic);

		if (groupPub.contains(localSwtId))
			return false;

		groupPub.add(localSwtId);
		groupPubMap.put(topic, groupPub);
		//全网广播
		spreadSPInfo(topic, "pub", Action.PUB);
		return true;
	}

	public static boolean unPublish(String topic) {
		if (groupPubMap.get(topic) == null)
			return false;
		Set<String> groupPub = groupPubMap.get(topic);
		groupPub.remove(localSwtId);
		groupPubMap.put(topic, groupPub);

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
}
