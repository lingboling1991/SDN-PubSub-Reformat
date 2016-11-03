package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.msg.udp.Route;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.MultiHandler;

/**
 * Created by lenovo on 2016-10-27.
 */
public class SyncRouteReceiver extends SysInfo implements Runnable {
	private MultiHandler handler;

	public SyncRouteReceiver() {
		handler = new MultiHandler(uPort, "route", "sys");
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			Route r = (Route) msg;
			new Thread(new SyncRouteHandler(r)).start();
		}
	}

	private class SyncRouteHandler implements Runnable {
		private Route route;

		SyncRouteHandler(Route r) {
			this.route = r;
		}

		@Override
		public void run() {
			groupRoutes.add(route);//在初始化groupRoutes时，已经确保过线程安全了
		}
	}
}