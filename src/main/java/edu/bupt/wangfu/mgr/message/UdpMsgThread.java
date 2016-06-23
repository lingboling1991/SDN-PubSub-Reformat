package edu.bupt.wangfu.mgr.message;

import edu.bupt.wangfu.mgr.base.RtMgr;
import edu.bupt.wangfu.mgr.base.SysInfo;

public class UdpMsgThread extends SysInfo implements Runnable {
	private RtMgr rtMgr;

	UdpMsgThread(RtMgr rtMgr) {
//		this.rtMgr = rtMgr;
//		try {
//			MultiHandler handler=new MultiHandler(rtMgr.getuPort(),)
//			s = new MulticastSocket(new InetSocketAddress(localAddr, uPort));
//			s.joinGroup(InetAddress.getByName(multiAddr));
//			s.setLoopbackMode(true);
//			s.setReceiveBufferSize(1024 * 1024);
//
//			p = new DatagramPacket(buf, buf.length);
//		} catch (IOException e) {
//			e.printStackTrace();
//			log.warn(e);
//		}

	}

	public void run() {
//		while (udpMsgThreadSwitch) {
//			try {
//				bais = new ByteArrayInputStream(buf);
//				s.receive(p);
//				ois = new ObjectInputStream(bais);
//				Object msg = ois.readObject();
//				rtMgr.getState().processUdpMsg(msg);
//			} catch (IOException e) {
//				log.warn(e);
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//				log.warn(e);
//			}
//		}
	}
}
