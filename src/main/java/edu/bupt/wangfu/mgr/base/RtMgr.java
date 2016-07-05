package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.mgr.message.DetectReceiver;
import edu.bupt.wangfu.mgr.message.HelloReceiver;
import edu.bupt.wangfu.mgr.message.Hello_Receiver;
import edu.bupt.wangfu.mgr.topology.DtMgr;

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
	}

	public static RtMgr getInstance() {
		return rt;
	}
}
