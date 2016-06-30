package edu.bupt.wangfu.info.device;

/**
 * Created by lenovo on 2016-6-28.
 */
public class Port {
	private String port;
	private String remoteSwitchId;
	private long lastUse;

	public Port(String port) {
		this.port = port;
		this.remoteSwitchId = null;
		this.lastUse = System.currentTimeMillis();
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getRemoteSwitchId() {
		return remoteSwitchId;
	}

	public void setRemoteSwitchId(String remoteSwitchId) {
		this.remoteSwitchId = remoteSwitchId;
	}

	public long getLastUse() {
		return lastUse;
	}

	public void setLastUse(long lastUse) {
		this.lastUse = lastUse;
	}
}
