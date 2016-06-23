package edu.bupt.wangfu.opendaylight;


import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.msg.udp.GroupUnit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-6.
 */
public class WsnGlobleUtil {
	private static WsnGlobleUtil INSTANCE = new WsnGlobleUtil();
	public static String wsn2Swt;//wsn连接switch，switch上的的端口
	private static WsnGlobleUtil INSTANCE = new WsnGlobleUtil();	public static ConcurrentHashMap<String, GroupUnit> groups;//保存当前拓扑内出了本集群外所有集群的信息，key为集群名
	private static Controller groupController;
	private static HashMap<String, Controller> controllers = new HashMap<>();
	private static Timer timer = new Timer();
	private static HashSet<String> hostSet = new HashSet<>();//当前集群所有host的mac
	private static HashSet<String> switchSet = new HashSet<>();//当前集群所有switch的id
	private static HashSet<String> outPorts = new HashSet<>();//当前switch对集群外的端口
	private static List<List<String>> notifyTopicList = new ArrayList<>();//lcw 主题树-->编码树
	private static HashMap<String, String> sysTopicMap = new HashMap<>();


	private WsnGlobleUtil() {
		// start timer to recaclateRoute
		timer.schedule(new GlobalTimerTask(), 2000, 5 * 60 * 1000);
	}


	public static WsnGlobleUtil getInstance() {
		return INSTANCE;
	}

	public static void main(String[] args) {
		Controller ctl = new Controller("10.108.164.240:8181");
//		WsnGlobleUtil.getInstance().swtStatusInit(ctl, "52:54:00:b4:46:51");
		String x = "openflow:117169754616649";
		System.out.println(x.substring(9, x.length() - 1));

	}

	public static void initNotifyTopicList(WSNTopicObject topicTree) {
		//TODO 把主题树转化成编码树，编码是v6地址的一部分
		List<List<String>> res = new ArrayList<>();
		notifyTopicList = res;
	}

	public static void initSysTopicMap() {
		//TODO 把管理消息主题和对应的v6地址
		HashMap<String, String> res = new HashMap<>();
		sysTopicMap = res;
	}

	public static HashMap<String, String> getSysTopicMap() {
		return sysTopicMap;
	}

	public static List<List<String>> getNotifyTopicList() {
		return notifyTopicList;
	}

	public static void swtStatusInit(Controller controller, String swtId) {
		String url = controller.getUrl() + "/restconf/operational/network-topology:network-topology/";
		String body = doClientGet(url);
		JSONObject json = new JSONObject(body);
		JSONObject net_topology = json.getJSONObject("network-topology");
		JSONArray topology = net_topology.getJSONArray("topology");

		for (int i = 0; i < topology.length(); i++) {
			JSONArray nodes = topology.getJSONObject(i).getJSONArray("node");
			for (int j = 0; j < nodes.length(); j++) {
				String node_id = nodes.getJSONObject(j).getString("node-id");
				if (node_id.contains("host")) {
					hostSet.add(node_id.substring(5, node_id.length()));
				} else if (node_id.contains("openflow")) {
					switchSet.add(node_id.substring(9, node_id.length()));
				}
			}
		}

		for (int i = 0; i < topology.length(); i++) {
			JSONArray link = topology.getJSONObject(i).getJSONArray("link");
			for (int j = 0; j < link.length(); j++) {
				String link_id = link.getJSONObject(j).getString("link-id");
				String[] link_id_info = link_id.split(":");
				if (link_id.contains(swtId) && !link_id.contains("/")) {//<link-id>openflow:117169754616649:1</link-id>
					//TODO 究竟条目长什么样子还需要再看。可以是node中所有端口，减去link中一部分端口
					// 这个连接左边是特定交换机，右边也是一个交换机
					String[] dest_info = link.getJSONObject(j).getJSONObject("destination").getString("dest-tp").split(":");
					if (!switchSet.contains(dest_info[1])) {
						//右边的交换机不在这个controller控制下，则左边交换机开的端口就是对外端口
						outPorts.add(link_id_info[2]);
					}
				}
			}
		}


	}

	public static String getLinkedSwtId(Controller controller, String wsnMac) {
		//lcw 返回本机所连Switch的odl_id
		String url = controller.getUrl() + "/restconf/operational/network-topology:network-topology/";
		String body = doClientGet(url);
		JSONObject json = new JSONObject(body);
		JSONObject net_topology = json.getJSONObject("network-topology");
		JSONArray topology = net_topology.getJSONArray("topology");

		for (int i = 0; i < topology.length(); i++) {
			JSONArray link = topology.getJSONObject(i).getJSONArray("link");
			for (int j = 0; j < link.length(); j++) {
				String link_id = link.getJSONObject(j).getString("link-id");
				if (link_id.contains(wsnMac)) {
					String[] ps = link_id.split("/");
					for (String p : ps) {
						if (p.contains("openflow")) {
							String[] qs = p.split(":");
							wsn2Swt = qs[2];
							return qs[1];
						}
					}
				}
			}
		}
		return null;
	}

	public static HashSet<String> getOutPorts() {
		return outPorts;
	}

//	public HashMap<String, DevInfo> getNodesOnSwitch() {
//		return nodesOnSwitch;
//	}
//
//	public void addNodesOnSwitch(String mac, DevInfo dev) {
//		WsnGlobleUtil.nodesOnSwitch.put(mac, dev);
//	}


	public static Controller getGroupController() {
		return groupController;
	}

//	public void init() {
//		//get realtime global info
//		reflashGlobleInfo();
//
//		//init all switchs
//		for (Map.Entry<String, Controller> entry : controllers.entrySet()) {
//			Controller controller = entry.getValue();
//			initSwitchs(controller);
//		}
//
//
//	}

	public static void setController(String groupCtlUrl) {
		//TODO 这后面还需要添加当前掉线后如何设置新控制器
		controllers.put(groupCtlUrl, new Controller(groupCtlUrl));
		groupController = controllers.get(groupCtlUrl);
	}

	private boolean initSwitchs(Controller controller) {
		boolean success = false;

		//down init flows
		FlowHandler.downFlow(controller, initFlows);

		return success;
	}

//	public boolean reflashGlobleInfo() {
//
//		//Traversal controllers, GET global realtime status
//		for (Map.Entry<String, Controller> entry : controllers.entrySet()) {
//			Controller controller = entry.getValue();
//			if (!controller.isAlive()) {
//				controllers.remove(entry.getKey());
//				continue;
//			}
//			controller.reflashSwitchMap();
//		}
//		return true;
//	}

//	public synchronized void addController(String controllerAddr) {
//
//		Controller newController = new Controller(controllerAddr);
//
//		newController.reflashSwitchMap();
//
//		controllers.put(controllerAddr, newController);
//
//	}

	public String[] splitString(String source_port) {
		String[] str;
		str = source_port.split(":");
		for (int i = 0; i < str.length; i++)
			System.out.println(str[i]);
		return str;
	}

	public HashSet<String> getHostSet() {
		return hostSet;
	}

	public void setHostSet(HashSet<String> hostSet) {
		WsnGlobleUtil.hostSet = hostSet;
	}

	public HashSet<String> getSwitchSet() {
		return switchSet;
	}

	public void setSwitchSet(HashSet<String> switchSet) {
		WsnGlobleUtil.switchSet = switchSet;
	}

	private class GlobalTimerTask extends TimerTask {

		/**
		 * The action to be performed by this timer task.
		 */
		@Override
		public void run() {

			//whether to adjust queue
			QueueManagerment.qosStart();

			//whether to adjust route

		}
	}
}
