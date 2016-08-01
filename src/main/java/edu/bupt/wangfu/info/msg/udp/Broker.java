package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;

/**
 * Created by lenovo on 2016-6-22.
 */
public class Broker implements Serializable {
	//集群单元
	private static final long serialVersionUID = 1L;

	public String name;
	public String localPort;//此节点连接的是我的哪个端口

	public Broker(String name, String localPort) {
		this.name = name;
		this.localPort = localPort;
	}
}
