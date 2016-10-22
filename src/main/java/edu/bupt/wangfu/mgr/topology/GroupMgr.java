package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Host;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.mgr.base.SysInfo;
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

	public static void initGroup() throws InterruptedException {
		refreshTimer.schedule(refreshTask, 0, refreshPeriod);
		while (switchMap.size() == 0)
			Thread.sleep(100);

		//下发访问groupCtl的flood流表
		if (localCtl.equals(groupCtl)) downRepFlow();
		else downRegFlow();
	}

	//初始化hostMap，switchMap，outSwtMap
	private static void setMaps(Controller controller) {
		String url = controller.url + "/restconf/operational/network-topology:network-topology/";

		//测试用
		HashMap<String, Host> hostMap = new HashMap<>();
		HashMap<String, Switch> switchMap = new HashMap<>();
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
				//TODO 清理portSet，让里面剩下的都是outPort
				if (hostMac != null) {
					Host h = hostMap.get(hostMac);
					switchMap.get(swtId1).addNeighbor(port1, h);
				} else {
					Switch s1 = switchMap.get(swtId1);
					Switch s2 = switchMap.get(swtId2);
					s1.addNeighbor(port1, s2);
					s2.addNeighbor(port2, s1);
				}
			}
		}

		System.out.println("test to see hostMap and switchMap");

		//TODO 这里还有outPorts没有初始化
		/*for (Switch old : outSwtMap.values()) {
			if (!tmp.values().contains(old)) {
				neighbors.remove(old.port);//旧的对外端口不存在了，那么这个口对应的邻居也就不存在了
				outSwtMap.remove(old.getPort());
				tmp.remove(old.getPort());
			}
		}

		outSwtMap.putAll(tmp);*/
	}

	//groupCtl下发全集群各swt上flood流表
	private static void downRepFlow() {
		for (Switch swt : switchMap.values()) {
			String id = swt.id;
			for (String p : swt.getNeighbors().keySet()) {
				Flow fromGroupCtlFlow = FlowHandler.getInstance().generateFlow(id, p, "flood", "", 1, 10);//TODO 优先级是越大越靠后吗？
//				fromGroupCtlFlow.setSrc(localAddr);
				FlowHandler.downFlow(localCtl, fromGroupCtlFlow, "add");

				Flow toGroupCtlFlow = FlowHandler.getInstance().generateFlow(id, p, "flood", "", 1, 10);
//				toGroupCtlFlow.setDst(localAddr);
				FlowHandler.downFlow(localCtl, toGroupCtlFlow, "add");
			}
		}
	}

	//localCtl下发本地swt上flood流表
	private static void downRegFlow() {
		Flow toGroupCtlFlow = FlowHandler.getInstance().generateFlow(localSwtId, portWsn2Swt, "flood", "", 1, 10);//TODO 优先级是越大越靠后吗？
//		fromGroupCtlFlow.setDst(groupCtl.url);
		FlowHandler.downFlow(localCtl, toGroupCtlFlow, "add");
	}

	//更新group拓扑信息
	private static class RefreshGroup extends TimerTask {
		@Override
		public void run() {
			setMaps(groupCtl);
		}
	}
}
