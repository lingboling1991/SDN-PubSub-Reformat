package edu.bupt.wangfu.opendaylight;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Switch;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 2016-5-18.
 */
public class FlowHandler {
	private static FlowHandler ins;
	private static int flowcount;

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
		// e.g. topicList == [[all],[a,b,c],[d,e,f]]
		// 所以all:a:d == 0:0:0

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

	public static boolean issueFlow(Controller controller, int tableNo, Flow flow) {
		//TODO 流表发送地址还需确定
		String url = controller.url;
		String staticFlowPushUri = url + "/";
		List<String> result = RestProcess.doClientPost(staticFlowPushUri, flow.getJsonContent());
		return result.size() < 1 || result.get(0).equals("200");
	}

	public static String getDpid(String controller, String localAddr) {
		//TODO 交换机状态查询地址还需确定，返回wsn连接的交换机的dpid
		String switchStatusUri = controller + "/";
		return RestProcess.doClientGet(switchStatusUri);
	}

	public static Flow generateFlow(Switch curSwitch, String curTopic, String targetPort) {
		String dpid = curSwitch.getDPID();
		HashMap<String, String> parms = new HashMap<>();
		parms.put("switch", dpid);
		parms.put("name", "flow-mod-");//为每个流表指定唯一的名称
		parms.put("cookie", "0");
		parms.put("priority", "32768");
//		parms.put("ipv6_dst", topicName2multiV6Addr(curTopic, topicList, queueNo));//测试v6地址转化函数
		parms.put("active", "true");

		parms.put("actions", "output=" + targetPort);
		parms.put("eth_type", "0x86dd");

		Flow f = new Flow();
		JSONObject content = new JSONObject(parms);
		f.setJsonContent(content);

		return f;
	}

	public static Flow generateFlow(String swtId, String p1, String p2, String action) {
		Flow flow = new Flow();
		//swt是switch在odl里的id，并不是mac或者dpid
		if (action.equals("wsn2out")) {
			//这里是wsn向admin发探测消息，admin回复这个消息所用的flow


		}
		return flow;
	}

	public static void main(String[] args) {

	}

	public static boolean downFlow(Controller controller, List<Flow> flows) {
		boolean success = false;
		for (Flow flow : flows) {
			if (downFlow(controller, flow)) success = true;
		}
		return true;
	}

	public static boolean downFlow(Controller controller, Flow flow) {
		//TODO 这里还要考虑下发到具体哪个流表里，查看是 更新流表项 还是 添加新流表项
		return RestProcess.doClientPost(controller.url, flow.getJsonContent()).get(0).equals("200");
	}
}
