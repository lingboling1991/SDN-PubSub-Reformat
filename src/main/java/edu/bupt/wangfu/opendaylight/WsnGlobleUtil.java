package edu.bupt.wangfu.opendaylight;


import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Port;
import edu.bupt.wangfu.info.ldap.WSNTopicObject;
import edu.bupt.wangfu.mgr.base.SysInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-6.
 */
public class WsnGlobleUtil extends SysInfo {
	private static WsnGlobleUtil INSTANCE = new WsnGlobleUtil();
	private static ConcurrentHashMap<String, Controller> controllers = new ConcurrentHashMap<>();//集群内所有的控制器

	private static List<List<String>> notifyTopicList = new ArrayList<>();//主题树-->编码树
	private static ConcurrentHashMap<String, String> sysTopicMap = new ConcurrentHashMap<>();//系统消息对应的编码

	private WsnGlobleUtil() {
		hostSet = new HashSet<>();
		switchSet = new HashSet<>();
	}


	public static WsnGlobleUtil getInstance() {
		return INSTANCE;
	}

	public static void main(String[] args) {
		Controller ctl = new Controller("10.108.164.240:8181");
//		WsnGlobleUtil.getInstance().initGroup(ctl, "52:54:00:b4:46:51");
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
		ConcurrentHashMap<String, String> res = new ConcurrentHashMap<>();
		sysTopicMap = res;
	}

	public static ConcurrentHashMap<String, String> getSysTopicMap() {
		return sysTopicMap;
	}

	public static List<List<String>> getNotifyTopicList() {
		return notifyTopicList;
	}

	public static void initGroup(String swtId) {
		String url = localCtl.url + "/restconf/operational/network-topology:network-topology/";
		String body = RestProcess.doClientGet(url);
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

		ConcurrentHashMap<String, Port> tmp = new ConcurrentHashMap<>();

		for (int i = 0; i < topology.length(); i++) {
			JSONArray link = topology.getJSONObject(i).getJSONArray("link");
			for (int j = 0; j < link.length(); j++) {
				String link_id = link.getJSONObject(j).getString("link-id");
				String[] link_id_info = link_id.split(":");
				if (link_id.contains(swtId) && !link_id.contains("/")) {//<link-id>openflow:117169754616649:1</link-id>
					//TODO 究竟条目长什么样子还需要再看。可以是node中所有端口，减去link中一部分端口
					//这个连接左边是特定交换机，右边也是一个交换机
					String[] dest_info = link.getJSONObject(j).getJSONObject("destination").getString("dest-tp").split(":");
					if (!switchSet.contains(dest_info[1])) {
						//右边的交换机不在这个controller控制下，则左边交换机开的端口就是对外端口
						tmp.put(link_id_info[2], new Port(link_id_info[2]));
					}
				}
			}
		}

		for (Port old : outPorts.values()) {
			if (!tmp.values().contains(old)) {
				neighbors.remove(old.getPort());//旧的对外端口不存在了，那么这个口对应的邻居也就不存在了
				outPorts.remove(old.getPort());
				tmp.remove(old.getPort());
				//TODO 这里需要把原来的out-->wsn流表删掉吗？
			}
		}

		outPorts.putAll(tmp);
	}

	public static String getLinkedSwtId(String wsnMac) {
		//返回wsn程序所在主机所连Switch的odl_id
		String url = localCtl.url + "/restconf/operational/network-topology:network-topology/";
		String body = RestProcess.doClientGet(url);
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
							portWsn2Swt = qs[2];
							return qs[1];
						}
					}
				}
			}
		}
		return null;
	}

	public String[] splitString(String source_port) {
		String[] str;
		str = source_port.split(":");
		for (String aStr : str) System.out.println(aStr);
		return str;
	}
}
