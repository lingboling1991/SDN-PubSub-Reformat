package edu.bupt.wangfu.mgr.topology.rcver;

import edu.bupt.wangfu.info.device.OuterGroup;
import edu.bupt.wangfu.info.msg.Hello;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.MultiHandler;

/**
 * Created by lenovo on 2016-6-23.
 */
public class ReHelloReceiver extends SysInfo implements Runnable {
	private MultiHandler handler;

	public ReHelloReceiver() {
		handler = new MultiHandler(uPort, "re_hello", "sys");
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			Hello re_hello = (Hello) msg;
			onReHello(re_hello);
		}
	}

	private void onReHello(Hello re_hello) {
		OuterGroup g = new OuterGroup();
		g.outerGroupName = re_hello.endGroup;
		g.srcBorderSwtId = re_hello.startBorderSwtId;
		g.srcOutPort = re_hello.startOutPort;
		g.dstBorderSwtId = re_hello.endBorderSwtId;
		g.dstOutPort = re_hello.endOutPort;
		outerGroups.add(g);

		handler = new MultiHandler(uPort, "hello", "sys");
		handler.v6Send(re_hello);//因为现在还在HeartMgr.SendTask()的sleep()中，因此直接发送就可以
	}
}