package edu.bupt.wangfu.mgr.subscribtion;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.udp.NewSub;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

/**
 * Created by LCW on 2016-7-19.
 */
public class SubMgr extends SysInfo {
	//本地产生新订阅
	public static boolean subscribe(String suberAddr, String topic) {
		String[] topicPath = topic.split(":");

		//查看是否已订阅该主题的父主题
		String cur = topicPath[0];
		for (int i = 1; i < topicPath.length; i++) {
			if (subTable.contains(cur)) return false;
			else cur += ":" + topicPath[i];
		}
		subTable.add(cur);
		spreadNewSub(cur);
		return true;
	}

	private static void spreadNewSub(String cur) {
		//TODO 尚缺收到后的处理函数
		NewSub ns = new NewSub();
		MultiHandler handler = new MultiHandler(uPort, WsnGlobleUtil.getSysTopicMap().get("sub"));

		ns.topic = cur;
		ns.hostMac = localMac;
		ns.port = portWsn2Swt;
		ns.swtId = localSwtId;

		handler.v6Send(ns);
	}

	//下发注册流表，有了这个流表，之后如果wsn要产生什么订阅或者发布，就可以通过这个流表扩散到全网
	public static void downSubPubFlow() {
		for (Switch swt : switchMap.values()) {
			//swt上连接着集群内其他swt或者host的端口
			for (String port : swt.getNeighbors().keySet()) {
				Flow floodFlow = FlowHandler.getInstance().generateSubPubFlow(swt.id, port, "flood", "sub", 1, 10);//TODO 优先级是越大越靠后吗？
				FlowHandler.downFlow(localCtl, floodFlow, "add");
				floodFlow = FlowHandler.getInstance().generateSubPubFlow(swt.id, port, "flood", "pub", 1, 10);//TODO 优先级是越大越靠后吗？
				FlowHandler.downFlow(localCtl, floodFlow, "add");
			}
			//swt上连接着集群外swt的端口
			for (String port : swt.portSet) {
				if (!port.equals("LOCAL")) {
					Flow floodFlow = FlowHandler.getInstance().generateSubPubFlow(swt.id, port, "flood", "sub", 1, 10);//TODO 优先级是越大越靠后吗？
					FlowHandler.downFlow(localCtl, floodFlow, "add");
					floodFlow = FlowHandler.getInstance().generateSubPubFlow(swt.id, port, "flood", "pub", 1, 10);//TODO 优先级是越大越靠后吗？
					FlowHandler.downFlow(localCtl, floodFlow, "add");
				}
			}
		}
	}
}
