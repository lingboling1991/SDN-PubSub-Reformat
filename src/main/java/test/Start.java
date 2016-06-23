package test;

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
			X.getInstance();//让RtMgr的实例一直存活在这个线程中
			System.out.println("getting instance");
		}
	}
}
