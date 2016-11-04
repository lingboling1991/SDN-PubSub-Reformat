package edu.bupt.wangfu.mgr.subpub.rcver;

import edu.bupt.wangfu.info.msg.SubPubInfo;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.subpub.Action;
import edu.bupt.wangfu.opendaylight.MultiHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lenovo on 2016-10-27.
 */
public class PubReceiver extends SysInfo implements Runnable {
	private MultiHandler handler;

	public PubReceiver() {
		handler = new MultiHandler(uPort, "pub", "sys");
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			SubPubInfo np = (SubPubInfo) msg;
			new Thread(new PubHandler(np)).start();
		}
	}

	private class PubHandler implements Runnable {
		private SubPubInfo np;

		PubHandler(SubPubInfo np) {
			this.np = np;
		}

		@Override
		public void run() {
			if (np.group.equals(groupName)) {
				if (np.action.equals(Action.PUB)) {
					Set<String> groupPub = groupPubMap.get(np.topic) == null ? new HashSet<String>() : groupPubMap.get(np.topic);
					groupPub.add(np.swtId);
					groupPubMap.put(np.topic, groupPub);
				} else if (np.action.equals(Action.UNPUB)) {
					Set<String> groupPub = groupPubMap.get(np.topic);
					groupPub.remove(np.swtId);
					groupPubMap.put(np.topic, groupPub);
				}
			} else {
				if (np.action.equals(Action.PUB)) {
					Set<String> outerPub = outerPubMap.get(np.topic) == null ? new HashSet<String>() : outerPubMap.get(np.topic);
					outerPub.add(np.group);
					outerPubMap.put(np.topic, outerPub);
				} else if (np.action.equals(Action.UNPUB)) {
					Set<String> outerPub = outerPubMap.get(np.topic);
					outerPub.remove(np.group);
					outerPubMap.put(np.topic, outerPub);
				}
			}
		}
	}
}
