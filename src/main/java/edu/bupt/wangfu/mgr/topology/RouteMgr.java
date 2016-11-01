package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.udp.Route;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.topology.graph.Edge;
import edu.bupt.wangfu.opendaylight.FlowHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LCW on 2016-7-16.
 */
public class RouteMgr extends SysInfo {
	public static List<String> calRoute(String startSwtId, String endSwtId) {
		//TODO
		ArrayList<String> route = new ArrayList<>();

		Route r = new Route();
		r.startSwtId = startSwtId;
		r.endSwtId = endSwtId;
		r.route = route;

		groupRoutes.add(r);
		return route;
	}

	public static List<Flow> downRouteFlows(List<String> route, String in, String out, String topic, String topicType, Controller ctl) {
		List<Flow> routeFlows = new ArrayList<>();
		for (int i = 0; i < route.size() - 1; i++) {
			Switch pre = switchMap.get(route.get(i - 1));
			Switch cur = switchMap.get(route.get(i));
			Switch next = switchMap.get(route.get(i + 1));

			String inPort = (i == 0 ? in : null);
			String outPort = (i == route.size() - 1 ? out : null);

			for (Edge e : edges) {
				if (i != 0 && e.getStart().equals(pre.id) && e.getFinish().equals(cur.id))
					inPort = e.finishPort;

				if (i != route.size() - 1 && e.getStart().equals(cur.id) && e.getFinish().equals(next.id))
					outPort = e.startPort;
			}
			Flow flow = FlowHandler.getInstance().generateFlow(route.get(i), inPort, outPort, topic, topicType, 1, 10);
			routeFlows.add(flow);
			FlowHandler.downFlow(ctl, flow, "add");
		}
		return routeFlows;
	}

	public static void delRouteFlows(List<Flow> routeFlows) {
		for (Flow flow : routeFlows) {
			FlowHandler.deleteFlow(groupCtl, flow);
		}
	}
}
