package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.msg.udp.GroupUnit;
import edu.bupt.wangfu.info.msg.udp.MsgAskForLSDB;
import edu.bupt.wangfu.info.msg.udp.MsgDetectGroupCtl_;
import edu.bupt.wangfu.mgr.message.DetectReceiver;
import edu.bupt.wangfu.mgr.message.HelloReceiver;
import edu.bupt.wangfu.mgr.message.Hello_Receiver;
import edu.bupt.wangfu.mgr.topology.DtMgr;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

/**
 * Created by lenovo on 2016-6-22.
 */
public class RtMgr extends SysInfo {
	private static RtMgr rt = new RtMgr();
	private DtMgr dt;//集群内检测模块

	private RtMgr() {
		dt = new DtMgr();
		dt.startSendTask();//这里只管发送，流表在configure()时已经下发了

		Thread helloRecv = new Thread(new HelloReceiver());
		Thread hello_Recv = new Thread(new Hello_Receiver());
		Thread detectRecv = new Thread(new DetectReceiver());

		helloRecv.start();
		hello_Recv.start();
		detectRecv.start();

		Thread askLsdb = new Thread(new LsdbAsker());
	}

	public static RtMgr getInstance() {
		return rt;
	}

	private class LsdbAsker implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(sendPeriod * 2);//确保已经发出过hello消息
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//TODO 向每个neighbor发送请求，请求一个就receive阻塞
			MsgAskForLSDB ask = new MsgAskForLSDB();
			String topic = WsnGlobleUtil.getSysTopicMap().get("askLSDB");
			MultiHandler handler = new MultiHandler(uPort, topic);

			for (GroupUnit gu : neighbors.values()) {
				Flow flow = FlowHandler.getInstance().generateFlow(localSwitch, portWsn2Swt, gu.localPort, topic, 0, 1);
				FlowHandler.downFlow(localCtl, flow, "");
			}
			handler.v6Send(ask);

			Object res = handler.v6Receive();
			MsgDetectGroupCtl_ mdgc_ = (MsgDetectGroupCtl_) res;
			if (mdgc_.groupName.equals(groupName)) {
				groupCtl = ((MsgDetectGroupCtl_) res).groupCtl;
			}
		}
	}
}
