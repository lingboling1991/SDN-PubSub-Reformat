package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.mgr.message.DetectReceiver;
import edu.bupt.wangfu.mgr.message.HelloReceiver;
import edu.bupt.wangfu.mgr.message.Hello_Receiver;
import edu.bupt.wangfu.mgr.message.LSDBAnswer;
import edu.bupt.wangfu.mgr.topology.HeartMgr;
import edu.bupt.wangfu.mgr.topology.RouteMgr;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;

import java.util.HashSet;

/**
 * Created by lenovo on 2016-6-22.
 */
public class WsnMgr extends SysInfo {
	private static WsnMgr wsnMgr = new WsnMgr();
	private HeartMgr dt;//集群内检测模块

	private WsnMgr() {
		if (groupCtl.equals(localCtl)) {
			downRepFlow();
			RouteMgr.calOutPort2Wsn();
		}
		dt = new HeartMgr();
		dt.startSendTask();//这里只管发送，流表在configure()时已经下发了

		Thread helloRecv = new Thread(new HelloReceiver());
		Thread hello_Recv = new Thread(new Hello_Receiver());
		Thread detectRecv = new Thread(new DetectReceiver());
		Thread lsdbAnswer = new Thread(new LSDBAnswer());

		helloRecv.start();
		hello_Recv.start();
		detectRecv.start();
		lsdbAnswer.start();

		Thread askLsdb = new Thread(new LsdbAsker());
		askLsdb.start();
	}

	public static WsnMgr getInstance() {
		return wsnMgr;
	}

	private void downRepFlow() {
		HashSet<Switch> swts = getGroupOutSwts();
		for (Switch swt : swts) {
			FlowHandler.getInstance().generateFlow();
		}
	}

	private HashSet<Switch> getGroupOutSwts() {
		return new HashSet<>();
	}

	private class LsdbAsker implements Runnable {
		boolean needClear;
		boolean getOK;
		String askTopic;
		String replyTopic;
		MultiHandler sendHandler;
		MultiHandler recvHandler;

		private LsdbAsker() {
//			needClear = false;
//			getOK = false;
//
//			askTopic = WsnGlobleUtil.getSysTopicMap().get("askLSDB");
//			replyTopic = WsnGlobleUtil.getSysTopicMap().get("askLSDB_");
//
//			for (Broker gu : neighbors.values()) {
//				Flow inFlow = FlowHandler.getInstance().generateFlow(localSwtId, gu.localPort, portWsn2Swt, replyTopic, 0, 1);
//				FlowHandler.downFlow(localCtl, inFlow, "");
//			}
//
//			sendHandler = new MultiHandler(uPort, askTopic);
//			recvHandler = new MultiHandler(uPort, replyTopic);
		}

		@Override
		public void run() {
//			try {
//				Thread.sleep(sendPeriod * 2);//确保已经发出过hello消息
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			MsgAskForLSDB ask = new MsgAskForLSDB("ask for LSDB");
//
//			for (Broker gu : neighbors.values()) {
//				Flow outFlow = FlowHandler.getInstance().generateFlow(localSwtId, portWsn2Swt, gu.localPort, askTopic, 0, 1);
//				FlowHandler.downFlow(localCtl, outFlow, "");
//				sendHandler.v6Send(ask);
//				Object res = recvHandler.v6Receive();
//
//				getOK = SubMgr.initLSDB(res);
//
//				FlowHandler.deleteFlow(localCtl, outFlow);
//				if (getOK) {
//					break;
//				}
//			}


		}
	}
}
