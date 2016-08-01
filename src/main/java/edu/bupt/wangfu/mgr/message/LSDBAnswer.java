package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.msg.udp.Broker;
import edu.bupt.wangfu.info.msg.udp.LSDB;
import edu.bupt.wangfu.info.msg.udp.MsgAskForLSDB;
import edu.bupt.wangfu.mgr.base.RtMgr;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

/**
 * Created by lenovo on 2016-6-23.
 */
public class LSDBAnswer extends SysInfo implements Runnable {
	private MultiHandler handler;

	public LSDBAnswer() {
		//接收MsgAskForLSDB消息的流表
		String askTopic = WsnGlobleUtil.getSysTopicMap().get("askLSDB");
		String replyTopic = WsnGlobleUtil.getSysTopicMap().get("askLSDB_");
		for (Broker gu : neighbors.values()) {
			Flow inFlow = FlowHandler.getInstance().generateFlow(localSwitch, gu.localPort, portWsn2Swt, askTopic, 0, 1);
			Flow outFlow = FlowHandler.getInstance().generateFlow(localSwitch, portWsn2Swt, gu.localPort, replyTopic, 0, 1);
			FlowHandler.downFlow(localCtl, inFlow, "");
			FlowHandler.downFlow(localCtl, outFlow, "");
		}

		handler = new MultiHandler(uPort, askTopic);
	}

	@Override
	public void run() {
		while (true) {
			Object res = handler.v6Receive();
			if (res instanceof MsgAskForLSDB) {
				LSDB reply = new LSDB();
				reply.lsdb.addAll(RtMgr.lsdb.values());
				handler.v6Send(reply);
			}
		}
	}
}