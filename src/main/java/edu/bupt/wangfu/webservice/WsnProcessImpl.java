package edu.bupt.wangfu.webservice;

import edu.bupt.wangfu.manager.RtMgr;

import javax.jws.WebService;

/**
 * Created by lenovo on 2016-6-22.
 */

@WebService(endpointInterface = "edu.bupt.wangfu.webservice.WsnProcess", serviceName = "WsnProcess")
public class WsnProcessImpl implements WsnProcess {

	public String wsnProcess(String message) {
		return null;
	}

	public void init() {
		System.out.println("init started");

		// 向管理员注册
		new Thread(new mgrInstance()).start();
		// 从openldap数据库加载主题树
		try {
			System.out.println("start reading ldap");
			WsnProcessImpl.readTopicTree("ou=all_test,dc=wsn,dc=com");
			WsnProcessImpl.printTopicTree();//lcw 在这里面进行topicList的初始化
			System.out.println("finish reading ldap");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class mgrInstance implements Runnable {
		public void run() {
			RtMgr.getInstance();
			System.out.println("init finished");
		}
	}
}
