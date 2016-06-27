package edu.bupt.wangfu.mgr.topology;

import edu.bupt.wangfu.info.msg.udp.MsgHello;
import edu.bupt.wangfu.mgr.base.RtMgr;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lenovo on 2016-6-22.
 */
public class DtMgr extends SysInfo {
	private RtMgr rtMgr;

	private long threshold;//失效阀值的缺省值
	private long sendPeriod;//发送频率的缺省值
	private SendTask sendTask; //发送hello消息的计时器

	private Timer helloTimer; //hello消息的计时器
	private Timer lostTimer;//邻居丢失计时器

	//丢失处理
	private LostTask[] lostTask; // 当邻居丢失时需要发生的动作
	private ConcurrentHashMap<String, Integer> nbName2index; // 记录邻居集群名称到他们所在LostTask项的对应
	private List<Integer> avlNum; // 记录可用的losttask坐标

	public DtMgr(RtMgr rtMgr) {
		this.rtMgr = rtMgr;
		neighbors = new ArrayList<>();
		helloTimer = new Timer();

		//lcw 这里可以变成从管理员读取，那么就需要向管理员请求信息
		Properties props = new Properties();
		String propertiesPath = "DtConfig.properties";
		try {
			props.load(new FileInputStream(propertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		threshold = Long.parseLong(props.getProperty("threshold"));//判断失效阀值
		sendPeriod = Long.parseLong((props.getProperty("sendPeriod")));//发送周期
	}

	public void onMsg(MsgHello mh) {
		this.addTarget(mh.indicator);
	}

	public void addTarget(String indicator) {
		Integer index;

		if (!neighbors.contains(indicator)) {//如果是重复接收的hello，就不需要再添加neighbor了
			neighbors.add(indicator);
		}

		if (nbName2index.containsKey(indicator)) {//发来hello消息的集群已经在执行着losttask了
			lostTask[nbName2index.get(indicator)].cancel();//重启losttask
			index = nbName2index.get(indicator);
		} else {
			index = avlNum.get(0);
			nbName2index.put(indicator, index);
			avlNum.remove(index);
		}
		lostTask[index] = new LostTask(indicator);
		lostTimer.schedule(lostTask[index], threshold);
	}

	public void removeTarget(String indicator) {
		if (neighbors.contains(indicator)) {
			neighbors.remove(indicator);
		}

		if (nbName2index.containsKey(indicator)) {
			Integer index = nbName2index.get(indicator);
			if (lostTask[index] != null)
				lostTask[index].cancel();
			nbName2index.remove(indicator);
			avlNum.add(index);
		}

	}

	public void startSendTask() {
		if (sendTask != null)
			sendTask.cancel();
		sendTask = new SendTask();
		helloTimer.schedule(sendTask, sendPeriod, sendPeriod);
	}

	private void sendAction() {
		//发MsgHello
		MsgHello hello = new MsgHello();
		String addr = WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello");
		MultiHandler handler = new MultiHandler(rtMgr.getuPort(), addr);

		hello.indicator = rtMgr.getLocalAddr();

		hello.helloInterval = sendPeriod;
		hello.deadInterval = threshold;

		//发给对外的端口
		handler.v6Send(hello);
		//TODO 这里不需要给集群内发hello消息了，直接轮询控制器就可以
	}

	//邻居超时未回复，后面要采取的行动
	private class LostTask extends TimerTask {
		String groupName;

		LostTask(String groupName) {
			this.groupName = groupName;
		}

		@Override
		public void run() {
			removeTarget(groupName);
//			rtMgr.lost(groupName);
		}
	}

	//向节点的邻居发送hello消息
	private class SendTask extends TimerTask {
		@Override
		public void run() {
			sendAction();
		}
	}


}
