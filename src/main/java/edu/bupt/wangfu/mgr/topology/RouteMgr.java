package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.topology.graph.Edge;
import edu.bupt.wangfu.mgr.topology.graph.Node;
import edu.bupt.wangfu.opendaylight.FlowHandler;

import java.util.HashSet;

import static edu.bupt.wangfu.mgr.topology.graph.Kruskal.KRUSKAL;

/**
 * Created by LCW on 2016-7-16.
 */
public class RouteMgr extends SysInfo {
	public static void calAllTopicRoute() {

	}

	public static void calOutPort2Wsn() {
		for (Switch swt : outSwtMap.values()) {
			if (swt.portSet.size() != 0) {
				for (String port : swt.portSet) {
//					Kruskal kruskal = new Kruskal();
//					TODO HashSet<Node>
					KRUSKAL(new HashSet<Node>(), new HashSet<Edge>());


				}
			}
		}
	}

}
