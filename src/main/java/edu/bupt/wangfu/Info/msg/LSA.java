package edu.bupt.wangfu.info.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
//TODO 这个破玩意儿要时刻广播！！！
public class LSA implements Serializable {
	/**
	 * 链路状态消息
	 */
	private static final long serialVersionUID = 1L;
	public int seqNum; // 序列号
	public int syn; // 0为普通LSA，1为同步LSA
	public String originator; // 发送源名称
	public String originAddr; // 发送源代表地址
	public ArrayList<String> lostGroup; // 丢失集群，若无丢失则为空，更新outGroups后需要看是否有丢失
	public ArrayList<String> subsTopics; // 发送源的订阅
	public ArrayList<String> cancelTopics; // 发送源取消的订阅
	public ConcurrentHashMap<String, Integer> distBtnNebrs; // 发送源与邻居的距离
	public long sendTime; // 发送时间

	public LSA() {
		lostGroup = new ArrayList<>();
		subsTopics = new ArrayList<>();
		cancelTopics = new ArrayList<>();
		distBtnNebrs = new ConcurrentHashMap<>();
	}

	public void copyLSA(LSA lsa) {
		this.seqNum = lsa.seqNum;
		this.syn = lsa.syn;
		this.originator = lsa.originator;
		this.originAddr = lsa.originAddr;
		this.lostGroup.addAll(lsa.lostGroup);
		this.subsTopics.addAll(lsa.subsTopics);
		this.cancelTopics.addAll(lsa.cancelTopics);
		this.distBtnNebrs.putAll(lsa.distBtnNebrs);
	}

	public void copyPartLSA(LSA lsa) {
		this.seqNum = lsa.seqNum;
		this.syn = lsa.syn;
		this.originator = lsa.originator;
		this.originAddr = lsa.originAddr;
		this.lostGroup.addAll(lsa.lostGroup);
		this.distBtnNebrs.putAll(lsa.distBtnNebrs);
	}
}
