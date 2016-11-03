package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Host;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.subpub.SubPubMgr;
import edu.bupt.wangfu.mgr.topology.graph.Edge;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.RestProcess;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lenovo on 2016-10-16.
 */
public class GroupMgr extends SysInfo {
	private static RefreshGroup refreshTask = new RefreshGroup();
	private static Timer refreshTimer = new Timer();

	public static void main(String[] args) {
		setMaps(new Controller("10.108.165.188:8181"));
	}

	public static void initGroup()  {
		refreshTimer.schedule(refreshTask, 0, refreshPeriod);
	}

	//初始化hostMap，switchMap，outPorts
	private static void setMaps(Controller controller) {
		String url = controller.url + "/restconf/operational/network-topology:network-topology/";

		//测试用
		HashMap<String, Host> hostMap = new HashMap<>();
		HashMap<String, Switch> switchMap = new HashMap<>();
		HashSet<Edge> edges = new HashSet<>();
		//结束

		String body = RestProcess.doClientGet(url);
		JSONObject json = new JSONObject(body);
		JSONObject net_topology = json.getJSONObject("network-topology");
		JSONArray topology = net_topology.getJSONArray("topology");
		JSONArray nodes = topology.getJSONObject(0).getJSONArray("node");
		for (int j = 0; j < nodes.length(); j++) {
			String node_id = nodes.getJSONObject(j).getString("node-id");
			if (node_id.contains("host")) {
				String swtId = nodes.getJSONObject(j).getJSONArray("host-tracker-service:attachment-points").getJSONObject(0).getString("tp-id");
				swtId = swtId.substring(9, swtId.length() - 2);
				String ip = nodes.getJSONObject(j).getJSONArray("host-tracker-service:addresses").getJSONObject(0).getString("ip");
				String mac = node_id.substring(5, node_id.length());

				Host host = new Host(ip);
				host.setMac(mac);
				host.swt = new Switch(swtId);
				hostMap.put(mac, host);
			} else if (node_id.contains("openflow")) {
				String swtId = node_id.split(":")[1];
				Switch swt = new Switch(swtId);
				swt.portSet = new HashSet<>();
				JSONArray nbs = nodes.getJSONObject(j).getJSONArray("termination-point");
				for (int i = 0; i < nbs.length(); i++) {
					String port = nbs.getJSONObject(i).getString("tp-id").split(":")[2];
					swt.portSet.add(port);
				}
				switchMap.put(swtId, swt);
			}
		}

		for (int i = 0; i < topology.length(); i++) {
			JSONArray links = topology.getJSONObject(i).getJSONArray("link");
			for (int j = 0; j < links.length(); j++) {
				String link_id = links.getJSONObject(j).getString("link-id");
				String dest = links.getJSONObject(j).getJSONObject("destination").getString("dest-tp");
				String src = links.getJSONObject(j).getJSONObject("source").getString("source-tp");
				String hostMac = null, swtId1, port1, swtId2 = null, port2 = null;
				//获取连接关系
				if (link_id.contains("host")) {
					hostMac = dest.contains("host") ? dest.substring(5, dest.length()) : src.substring(5, src.length());
					swtId1 = dest.contains("host") ? src.split(":")[1] : dest.split(":")[1];
					port1 = dest.contains("host") ? src.split(":")[2] : dest.split(":")[2];
				} else {
					swtId1 = src.split(":")[1];
					port1 = src.split(":")[2];
					swtId2 = dest.split(":")[1];
					port2 = dest.split(":")[2];
				}
				//修改连接关系
				if (hostMac != null) {
					Host h = hostMap.get(hostMac);
					switchMap.get(swtId1).addNeighbor(port1, h);
					switchMap.get(swtId1).portSet.remove(port1);
				} else {
					Switch s1 = switchMap.get(swtId1);
					Switch s2 = switchMap.get(swtId2);
					s1.addNeighbor(port1, s2);
					s1.portSet.remove(port1);
					s2.addNeighbor(port2, s1);
					s2.portSet.remove(port2);
				}
			}
		}

		for (int i = 0; i < topology.length(); i++) {
			JSONArray links = topology.getJSONObject(i).getJSONArray("link");
			for (int j = 0; j < links.length(); j++) {
				String link_id = links.getJSONObject(j).getString("link-id");
				if (!link_id.contains("host")) {//这说明这个连接是一个swt--swt的连接
					Edge e = new Edge();
					String[] s = links.getJSONObject(j).getJSONObject("destination").getString("dest-tp").split(":");
					e.setStart(s[1]);
					e.startPort = s[2];

					String[] f = links.getJSONObject(j).getJSONObject("source").getString("source-tp").split(":");
					e.setFinish(f[1]);
					e.finishPort = f[2];

					edges.add(e);
				}
			}
		}

		for (Switch swt : switchMap.values()) {
			if (swt.portSet.size() > 1) {
				outSwitchs.add(swt);
			}
		}

		System.out.println("test to see hostMap and switchMap");

	}

	//groupCtl下发全集群各swt上flood流表
	private static void downRepFlow() {
		for (Switch swt : switchMap.values()) {
			String id = swt.id;
			for (String p : swt.neighbors.keySet()) {
				Flow fromGroupCtlFlow = FlowHandler.getInstance().generateFlow(id, p, "flood", "rest", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
//				TODO fromGroupCtlFlow.setV4Src(localAddr);
				FlowHandler.downFlow(localCtl, fromGroupCtlFlow, "add");

				Flow toGroupCtlFlow = FlowHandler.getInstance().generateFlow(id, p, "flood", "rest", "sys", 1, 10);
//				TODO toGroupCtlFlow.setV4Dst(localAddr);
				FlowHandler.downFlow(localCtl, toGroupCtlFlow, "add");
			}
		}
	}

	//localCtl下发本地swt上flood流表
	private static void downRegFlow() {
		Flow toGroupCtlFlow = FlowHandler.getInstance().generateFlow(localSwtId, portWsn2Swt, "flood", "rest", "sys", 1, 10);//TODO 优先级是越大越靠后吗？
//		TODO fromGroupCtlFlow.setV4Dst(groupCtl.url);
		FlowHandler.downFlow(localCtl, toGroupCtlFlow, "add");
	}

	//更新group拓扑信息
	private static class RefreshGroup extends TimerTask {
		@Override
		public void run() {
			setMaps(groupCtl);
			while (switchMap.size() == 0)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			SubPubMgr.downSubPubFlow();
			RouteMgr.downSyncGroupRouteFlow();
			//下发访问groupCtl的flood流表
			if (localCtl.equals(groupCtl)) {
				downRepFlow();
			} else {
				downRegFlow();
			}
		}
	}
}
