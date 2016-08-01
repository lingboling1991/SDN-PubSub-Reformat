package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.info.msg.udp.MsgDetectGroupCtl;
import edu.bupt.wangfu.info.msg.udp.MsgDetectGroupCtl_;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

/**
 * Created by lenovo on 2016-6-23.
 */
public class DetectReceiver extends SysInfo implements Runnable {
	private MultiHandler handler;

	public DetectReceiver() {
		String topic = WsnGlobleUtil.getSysTopicMap().get("groupCtl");
		handler = new MultiHandler(uPort, topic);
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			MsgDetectGroupCtl mdgc = (MsgDetectGroupCtl) msg;
			if (mdgc.indicator.equals(groupName) && groupCtl != null) {
				MsgDetectGroupCtl_ mdgc_ = new MsgDetectGroupCtl_(groupName, groupCtl);
				handler.v6Send(mdgc_);
			}
		}
	}
}