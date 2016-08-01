package edu.bupt.wangfu;

import edu.bupt.wangfu.mgr.base.Config;
import edu.bupt.wangfu.mgr.base.RtMgr;

/**
 * Created by LCW on 2016-6-19.
 */
public class Start {
	public static void main(String[] args) {
		System.out.println("config start");
		Config.configure();//这里进行配置，配置文件的内容写到SysInfo里
		System.out.println("config done");

		System.out.println("mgr thread start");
		new Thread(new mgrInstance()).start();
	}

	private static class mgrInstance implements Runnable {
		public void run() {
			RtMgr.getInstance();
		}
	}
}
