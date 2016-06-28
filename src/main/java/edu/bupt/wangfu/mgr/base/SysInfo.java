package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.info.device.Port;
import edu.bupt.wangfu.info.msg.udp.GroupUnit;
import edu.bupt.wangfu.info.msg.udp.LSA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lenovo on 2016-6-22.
 */

public abstract class SysInfo {
	//本地属性
	public static String groupName;//本集群的名字
	public static String localAddr;//本系统的地址
	//	public static String multiAddr;//群内组播地址
	public static int uPort;//UDP端口号，同时也是组播端口号
	public static int tPort;//TCP端口号
	//拓扑
	public static String localSwitch;//wsn连接的switch的id
	public static String wsn2swt;//wsn连接switch，switch上的的端口
	public static ConcurrentHashMap<String, GroupUnit> groups;//当前拓扑内，其他所有集群的信息，key为集群名；用的时候直接向controller请求
	public static ArrayList<String> neighbors; //邻居集群，与上面groupMap区分开，这个是直接连接的
	public static HashSet<String> hostSet;//当前集群所有host的mac
	public static HashSet<String> switchSet;//当前集群所有switch的id
	public volatile static HashMap<String, Port> outPorts;//wsn交换机对外开启的端口，key是端口号
	//订阅表
	public static ArrayList<String> subTable;//本地的订阅信息,本地broker订阅主题的集合
	//管理员属性
	public static String adminAddr;//管理者的地址
	public static int adminPort;//管理者的地址
	//LSDB
	public static int lsaSeqNum;//LSA的序列号
	public static ConcurrentHashMap<String, LSA> lsdb;//LSA数据库，以集群名称标示该集群发出的LSA消息
}
