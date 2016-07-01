package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;

/**
 * Created by lenovo on 2016-6-28.
 */
public class Hello_ implements Serializable {
	private static final long serialVersionUID = 1L;

	public String srcGroup;//发送者所在集群
	public String srcSwitch;//发送者连接的交换机odl_id

	public String dstPort;//目的节点使用的对外port
	public String dstGroup;//目的节点所在的集群
}
