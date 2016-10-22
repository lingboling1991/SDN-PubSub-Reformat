package edu.bupt.wangfu.mgr.sub;

import edu.bupt.wangfu.info.msg.udp.LSA;
import edu.bupt.wangfu.info.msg.udp.LSDB;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.topology.RouteMgr;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LCW on 2016-7-19.
 */
public class SubMgr extends SysInfo {
	private static SyncLSATask syncLSATask;
	private static Timer syncLSATimer;

	public static boolean initLSDB(Object res) {
		boolean getOK = false;
		if (res instanceof LSDB) {
			for (LSA l : ((LSDB) res).lsdb) {
				l.sendTime = System.currentTimeMillis();
				addSubsByGroup(l.originator, l.subsTopics);//TODO 这里的逻辑是怎样的
				lsdb.put(l.originator, l);
			}
			//TODO 这里原来有个needClear，不知道什么意思，先删去
			RouteMgr.calAllTopicRoute();//这里是计算全网订阅主题的路由

			//开始LSA同步任务
			syncLSATask = new SyncLSATask();
			syncLSATimer = new Timer();
			syncLSATimer.schedule(syncLSATask, 5000);

			getOK = true;
		}
		return getOK;
	}

	// 以标题带一堆集群的形式加订阅
	public static ArrayList<String> addSubsByTopic(String topic, ArrayList<String> subs) {
		ArrayList<String> changedTopics = new ArrayList<>();

		return changedTopics;
	}

	// 以集群带一堆标题的方式加订阅
	public static ArrayList<String> addSubsByGroup(String group, ArrayList<String> sub1) {
		ArrayList<String> changedTopics = new ArrayList<>();

		return changedTopics;
	}

	private static class SyncLSATask extends TimerTask {
		@Override
		public void run() {
			/*for (String:
					) {

			}
			if (!cacheLSA.dbns.containsKey(neighbor)) {
				cacheLSA.dbns.put(neighbor, new DistBtnNebr(1));
			}*/
		}
	}
}
