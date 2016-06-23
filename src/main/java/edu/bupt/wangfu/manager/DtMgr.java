package edu.bupt.wangfu.manager;

import edu.bupt.wangfu.base.Dt;
import edu.bupt.wangfu.info.msg.udp.MsgHello;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lenovo on 2016-6-22.
 */
public class DtMgr implements Dt {
	private RtMgr rtMgr;//调度模块
	private Timer timer;//计时器

	private long threshold;//失效阀值的缺省值
	private long sendPeriod;//发送频率的缺省值
	private SendTask sendTask; //发送hello消息的计时器

	private List<String> neighbors;//Hello信息表，记录邻居集群

	private Timer timerForHellos; //为邻居们记录hello消息的计时器
	private LostTask lostTask[]; //当邻居丢失时需要发生的动作
	private ConcurrentHashMap<String, Integer> tblOfNbr; //记录邻居集群名称到他们所在LostTask项的对应
	private List<Integer> avlbNum; //记录可用的losttask坐标

	public DtMgr(RtMgr rtMgr) {
		this.rtMgr = rtMgr;
		neighbors = new ArrayList<>();
		timerForHellos = new Timer();

	}

	//邻居超时未回复，后面要采取的行动
	class LostTask extends TimerTask {
		String groupName;

		public LostTask(String groupName) {
			this.groupName = groupName;
		}

		@Override
		public void run() {
			removeTarget(groupName);
			rtMgr.lost(groupName);
		}
	}

	//向节点的邻居发送hello消息
	class SendTask extends TimerTask {
		@Override
		public void run() {
			sendAction();
		}
	}

	private void sendAction() {
		//lcw 发MsgHello
		MsgHello hello = new MsgHello();
		String addr = WsnGlobleUtil.getSysTopicMap().get("wsn2out_hello");
		MultiHandler handler = new MultiHandler(rtMgr.getuPort(), addr);


		hello.indicator = rtMgr.getLocalAddr();

		hello.helloInterval = sendPeriod;
		hello.deadInterval = threshold;

		// sent to other groups
		handler.v6Send(hello);
		//TODO lcw 这里不需要给集群内发hello消息了，直接轮询控制器就可以
	}
}
