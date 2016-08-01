package edu.bupt.wangfu.mgr.sub;

import edu.bupt.wangfu.info.msg.udp.LSA;
import edu.bupt.wangfu.info.msg.udp.LSDB;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.topology.TopoMgr;

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
			TopoMgr.calAllTopicRoute();//这里是计算全网订阅主题的路由

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
		String[] classify = topic.split(":");
		MsgSubsForm msf = groupTableRoot;
		ArrayList<String> changedTopics = new ArrayList<>();
		for (int i = 0; i < classify.length; i++) {
			if (msf.topicChildList.containsKey(classify[i])) {
				// if the group appears in the parent's subs, it won't appear in
				// the children's
				for (int j = 0; j < subs.size(); j++) {
					String sub = subs.get(j);
					if (msf.topicChildList.get(classify[i]).subs.contains(sub)) {
						subs.remove(sub);
						j--;
					}
				}
			} else {
				MsgSubsForm temp = new MsgSubsForm();
				temp.topicComponent = classify[i];
				msf.topicChildList.put(classify[i], temp);
			}
			if (subs.isEmpty())
				break;
			msf = msf.topicChildList.get(classify[i]);
		}
		boolean changed = false;
		for (int j = 0; j < subs.size(); j++) {
			String sub = subs.get(j);
			if (!msf.subs.contains(sub)) {
				msf.subs.add(sub);
				// 向下删除子节点中此订阅集群
				changed = true;
				for (String change : DeleteRepeatedSub(topic, msf, sub))
					if (!changedTopics.contains(change))
						changedTopics.add(change);
			}
		}
		if (changed) {
			changedTopics.add(topic);
		}
		return changedTopics;
	}

	// 以集群带一堆标题的方式加订阅
	public static ArrayList<String> addSubsByGroup(String group, ArrayList<String> sub1) {
		if (sub1 == null || sub1.isEmpty()) {
			return null;
		}
		ArrayList<String> ts = new ArrayList<String>();
		ts.add(group);
		ArrayList<String> changedTopics = new ArrayList<String>();
		for (String topic : sub1)
			changedTopics.addAll(addSubsByTopic(topic, ts));
		return changedTopics;
	}

	private static class SyncLSATask extends TimerTask {
		@Override
		public void run() {
			for (String:
					) {

			}
			if (!cacheLSA.dbns.containsKey(neighbor)) {
				cacheLSA.dbns.put(neighbor, new DistBtnNebr(1));
			}
		}
	}
}
