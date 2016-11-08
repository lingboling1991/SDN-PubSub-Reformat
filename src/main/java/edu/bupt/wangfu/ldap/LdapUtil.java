package edu.bupt.wangfu.ldap;

import edu.bupt.wangfu.mgr.base.SysInfo;
import edu.bupt.wangfu.ldap.topic.WSNTopicObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by lenovo on 2016-11-8.
 */
public class LdapUtil extends SysInfo {
	public static WSNTopicObject topicTree;

	public static void main(String[] args) {
		try {
			readTopicTree();
			printTopicTree();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readTopicTree() throws Exception {
		Socket s = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		Object recieveTopicTee;

		try {
			s = new Socket();
			s.connect(new InetSocketAddress("10.109.253.27", 30006), 5000);
//			s.connect(new InetSocketAddress(adminAddr, adminPort), 5000);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());

			// 发送请求空object
			WSNTopicObject requestTopicTree = new WSNTopicObject();
			oos.writeObject(requestTopicTree);
			System.out.println("send topictree download request");
			// 接收TopicTree
			while (true) {
				recieveTopicTee = ois.readObject();
				if (recieveTopicTee instanceof WSNTopicObject) {
					topicTree = (WSNTopicObject) recieveTopicTee;
					System.out.println("从管理员处下载主题树完成");
					break;

				} else
					System.out.println("没有下载到主题树，将重新下载");

			}
		} catch (IOException e) {
			System.out.println("从管理员处下载主题数出错，将重新下载");
			readTopicTree();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (ois != null) {
					ois.close();
				}
				if (s != null) {
					s.close();
				}
			} catch (IOException e) {
				System.out.println("关闭下载主题树的socket出错");
			}
		}
	}

	// 打印当前内存中存储的topic tree
	public static void printTopicTree() {
		Queue<WSNTopicObject> printQueue = new LinkedList<WSNTopicObject>();
		printQueue.offer(topicTree);
		while (!printQueue.isEmpty()) {
			WSNTopicObject x = printQueue.poll();
			if (x == topicTree)
				System.out.println("root: " + x);
			else
				System.out.println(x + " " + x.getParent());
			List<WSNTopicObject> y = x.getChildrens();
			if (!y.isEmpty()) {
				for (WSNTopicObject w : y) {
					printQueue.offer(w);
					System.out.print(w + " " + w.getParent() + "   ");
				}
				System.out.println();
			}
		}
	}
}
