package edu.bupt.wangfu.ldap.policy;

public class TargetHost extends TargetMsg {
	private static final long serialVersionUID = 1L;

	protected String hostIp;

	public TargetHost() {
		this(null);
	}

	public TargetHost(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
}
