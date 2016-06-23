package edu.bupt.wangfu.info.device;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Flow {
	//	private static String flowCount;
//	private String dpid;
	private int flow_id;
	private int table_id;
	private JSONObject jsonContent;
	private String xmlContent;

	public JSONObject getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(JSONObject jsonContent) {
		this.jsonContent = jsonContent;
	}

	public int getFlow_id() {
		return flow_id;
	}

	public void setFlow_id(int flow_id) {
		this.flow_id = flow_id;
	}

	public int getTable_id() {
		return table_id;
	}

	public void setTable_id(int table_id) {
		this.table_id = table_id;
	}

	public String getXmlContent() {
		return xmlContent;
	}

	public void setXmlContent(String xmlContent, int flowcount, String tableId) {
		//lcw 这里是测试，不想在这里写太多xml格式的东西
		File file = new File("Template.txt");
		BufferedReader reader;
		String s = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			s = reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		s = s.replace("%FLOWNAME%", "flowmod" + flowcount++);
		s = s.replace("%ID%", String.valueOf(flowcount));
		s = s.replace("%TABLE_ID%", "0");

		this.xmlContent = xmlContent;
	}

//	public String getDpid() {
//		return dpid;
//	}
//
//	public void setDpid(String dpid) {
//		this.dpid = dpid;
//	}
//
//	public String getFlowCount() {
//		return flowCount;
//	}
//
//	public void setFlowCount(String flowCount) {
//		Flow.flowCount = flowCount;
//	}


}
