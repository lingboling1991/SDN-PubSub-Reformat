package edu.bupt.wangfu.mgr.topology.rcver;

import edu.bupt.wangfu.info.device.OuterGroup;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.Hello;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.MultiHandler;

/**
 * Created by lenovo on 2016-6-23.
 */
public class HelloReceiver extends SysInfo implements Runnable {
	private MultiHandler handler;

	public HelloReceiver() {
		handler = new MultiHandler(uPort, "hello", "sys");
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			Hello mh = (Hello) msg;

			try {
				onHello(mh);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void onHello(Hello mh) throws InterruptedException {
		if (mh.endGroup.equals(groupName)) {
			//第三次握手，携带这个跨集群连接的全部信息
			new Thread(new ReReHello(mh)).start();
		} else {
			//第一次握手，只携带发起方的信息，需要补完接收方的信息，也就是当前节点
			new Thread(new ReHello(mh)).start();
		}
	}

	private class ReHello implements Runnable {
		Hello re_hello;

		ReHello(Hello mh) {
			Hello re_hello = new Hello();

			re_hello.startGroup = mh.startGroup;
			re_hello.endGroup = groupName;
			re_hello.startBorderSwtId = mh.startBorderSwtId;
			re_hello.startOutPort = mh.startOutPort;
			re_hello.reHelloPeriod = mh.reHelloPeriod;

			this.re_hello = re_hello;
		}

		@Override
		public void run() {
			for (Switch swt : outSwitchs) {
				for (String out : swt.portSet) {
					if (!out.equals("LOCAL")) {
						re_hello.endBorderSwtId = swt.id;
						re_hello.endOutPort = out;

						//依次发送re_hello到每一个outPort，中间的时延保证对面有足够的时间反应第一条收到的信息
						MultiHandler handler = new MultiHandler(uPort, "re_hello", "sys");
						handler.v6Send(re_hello);

						try {
							Thread.sleep(re_hello.reHelloPeriod);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private class ReReHello implements Runnable {
		Hello finalHello;

		ReReHello(Hello mh) {
			this.finalHello = mh;
		}

		@Override
		public void run() {
			//这里存的和最早发出hello信息的那边，顺序正好相反
			OuterGroup g = new OuterGroup();
			g.outerGroupName = finalHello.startGroup;
			g.srcBorderSwtId = finalHello.endBorderSwtId;
			g.srcOutPort = finalHello.endOutPort;
			g.dstBorderSwtId = finalHello.startBorderSwtId;
			g.dstOutPort = finalHello.startOutPort;
			outerGroups.add(g);
		}
	}
}