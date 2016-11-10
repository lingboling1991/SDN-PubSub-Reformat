package edu.bupt.wangfu.mgr.subpub.rcver;

import edu.bupt.wangfu.info.device.OuterGroup;
import edu.bupt.wangfu.info.msg.SubPubInfo;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.route.RouteMgr;
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
			if (ns.group.equals(groupName)) {//本集群内节点产生的订阅
				if (ns.action.equals(Action.SUB)) {
					Set<String> groupSub = groupSubMap.get(ns.topic) == null ? new HashSet<String>() : groupSubMap.get(ns.topic);
					groupSub.add(ns.swtId + ":" + ns.port);
					groupSubMap.put(ns.topic, groupSub);

					RouteMgr.newSuber(ns.swtId, ns.port, ns.topic);
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

					OuterGroup outerGroup = null;
					for (OuterGroup og : outerGroups) {
						if (og.outerGroupName.equals(ns.group)) {
							outerGroup = og;
							break;
						}
					}
					RouteMgr.newSuber(outerGroup.srcBorderSwtId, outerGroup.srcOutPort, ns.topic);
				} else if (ns.action.equals(Action.UNSUB)) {
					Set<String> outerSub = outerSubMap.get(ns.topic);
					outerSub.remove(ns.group);
					outerSubMap.put(ns.topic, outerSub);
				}
			}

		}
	}
}
