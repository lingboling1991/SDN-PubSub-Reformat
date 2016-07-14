package edu.bupt.wangfu.opendaylight;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.mgr.base.SysInfo;

import java.util.List;

/**
 * Created by lenovo on 2016-5-18.
 */
public class FlowHandler extends SysInfo {
	private static FlowHandler ins;
	private static int flowcount = 0;

	private FlowHandler() {
		this.flowcount = 0;
	}

	public static synchronized FlowHandler getInstance() {
		if (ins == null)
			ins = new FlowHandler();
		return ins;
	}

	public static String topicName2multiV6Addr(String topicName, List<List<String>> topicList, int queueNo) {
		String tc = topicName2topicCode(topicName, topicList);
		return topicCode2multiV6Addr(tc, queueNo);
	}

	public static String topicCode2multiV6Addr(String topicCode, int queueNo) {
		//topicCode length, from 10 to 2, 7 bits
		String len = Integer.toBinaryString(topicCode.length());
		int l = len.length();
		for (int k = 0; k < 7 - l; k++) {
			len = "0" + len;
		}

		//queue number, from 10 to 2, 3 bits
		String qn = Integer.toBinaryString(queueNo);
		l = qn.length();
		for (int k = 0; k < 3 - l; k++) {
			qn = "0" + qn;
		}

		//ldap code, complete to 100 bits
		l = topicCode.length();
		for (int k = 0; k < 100 - l; k++) {
			topicCode = topicCode + "0";
		}

		return "11111111" + "0000" + "1110" + "10" + len + qn + topicCode;
	}

	public static String topicName2topicCode(String topicName, List<List<String>> topicList) {
		//topicList中每一个list代表（像是all:a:d这样）一截主题8bit，100bit能容纳12层
		//e.g. topicList == [[all],[a,b,c],[d,e,f]]
		//所以all:a:d == 0:0:0

		String[] names = topicName.split(":");
		StringBuilder binIndex = new StringBuilder();

		for (int i = 0; i < names.length; i++) {
			List<String> levelList = topicList.get(i);
			String cur = names[i];
			for (int j = 0; j < levelList.size(); j++) {
				if (cur.equals(levelList.get(j))) {
					String t = Integer.toBinaryString(j);
					int l = t.length();
					for (int k = 0; k < 8 - l; k++) {
						t = "0" + t;
					}
					binIndex.append(t);
				}
			}
		}

		return binIndex.toString();
	}

	public static String getDpid(Controller controller, String localAddr) {
		//TODO 执行交换机状态查询功能的 地址 还需确定，返回wsn连接的交换机的dpid
		String switchStatusUri = controller.url + "/";
		return RestProcess.doClientGet(switchStatusUri);
	}

	public static boolean downFlows(Controller controller, List<Flow> flows, List<String> actions) {
		boolean success = false;
		for (Flow flow : flows) {
			if (downFlow(controller, flow, actions.get(flows.indexOf(flow))))
				success = true;
		}
		return success;
	}

	public static boolean deleteFlow(Controller controller, String table_id, String flow_id) {
		String url = controller.url + "/restconf/config/opendaylight-inventory:nodes/node/openflow:" + localSwitch
				+ "/table/" + table_id + "/flow/" + flow_id;
		return RestProcess.doClientDelete(url).equals("200");
	}

	public static boolean deleteFlow(Controller controller, Flow flow) {
		String url = controller.url + "/restconf/config/opendaylight-inventory:nodes/node/openflow:" + localSwitch
				+ "/table/" + flow.getTable_id() + "/flow/" + flow.getFlow_id();
		return RestProcess.doClientDelete(url).equals("200");
	}

	public static boolean downFlow(Controller controller, Flow flow, String action) {
		//TODO 这里还要考虑下发到具体哪个流表里，看要执行的动作是 更新流表项 还是 添加新流表项
		return RestProcess.doClientPost(controller.url, flow.getJsonContent()).get(0).equals("200");
	}

	public Flow generateFlow(String swtId, String in, String out, String topic, int t_id, int pri) {
		//swtId是switch在odl里的id，并不是mac或者dpid
		flowcount++;
		String table_id = String.valueOf(t_id);
		String priority = String.valueOf(pri);//TODO 优先级是数字越大越靠前吗？

		Flow flow = new Flow();
		return flow;
	}
}
