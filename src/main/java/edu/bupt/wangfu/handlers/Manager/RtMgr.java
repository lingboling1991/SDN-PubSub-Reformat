package edu.bupt.wangfu.handlers.Manager;

/**
 * Created by lenovo on 2016-6-22.
 */
public class RtMgr {
	private static RtMgr INSTANCE = new RtMgr();

	public static RtMgr getInstance() {
		return INSTANCE;
	}

	private RtMgr() {

	}
}
