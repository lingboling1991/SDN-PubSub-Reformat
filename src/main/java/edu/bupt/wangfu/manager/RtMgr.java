package edu.bupt.wangfu.manager;

/**
 * Created by lenovo on 2016-6-22.
 */
public class RtMgr extends SysInfo{
	private Configuration configuration;// 配置系统


	private static RtMgr INSTANCE = new RtMgr();

	public static RtMgr getInstance() {
		return INSTANCE;
	}

	private RtMgr() {
		dt = new DtMgr(this);

		ir = new Router();

		configuration = new Configuration(this);
	}
}
