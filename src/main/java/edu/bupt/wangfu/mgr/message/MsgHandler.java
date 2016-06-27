package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.info.msg.udp.MsgHello;
import edu.bupt.wangfu.mgr.base.RtMgr;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.topology.DtMgr;

/**
 * Created by lenovo on 2016-6-23.
 */
public class MsgHandler extends SysInfo {
	private static MsgHandler INSTANCE;
	private DtMgr dt;
	private RtMgr rt;

	private MsgHandler() {
	}

	public static MsgHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new MsgHandler();
		return INSTANCE;
	}

	public void init(DtMgr dt, RtMgr rt) {
		this.dt = dt;
		this.rt = rt;
	}

	public void processUdpMsg(Object msg) {
		if (msg instanceof MsgHello) {
			MsgHello mh = (MsgHello) msg;
			dt.onMsg(mh);
		}
	}
}
