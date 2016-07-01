package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

/**
 * Created by lenovo on 2016-6-23.
 */
public class HelloReceiver extends SysInfo implements Runnable {
	private MultiHandler multiHandler;
	private MsgHandler msgHandler;

	public HelloReceiver() {
		String v6addr = WsnGlobleUtil.getSysTopicMap().get("hello");
		multiHandler = new MultiHandler(uPort, v6addr);
		msgHandler = MsgHandler.getInstance();
	}

	@Override
	public void run() {
		while (true) {
			Object msg = multiHandler.v6Receive();
			//收到邻居发来的hello消息
			msgHandler.processUdpMsg(msg);
		}
	}
}