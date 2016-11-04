package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.Route;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.topology.graph.Edge;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by LCW on 2016-7-16.
 */
public class RouteMgr extends SysInfo {
	public static List<String> calRoute(String startSwtId, String endSwtId) {
		//TODO 冠群
		ArrayList<String> route = new ArrayList<>();

		Route r = new Route();
		r.group = groupName;
		r.startSwtId = startSwtId;
		r.endSwtId = endSwtId;
		r.route = route;

		groupRoutes.add(r);
		spreadRoute(r);//在集群内广播这条路径，后面再需要就不用重复计算了
		return route;
	}

	public static void calGraph(Set<Switch> subers, Set<Switch> pubers) {
		//TODO ！！！张冠群算法，计算发布者订阅者组成的最小连通图，以及转发的消息方向
	}

	private static void spreadRoute(Route r) {
		MultiHandler handler = new MultiHandler(uPort, "route", "sys");
		handler.v6Send(r);
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

	//下发同步流表，使wsn计算出来的新route可以全网同步
	public static void downSyncGroupRouteFlow() {
		Flow floodOutFlow = FlowHandler.getInstance().generateFlow(localSwtId, portWsn2Swt, "flood", "route", "sys", 1, 10);
		FlowHandler.downFlow(localCtl, floodOutFlow, "add");

		for (Switch swt : switchMap.values()) {
			for (String p : swt.neighbors.keySet()) {
				Flow floodInFlow = FlowHandler.getInstance().generateFlow(localSwtId, p, "flood", "route", "sys", 1, 10);
				FlowHandler.downFlow(localCtl, floodInFlow, "add");
			}
		}
	}
}
