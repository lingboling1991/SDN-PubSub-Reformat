package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

/**
 * Created by lenovo on 2016-6-23.
 */
public class ReplyReceiver extends SysInfo implements Runnable {
	private MultiHandler multiHandler;
	private MsgHandler msgHandler;

	public ReplyReceiver() {
		String v6addr = WsnGlobleUtil.getSysTopicMap().get("hello_");
		multiHandler = new MultiHandler(uPort, v6addr);
		msgHandler = MsgHandler.getInstance();
	}

	@Override
	public void run() {
		while (true) {
			Object msg = multiHandler.v6Receive();
			//收到邻居回复的消息
			msgHandler.processUdpMsg(msg);
		}
	}
}