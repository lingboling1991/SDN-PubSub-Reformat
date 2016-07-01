package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;

public class MsgHello implements Serializable {
	private static final long serialVersionUID = 1L;

	public String indicator;//发送集群名称，就是groupname
	public long helloInterval;//发送hello消息的时间间隔
	public long deadInterval;//判定节点失效的时间间隔

	public String srcPort;//记录这条消息是从哪个端口发出去的
}
