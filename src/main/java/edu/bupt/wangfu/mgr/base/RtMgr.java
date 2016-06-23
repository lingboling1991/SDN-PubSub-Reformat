package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.mgr.topology.DtMgr;

/**
 * Created by lenovo on 2016-6-22.
 */
public class RtMgr extends SysInfo {
	private static RtMgr rt = new RtMgr();
	private DtMgr dt;//集群内检测模块
	private Thread umt;//监听udp消息的线程
	private Thread tcr;//拓扑探测消息监听

	private RtMgr() {
		dt = new DtMgr(this);
		dt.startSendTask();//这里只管发送，流表在configure()时已经下发了

//		umt = new Thread(new UdpMsgThread(this));//监听UDP消息，其中就包括群内广播的消息
//		tcr = new Thread(new HelloReceiver(this));//因为v6组播不同消息地址不同，所以这里需要每个功能起一个线程（如果必要的话）
//
//		umt.start();
//		tcr.start();

	}

	public static RtMgr getInstance() {
		return rt;
	}
}
