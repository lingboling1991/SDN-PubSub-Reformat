package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.msg.udp.MsgHello;
import edu.bupt.wangfu.info.msg.udp.MsgHello_;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

/**
 * Created by lenovo on 2016-6-23.
 */
public class HelloReceiver extends SysInfo implements Runnable {
	private MultiHandler multiHandler;

	public HelloReceiver() {
		String v6addr = WsnGlobleUtil.getSysTopicMap().get("hello");
		multiHandler = new MultiHandler(uPort, v6addr);
	}

	@Override
	public void run() {
		while (true) {
			Object msg = multiHandler.v6Receive();
			MsgHello mh = (MsgHello) msg;
			onHello(mh);
		}
	}

	public void onHello(MsgHello mh) {
		for (String port : outPorts.keySet()) {
			Flow flow = FlowHandler.getInstance().generateFlow(localSwitch, portWsn2Swt, port,
					WsnGlobleUtil.getSysTopicMap().get("hello_"), 0, 1);
			FlowHandler.downFlow(localCtl, flow, "update");

			replyHello(mh);

			FlowHandler.deleteFlow(localCtl, flow);
		}
	}

	private void replyHello(MsgHello mh) {
		MsgHello_ reply = new MsgHello_();
		String addr = WsnGlobleUtil.getSysTopicMap().get("hello_");
		MultiHandler handler = new MultiHandler(uPort, addr);

		reply.srcSwitch = localSwitch;
		reply.srcGroup = groupName;

		reply.dstPort = mh.srcPort;
		reply.dstGroup = mh.indicator;

		handler.v6Send(reply);
	}
}