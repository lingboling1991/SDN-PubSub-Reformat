package edu.bupt.wangfu.mgr.subpub;

import edu.bupt.wangfu.info.msg.udp.NewSub;
import edu.bupt.wangfu.mgr.base.SysInfo;
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
			NewSub ns = (NewSub) msg;
			if (ns.group.equals(groupName)) {
				Set<String> groupSub = groupSubMap.get(ns.topic) == null ? new HashSet<String>() : groupSubMap.get(ns.topic);
				groupSub.add(ns.swtId);
				groupSubMap.put(ns.topic, groupSub);
			} else {
				Set<String> outerSub = outerSubMap.get(ns.topic) == null ? new HashSet<String>() : outerSubMap.get(ns.topic);
				outerSub.add(ns.group);
				outerSubMap.put(ns.topic, outerSub);
			}
		}
	}
}
