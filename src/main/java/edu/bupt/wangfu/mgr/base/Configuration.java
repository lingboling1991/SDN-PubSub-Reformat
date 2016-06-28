package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.WsnHost;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lenovo on 2016-6-22.
 */
public class Configuration extends SysInfo {
	public static void configure() {
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
		localAddr = props.getProperty("localAddress");
		tPort = Integer.valueOf(props.getProperty("tPort"));
		uPort = Integer.valueOf(props.getProperty("uPort"));

		groups = new ConcurrentHashMap<>();
		neighbors = new ArrayList<>();
		hostSet = new HashSet<>();
		switchSet = new HashSet<>();
		subTable = new ArrayList<>();
		lsaSeqNum = 0;
		lsdb = new ConcurrentHashMap<>();

		//TODO 向谁获知控制器地址，因为要从controlllers里选集群控制器
		WsnGlobleUtil.setController(localAddr);
		WsnHost node = new WsnHost(localAddr);
		String hostMac = node.getMac();

		//开始配置，获得当前控制器连接的所有switch和host，以及其中对外连接的port
		localSwitch = WsnGlobleUtil.getLinkedSwtId(localAddr, hostMac);
		WsnGlobleUtil.initGroup(localAddr, localSwitch);//初始化了outPorts, hostSet, switchSet
		outPorts = WsnGlobleUtil.getOutPorts();

		//针对switch向外的连接，下发流表（hello类的流表）
		//这里先假设一个集群只有一个交换机
		for (String port : outPorts.keySet()) {
			//这里是“向外的端口进消息，wsn收消息”的流表
			Flow flow = FlowHandler.generateFlow(localSwitch, port, wsn2swt,
					WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello"), "");
			//TODO out_port重复流表会覆盖吗？如果会，那么这里就要注意是修改已有流表而不是新增一条，因为出端口是同一个，进端口会变多
			FlowHandler.downFlow(localAddr, flow, "update");
		}
	}
}
