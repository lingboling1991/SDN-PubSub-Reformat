package test;

import edu.bupt.wangfu.opendaylight.MultiHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lenovo on 2016-6-23.
 */
public class Start {
	private static XTask xTask; //广播获取groupCtl消息的计时器
	private static Timer xTimer; //广播获取groupCtl消息的计时器
	private static int count = 1;

	public static void main(String[] args) {
		xTask = new XTask();
		xTimer = new Timer();
		xTimer.schedule(xTask, 0, 1500);
//		fuck();
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

	private static class XTask extends TimerTask {
		@Override
		public void run() {
			if (count % 3 == 0) {
				count++;
			} else {
				if (count == 10) {
					xTask.cancel();
					xTimer.cancel();
				}
				System.out.println("hahaha" + String.valueOf(count));
				count++;
			}
		}
	}
}
