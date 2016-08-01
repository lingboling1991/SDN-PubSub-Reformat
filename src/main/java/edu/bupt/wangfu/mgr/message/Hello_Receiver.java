package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.info.device.Flow;
import edu.bupt.wangfu.info.device.Port;
import edu.bupt.wangfu.info.msg.udp.Broker;
import edu.bupt.wangfu.info.msg.udp.MsgHello_;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.opendaylight.FlowHandler;
import edu.bupt.wangfu.opendaylight.MultiHandler;
import edu.bupt.wangfu.opendaylight.WsnGlobleUtil;

/**
 * Created by lenovo on 2016-6-23.
 */
public class Hello_Receiver extends SysInfo implements Runnable {
	private MultiHandler handler;

	public Hello_Receiver() {//等待Hello_消息
		String topic = WsnGlobleUtil.getSysTopicMap().get("hello_");
		for (String out : outPorts.keySet()) {
			Flow inFlow = FlowHandler.getInstance().generateFlow(localSwitch, out, portWsn2Swt, topic, 0, 1);
			FlowHandler.downFlow(localCtl, inFlow, "update");
		}
		handler = new MultiHandler(uPort, topic);
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			MsgHello_ mh_ = (MsgHello_) msg;
			onReply(mh_);
		}
	}

	private void onReply(MsgHello_ mh_) {
		//这条消息是针对 本集群groupName 的 对外端口dstPort 的回复
		//TODO 这里有个问题，可能不可能一个集群中有两个交换机，暴露了同样的对外端口，所以每个交换机应该有唯一标识，这样就不存在集群这个概念了
		if (groupName.equals(mh_.dstGroup) && outPorts.keySet().contains(mh_.dstPort)) {
			//更新邻居信息
			if (!neighbors.containsKey(mh_.dstPort)) {
				Broker broker = new Broker(mh_.srcGroup, mh_.dstPort);
				neighbors.put(mh_.dstPort, broker);
			}
			//更新对外端口信息
			Port port = outPorts.get(mh_.dstPort);
			if (port.getRemoteSwitchId() == null) {
				port.setRemoteSwitchId(mh_.srcSwitch);
				outPorts.put(mh_.dstPort, port);
			}
		}
		Thread lost = new Thread(new LostTask(System.currentTimeMillis()));
		lost.start();
	}

	private class LostTask implements Runnable {
		//收到任意一个端口的回复，就启动这个线程，检查所有outports，看是否有端口超时
		long curTime;

		LostTask(long curTime) {
			this.curTime = curTime;
		}

		@Override
		public void run() {
			for (Port port : outPorts.values()) {
				if (curTime - port.getLastUse() >= threshold) {
					neighbors.remove(port.getPort());
				} else {
					port.setLastUse(curTime);//这样应该修改的是neighbors里面的那个port对象
				}
			}
		}
	}
}