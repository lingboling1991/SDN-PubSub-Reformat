package edu.bupt.wangfu.opendaylight;


import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.ldap.WSNTopicObject;
import edu.bupt.wangfu.mgr.base.SysInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-6.
 */
public class WsnGlobleUtil extends SysInfo {
	//TODO 放到SysInfo？具体怎么存notify主题树？
	private static List<List<String>> notifyTopicList = new ArrayList<>();//主题树-->编码树
	private static ConcurrentHashMap<String, String> sysTopicMap = new ConcurrentHashMap<>();//系统消息对应的编码

	public static void main(String[] args) {
		Controller ctl = new Controller("10.108.165.188:8181");
		String x = "openflow:117169754616649";
		System.out.println(x.substring(9, x.length() - 1));
	}

	public static void initNotifyTopicList(WSNTopicObject topicTree) {
		//TODO 把主题树转化成编码树，编码是v6地址的一部分
		List<List<String>> res = new ArrayList<>();
		notifyTopicList = res;
	}

	public static void initSysTopicMap() {
		//TODO 用SysTopic.properties初始化管理消息主题和对应的v6地址
		//TODO 注意有sub,pub这种eventType不一样的
		ConcurrentHashMap<String, String> res = new ConcurrentHashMap<>();
		sysTopicMap = res;
	}

	public static ConcurrentHashMap<String, String> getSysTopicMap() {
		return sysTopicMap;
	}

	public static List<List<String>> getNotifyTopicList() {
		return notifyTopicList;
	}

	//TODO 需要验证
	public static String getLinkedSwtId(String wsnMac) {
		//返回wsn程序所在主机所连Switch的odl_id
		String url = groupCtl.url + "/restconf/operational/network-topology:network-topology/";
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
}
