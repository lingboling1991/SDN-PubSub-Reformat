package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.mgr.message.HelloReceiver;
import edu.bupt.wangfu.mgr.message.MsgHandler;
import edu.bupt.wangfu.mgr.message.ReplyReceiver;
import edu.bupt.wangfu.mgr.topology.DtMgr;

/**
 * Created by lenovo on 2016-6-22.
 */
public class RtMgr extends SysInfo {
	private static RtMgr rt = new RtMgr();
	private DtMgr dt;//集群内检测模块
	private MsgHandler msgHandler;
	private Thread hrt;//监听其他节点发来的hello消息
	private Thread rrt;//监听其他节点回复的hello_消息

	private RtMgr() {
		dt = new DtMgr(this);
		dt.startSendTask();//这里只管发送，流表在configure()时已经下发了

		MsgHandler.getInstance().init(dt, this);

		hrt = new Thread(new HelloReceiver());//因为v6组播不同消息地址不同，所以这里需要每个功能起一个线程（如果必要的话）
		rrt = new Thread(new ReplyReceiver());//因为v6组播不同消息地址不同，所以这里需要每个功能起一个线程（如果必要的话）

		hrt.start();
		rrt.start();
	}

	public static RtMgr getInstance() {
		return rt;
	}
}
