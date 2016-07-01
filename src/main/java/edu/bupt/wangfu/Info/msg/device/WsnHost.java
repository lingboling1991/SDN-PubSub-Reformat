package edu.bupt.wangfu.info.msg.device;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-5.
 */
public class WsnHost extends DevInfo {
	private String ip;
	private String mac = null;
	private Map<String, List<String>> subers = new ConcurrentHashMap<String, List<String>>();

	public WsnHost(String ip) {
		this.ip = ip;
	}

	public static void main(String[] args) {
		WsnHost x = new WsnHost("10.108.166.15");
		System.out.println(x.getMac());
	}

	public String getIpAddr() {
		return ip;
	}

	public void setIpAddr(String ip) {
		this.ip = ip;
	}

	@Override
	public String getMac() {
		if (this.mac == null) {
			InetAddress ia;
			byte[] mac = null;
			try {
				ia = InetAddress.getByName(this.ip);
				mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
			} catch (UnknownHostException | SocketException e) {
				e.printStackTrace();
			}

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append(":");
				}
				String s = Integer.toHexString(mac[i] & 0xFF);
				sb.append(s.length() == 1 ? 0 + s : s);
			}

			this.mac = sb.toString().toLowerCase();
		}
		return this.mac;
	}
}
