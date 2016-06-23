package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.mgr.base.RtMgr;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

/**
 * Created by lenovo on 2016-6-23.
 */
public class HelloReceiver extends SysInfo implements Runnable {
	private MultiHandler handler;
	private RtMgr rtMgr;

	HelloReceiver(RtMgr rtMgr) {
		this.rtMgr = rtMgr;
		String addr = WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello");
		handler = new MultiHandler(uPort, addr);
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			//lcw 收到邻居回复的消息
			MsgHandler.processUdpMsg(msg);
		}
	}
}