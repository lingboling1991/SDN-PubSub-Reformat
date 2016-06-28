package test;

import edu.bupt.wangfu.opendaylight.MultiHandler;

/**
 * Created by lenovo on 2016-6-23.
 */
public class Start {
	public static void main(String[] args) {
		fuck();
	}

	public static void fuck() {
		Config.config();
		new Thread(new mgrInstance()).start();
	}

	private static class mgrInstance implements Runnable {
		public void run() {
//			X.getInstance();//让RtMgr的实例一直存活在这个线程中
			MultiHandler a = new MultiHandler(1234, "FF01:0000:0000:0000:0001:2345:6789:abcd");
			a.v6Receive();
			MultiHandler b = new MultiHandler(1234, "FF01:0000:0000:0000:0001:2345:6789:efgh");
			b.v6Receive();

		}
	}
}
