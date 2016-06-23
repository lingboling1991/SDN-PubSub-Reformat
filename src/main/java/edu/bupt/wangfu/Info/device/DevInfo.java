package edu.bupt.wangfu.info.device;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DevInfo {
	private static DevInfo INSTANCE = null;
	private String url;
	private String mac;
	private String port;
	private String errorStatus;
	private String lastSeen;
	private String remark;
	private Map<String, Switch> switchs;
	private Map<Switch, List<Switch>> topology;
	//key是端口号，value是设备
	private Map<Integer, DevInfo> wsnDevMap = new ConcurrentHashMap<Integer, DevInfo>();

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

	public Map<String, Switch> getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Map<String, Switch> switchs) {
		this.switchs = switchs;
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

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;//这里的port在WsnHost中，表示host与switch连接中switch开放的端口
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

	public Map<Integer, DevInfo> getWsnDevMap() {
		return wsnDevMap;
	}

	public void setWsnDevMap(Map<Integer, DevInfo> wsnDevMap) {
		this.wsnDevMap = wsnDevMap;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


}
