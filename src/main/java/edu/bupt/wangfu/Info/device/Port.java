package edu.bupt.wangfu.info.device;

/**
 * Created by lenovo on 2016-6-28.
 */
public class Port {
	private int port;
	private int lastseen;


	public Port(int port) {
		this.port = port;
	}

	public int getLastseen() {
		return lastseen;
	}

	public void setLastseen(int lastseen) {
		this.lastseen = lastseen;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
