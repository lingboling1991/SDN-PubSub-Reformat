package edu.bupt.wangfu.manager;

import edu.bupt.wangfu.base.Dt;
import edu.bupt.wangfu.base.SysInfo;

import java.util.concurrent.ExecutorService;

/**
 * Created by lenovo on 2016-6-22.
 */
public class RtMgr extends SysInfo {
	private Configuration configuration;//配置系统
	public static ExecutorService pool;// 处理wsn的线程池
	private static int count;// 允许处理“上交WSN”的并发线程数目
	private static int poolLimit;// 线程池容量//多次声明的问题//注意编码层次
	private static RtMgr INSTANCE = new RtMgr();
	private static Object routeObject;
	private Dt dt;// 集群内检测模块
	private Thread tmt;// 监听tcp连接的线程
	private Thread umt;// 监听udp消息的线程
	private Thread tcr;// 拓扑探测消息监听
	private Thread topoCheck;//定时发送hello消息探测邻居情况

	private RtMgr() {
		dt = new DtMgr(this);

		configuration = new Configuration(this);
	}

	public static RtMgr getInstance() {
		return INSTANCE;
	}
}
