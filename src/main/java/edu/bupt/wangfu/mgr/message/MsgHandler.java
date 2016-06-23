package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.mgr.base.RtMgr;
import edu.bupt.wangfu.info.msg.udp.GroupUnit;
import edu.bupt.wangfu.info.msg.udp.MsgHello;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;
import edu.bupt.wangfu.mgr.topology.DtMgr;

/**
 * Created by lenovo on 2016-6-23.
 */
public class MsgHandler {
	private static DtMgr dt;
	private static RtMgr rt;

	public MsgHandler(DtMgr dt, RtMgr rt) {
		this.dt = dt;
		this.rt = rt;
	}

	public static void processUdpMsg(Object msg) {
		if (msg instanceof MsgHello) {
			MsgHello mh = (MsgHello) msg;
//			dt.onMsg(mh);

			//TODO 收到MsgHello，记录邻居情况，记录到groups中，后面在join()中用来向其索取LSDB
			WsnGlobleUtil.groups.put(mh.indicator, new GroupUnit());
			//TODO 新添邻居
//			this.addNeighbor();

		}
	}
}
