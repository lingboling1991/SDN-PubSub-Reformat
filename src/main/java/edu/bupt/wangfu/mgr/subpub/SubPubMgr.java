package edu.bupt.wangfu.mgr.subpub;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.udp.NewSub;
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
	}

	//TODO 程序如何通知wsn本地产生新订阅
	public static boolean subscribe(String suberAddr, String topic) {
		String[] topicPath = topic.split(":");
		//查看是否已订阅该主题的父主题
		String cur = topicPath[0];

		for (int i = 1; i < topicPath.length; i++) {
			if (localSubTopic.contains(cur))
				return false;
			else
				cur += ":" + topicPath[i];
		}
		//更新本地订阅
		localSubTopic.add(cur);
		//更新本集群订阅
		Set<String> groupSub = groupSubMap.get(cur) == null ? new HashSet<String>() : groupSubMap.get(cur);
		groupSub.add(localSwtId);
		groupSubMap.put(topic, groupSub);
		//全网广播
		spreadNewSub(cur);
		return true;
	}

	private static void spreadNewSub(String cur) {
		NewSub ns = new NewSub();
		MultiHandler handler = new MultiHandler(uPort, "sub", "sys");

		ns.group = groupName;
		ns.swtId = localSwtId;
		ns.hostMac = localMac;
		ns.port = portWsn2Swt;

		ns.topic = cur;

		handler.v6Send(ns);
	}

	//下发注册流表，有了这个流表，之后如果wsn要产生什么订阅或者发布，就可以通过这个流表扩散到全网
	public static void downSubPubFlow() {
		for (Switch swt : switchMap.values()) {
			//swt上连接着集群内其他swt或者host的端口
			for (String port : swt.getNeighbors().keySet()) {
				Flow floodFlow = FlowHandler.getInstance().generateFlow(swt.id, port, "flood", "sub", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
				FlowHandler.downFlow(localCtl, floodFlow, "add");
				floodFlow = FlowHandler.getInstance().generateFlow(swt.id, port, "flood", "pub", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
				FlowHandler.downFlow(localCtl, floodFlow, "add");
			}
			//swt上连接着集群外swt的端口
			for (String port : swt.portSet) {
				if (!port.equals("LOCAL")) {
					Flow floodFlow = FlowHandler.getInstance().generateFlow(swt.id, port, "flood", "sub", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
					FlowHandler.downFlow(localCtl, floodFlow, "add");
					floodFlow = FlowHandler.getInstance().generateFlow(swt.id, port, "flood", "pub", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
					FlowHandler.downFlow(localCtl, floodFlow, "add");
				}
			}
		}
	}
}
