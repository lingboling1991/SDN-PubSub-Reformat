package edu.bupt.wangfu.mgr.topology.rcver;

import edu.bupt.wangfu.info.device.Group;
import edu.bupt.wangfu.info.device.GroupLink;
import edu.bupt.wangfu.info.msg.Hello;
import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.mgr.topology.GroupUtil;
import edu.bupt.wangfu.opendaylight.MultiHandler;

/**
 * Created by lenovo on 2016-6-23.
 */
public class ReHelloReceiver extends SysInfo implements Runnable {
	private MultiHandler handler;

	public ReHelloReceiver() {
		handler = new MultiHandler(uPort, "re_hello", "sys");
	}

	@Override
	public void run() {
		while (true) {
			Object msg = handler.v6Receive();
			Hello re_hello = (Hello) msg;
			onReHello(re_hello);
		}
	}

	private void onReHello(Hello re_hello) {
		GroupLink gl = new GroupLink();
		gl.srcGroupName = re_hello.startGroup;
		gl.dstGroupName = re_hello.endGroup;
		gl.srcBorderSwtId = re_hello.startBorderSwtId;
		gl.srcOutPort = re_hello.startOutPort;
		gl.dstBorderSwtId = re_hello.endBorderSwtId;
		gl.dstOutPort = re_hello.endOutPort;
		neighborGroupLinks.add(gl);

		//同步LSDB，其他集群的连接情况
		//TODO 在最开始的时候，要把自己的group信息添加到allGroup里面
		allGroups = re_hello.allGroups;
		//TODO 对面集群情况的更新（也就是新增了自己这个邻居），等他自己flood来告知
		Group g = new Group(re_hello.endGroup);//自己这个集群
		g.updateTime = System.currentTimeMillis();
		g.subMap = groupSubMap;
		g.pubMap = groupPubMap;
		g.distances.put(re_hello.startGroup, 1);
		allGroups.put(g.groupName, g);

		GroupUtil.spreadNewLSA(g);

		handler = new MultiHandler(uPort, "hello", "sys");
		handler.v6Send(re_hello);//因为现在还在HeartMgr.SendTask()的sleep()中，因此直接发送就可以
	}
}