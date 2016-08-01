package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lenovo on 2016-6-22.
 */
public class LSA implements Serializable {
	private static final long serialVersionUID = 1L;

	public int seqNum; // 序列号
	public int syn; // 0为普通LSA，1为同步LSA
	public String originator; // 发送源名称
	public ArrayList<String> lostGroup; // 丢失集群，若无丢失则为空
	public ArrayList<String> subsTopics; // 发送源的订阅
	public ArrayList<String> cancelTopics; //发送源取消的订阅
	public ConcurrentHashMap<String, DistanceBetweenNeighbor> dbns; // 集群及其邻居
	public long sendTime; //发送时间
}
