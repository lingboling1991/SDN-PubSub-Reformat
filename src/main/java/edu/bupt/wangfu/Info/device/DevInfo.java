package edu.bupt.wangfu.info.device;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DevInfo {
	public String mac;
	public int distance;//计算最小连通图要用，默认1跳是1
	public HashSet<String> subTopics = new HashSet<>();//订阅的主题编码
	private String url;
	private String errorStatus;
	private String lastSeen;
	private String remark;
	private Map<Switch, List<Switch>> topology;
	//key是端口号，value是设备
	private Map<String, DevInfo> neighbors = new ConcurrentHashMap<>();//不包括跨集群的邻居

	public Map<Switch, List<Switch>> getRuntimeTopology() {
//		RestProcess.downRuntimeTopology();
		return topology;
	}

	public Map<Switch, List<Switch>> getTopology() {
		return topology;
	}

	public void setTopology(Map<Switch, List<Switch>> topology) {
		this.topology = topology;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}

	public String getLastSeen() {//transfer to Date
		return new SimpleDateFormat("HH:mm:ss dd/MM/yy").format(new Date(Long.valueOf(lastSeen)));
//		return lastSeen;
	}

	public void setLastSeen(String lastSeen) {
		this.lastSeen = lastSeen;
	}

	public Map<String, DevInfo> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(Map<String, DevInfo> neighbors) {
		this.neighbors = neighbors;
	}

	public void addNeighbor(String port, DevInfo dev) {
		this.neighbors.put(port, dev);
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


}
