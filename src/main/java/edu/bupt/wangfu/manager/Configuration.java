package edu.bupt.wangfu.manager;

import edu.bupt.wangfu.base.SysInfo;
import edu.bupt.wangfu.info.device.Controller;
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
	public void configure() {
		Properties props = new Properties();
		String propertiesPath = "config.properties";
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

		//lcw 开始配置，获得当前控制器连接的所有switch和host，以及其中对外连接的port
		WsnGlobleUtil.setController(localAddr);
		WsnHost node = new WsnHost(localAddr);
		String wsnMac = node.getMac();
		Controller ctl = WsnGlobleUtil.getGroupController();

		String swtId = WsnGlobleUtil.getLinkedSwtId(ctl, wsnMac);
		WsnGlobleUtil.swtStatusInit(ctl, swtId);
		HashSet<String> outPorts = WsnGlobleUtil.getOutPorts();

		//TODO 针对switch向外的连接，下发流表（hello类的流表）
		// 这里先假设一个集群只有一个交换机
		for (String port : outPorts) {
			//通过这个流表传递LSA消息
			Flow flow = FlowHandler.generateFlow(swtId, port, WsnGlobleUtil.wsn2Swt,
					WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello"));
			//TODO 这里要注意是修改流表，不是新增一条，因为进端口是同一个，出端口会变多
			FlowHandler.downFlow(ctl, flow);
		}

		groupMap = new ConcurrentHashMap<>();
		subTable = new ArrayList<>();
		lsdb = new ConcurrentHashMap<>();
		neighbors = new ArrayList<>();
		lsaSeqNum = 0;
	}
}
