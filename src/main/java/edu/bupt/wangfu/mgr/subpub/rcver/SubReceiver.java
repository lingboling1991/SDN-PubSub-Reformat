package edu.bupt.wangfu.mgr.subpub.rcver;

import edu.bupt.wangfu.info.device.Group;
import edu.bupt.wangfu.info.msg.SubPubInfo;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.route.RouteUtil;
import edu.bupt.wangfu.mgr.subpub.Action;
import edu.bupt.wangfu.opendaylight.MultiHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lenovo on 2016-10-27.
 */
public class SubReceiver extends SysInfo implements Runnable {
	private MultiHandler handler;

	public SubReceiver() {
		handler = new MultiHandler(uPort, "sub", "sys");
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			SubPubInfo ns = (SubPubInfo) msg;
			new Thread(new SubHandler(ns)).start();
		}
	}

	private class SubHandler implements Runnable {
		private SubPubInfo ns;

		SubHandler(SubPubInfo ns) {
			this.ns = ns;
		}

		@Override
		public void run() {
			if (ns.group.equals(localGroupName)) {//本集群内节点产生的订阅
				if (ns.action.equals(Action.SUB)) {
					Set<String> groupSub = groupSubMap.get(ns.topic) == null ? new HashSet<String>() : groupSubMap.get(ns.topic);
					groupSub.add(ns.swtId + ":" + ns.port);
					groupSubMap.put(ns.topic, groupSub);
				} else if (ns.action.equals(Action.UNSUB)) {
					Set<String> groupSub = groupSubMap.get(ns.topic);
					groupSub.remove(ns.swtId + ":" + ns.port);
					groupSubMap.put(ns.topic, groupSub);
				}
			} else {//邻居集群产生的订阅
				if (ns.action.equals(Action.SUB)) {
					Set<String> outerSub = outerSubMap.get(ns.topic) == null ? new HashSet<String>() : outerSubMap.get(ns.topic);
					outerSub.add(ns.group);
					outerSubMap.put(ns.topic, outerSub);

					Group g = allGroups.get(ns.group);
					g.subMap.get(ns.topic).add(ns.swtId + ":" + ns.port);
					g.updateTime = System.currentTimeMillis();
					allGroups.put(g.groupName, g);

					if (localCtl.equals(groupCtl)) {//因为sub信息会全网广播，集群中只要有一个人计算本集群该做什么就可以了
						RouteUtil.newSuber(ns.group, "", "", ns.topic);
					}
				} else if (ns.action.equals(Action.UNSUB)) {
					Set<String> outerSub = outerSubMap.get(ns.topic);
					outerSub.remove(ns.group);
					outerSubMap.put(ns.topic, outerSub);
				}
			}

		}
	}
}
