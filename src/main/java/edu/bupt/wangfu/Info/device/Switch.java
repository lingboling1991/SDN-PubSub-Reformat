package edu.bupt.wangfu.info.device;


import java.util.*;

/**
 * Created by root on 15-7-14.
 */
public class Switch extends DevInfo {
	public HashSet<String> portSet;//经过initGroup()，剩下的端口就是outPorts；在普通节点里，portSet存的是所有激活的端口
	public String id;
	public HashMap<String, Host> hosts;
	private String ipAddr;
	private double load;
	private List<Flow> flows;
	private Map<Integer, List<Queue>> queues;//一个端口有多个队列

	public Switch(String id) {
		this.id = id;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Map<Integer, List<Queue>> getQueues() {
		return queues;
	}

	public void setQueues(Map<Integer, List<Queue>> queues) {
		this.queues = queues;
	}

	public double getLoad() {
		return load;
	}

	public void setLoad(double load) {
		this.load = load;
	}
}