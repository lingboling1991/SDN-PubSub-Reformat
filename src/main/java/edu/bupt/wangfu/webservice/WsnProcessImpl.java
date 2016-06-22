package edu.bupt.wangfu.webservice;

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
		// 向管理员注册
		mgrInstance mgrinst = new mgrInstance();
		Thread mgrThread = new Thread(mgrinst);
		mgrThread.start();
		// 从openldap数据库加载主题树
		try {
			System.out.println("read ldap starting!");
			WsnProcessImpl.readTopicTree("ou=all_test,dc=wsn,dc=com");
			WsnProcessImpl.printTopicTree();//lcw 在这里面进行topicList的初始化
			System.out.println("read ldap successed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
