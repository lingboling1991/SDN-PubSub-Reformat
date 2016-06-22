package edu.bupt.wangfu;

import edu.bupt.wangfu.webservice.WsnProcessImpl;

import javax.xml.ws.Endpoint;

/**
 * Created by LCW on 2016-6-19.
 */
public class Start {
	public static void main(String[] args) {
		WsnProcessImpl wsnprocess = new WsnProcessImpl();
		wsnprocess.init();//在这里启动第一个RtMgr，负责维护拓扑和计算路由
		Endpoint.publish(args[0], wsnprocess);
	}
}
