package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.mgr.topology.HeartMgr;

import java.util.HashSet;

/**
 * Created by lenovo on 2016-6-22.
 */
public class WsnMgr extends SysInfo {
	private static WsnMgr wsnMgr = new WsnMgr();
	private HeartMgr dt;//集群内检测模块

	private WsnMgr() {
		if (groupCtl.equals(localCtl)) {
			downRepFlow();
			dt = new HeartMgr();
		}
	}

	public static WsnMgr getInstance() {
		return wsnMgr;
	}

	private void downRepFlow() {
		HashSet<Switch> swts = getGroupOutSwts();
		for (Switch swt : swts) {
//			!!!FlowHandler.getInstance().generateFlow();
		}
	}

	private HashSet<Switch> getGroupOutSwts() {
		return new HashSet<>();
	}
}
