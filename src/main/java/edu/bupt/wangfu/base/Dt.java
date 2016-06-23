package edu.bupt.wangfu.base;

public interface Dt {

	//DtMgr主要作用是发送心跳，这个target就是目的地址集合
	public void addTarget(String indicator);

	//remove the detection of a neighbor
	public void removeTarget(String indicator);

	public void onMsg(Object msg);
}
