package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Host;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Timer;

/**
 * Created by lenovo on 2016-6-22.
 */
public class Config extends SysInfo {

	private static Timer detectTimer; //广播获取groupCtl消息的计时器
	private static int count = 0;

	public static void configure() {
		//初始化topic和对应的编码
		WsnGlobleUtil.initSysTopicMap();

		//TODO 这里还得把ldap的入口搞好
//		WsnGlobleUtil.initNotifyTopicList();

		setParams();
		Host node = new Host(localAddr);
		localMac = node.getMac();
		localSwtId = WsnGlobleUtil.getLinkedSwtId(localMac);
	}

	private static void setParams() {
		Properties props = new Properties();
		String propertiesPath = "RtConfig.properties";
		try {
			props.load(new FileInputStream(propertiesPath));
		} catch (FileNotFoundException e) {
			System.out.println("找不到公共配置文件");
		} catch (IOException e) {
			System.out.println("读取公共配置文件时发生IOException");
		}

		adminAddr = props.getProperty("adminAddress");
		adminPort = Integer.valueOf(props.getProperty("adminPort"));
		groupName = props.getProperty("localGroupName");
		groupCtl = new Controller(props.getProperty("groupCtl"));
		localAddr = props.getProperty("localAddress");
		tPort = Integer.valueOf(props.getProperty("tPort"));
		uPort = Integer.valueOf(props.getProperty("uPort"));

		refreshPeriod = Long.parseLong(props.getProperty("refreshPeriod"));

		edges = new HashSet<>();
//		neighbors = new ConcurrentHashMap<>();
		hostMap = new HashMap<>();
		switchMap = new HashMap<>();
		localSubTopic = new HashSet<>();
		groupSubMap = new HashMap<>();
		lsaSeqNum = 0;
		lsdb = new HashMap<>();
		localCtl = new Controller(localAddr);
		outSwtMap = new HashMap<>();
	}


}
