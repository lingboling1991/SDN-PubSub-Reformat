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

	private RtMgr() {
		dt = new DtMgr(this);
		dt.startSendTask();//这里只管发送，流表在configure()时已经下发了

		MsgHandler.getInstance().init(dt, this);

		Thread hrt = new Thread(new HelloReceiver());
		Thread rrt = new Thread(new ReplyReceiver());

		hrt.start();
		rrt.start();
	}

	public static RtMgr getInstance() {
		return rt;
	}
}
