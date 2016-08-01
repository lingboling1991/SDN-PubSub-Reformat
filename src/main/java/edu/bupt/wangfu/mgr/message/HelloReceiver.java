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
	private MultiHandler handler;

	public HelloReceiver() {
		String topic = WsnGlobleUtil.getSysTopicMap().get("hello");
		//这里先假设一个集群只有一个交换机
		for (String out : outPorts.keySet()) {
			Flow inFlow = FlowHandler.getInstance().generateFlow(localSwitch, out, portWsn2Swt, topic, 0, 1);
			//TODO out_port重复，流表会覆盖吗？如果会，那么这里就要注意是修改已有流表而不是新增一条，因为出端口都是wsn2swt，进端口会变多
			FlowHandler.downFlow(localCtl, inFlow, "update");
		}
		handler = new MultiHandler(uPort, topic);
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			MsgHello mh = (MsgHello) msg;
			onHello(mh);
		}
	}

	public void onHello(MsgHello mh) {//收到Hello消息，给予回复，回复类型为Hello_
		for (String out : outPorts.keySet()) {
			String topic = WsnGlobleUtil.getSysTopicMap().get("hello_");
			Flow outFlow = FlowHandler.getInstance().generateFlow(localSwitch, portWsn2Swt, out, topic, 0, 1);
			FlowHandler.downFlow(localCtl, outFlow, "update");

			replyHello(mh, topic);

			FlowHandler.deleteFlow(localCtl, outFlow);
		}
	}

	private void replyHello(MsgHello mh, String topic) {
		MsgHello_ reply = new MsgHello_();
		MultiHandler handler = new MultiHandler(uPort, topic);

		reply.srcSwitch = localSwitch;
		reply.srcGroup = groupName;

		reply.dstPort = mh.srcPort;
		reply.dstGroup = mh.indicator;

		handler.v6Send(reply);
	}
}