package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Host;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.udp.LSA;

import java.util.ArrayList;
import java.util.HashMap;

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
	public static Controller localCtl;//节点控制器
	public static Controller groupCtl;//集群控制器
	//拓扑
	public static String localSwtId;//wsn连接的switch的id
	public static String portWsn2Swt;//wsn连接switch，switch上的的端口
	//	public static Map<String, Broker> groups;
	// 	public static Map<String, Broker> neighbors; //邻居集群，与上面groupMap区分开，这个是直接连接的；
	// key是连接的端口，value是对面的集群名
	public static HashMap<String, Host> hostMap;//当前集群所有host，key是mac
	public static HashMap<String, Switch> switchMap;//当前集群所有switch，key是id
	public volatile static HashMap<String, Switch> outSwtMap;//对外端口所在的swt，key是id
	//订阅表
	public static ArrayList<String> subTable;//本地的订阅信息,本地broker订阅主题的集合
	public static LSA cacheLSA; //缓存LSA，缓存需要发送的LSA数据
	//管理员属性
	public static String adminAddr;//管理者的地址
	public static int adminPort;//管理者的地址
	//LSDB
	public static int lsaSeqNum;//LSA的序列号
	public static HashMap<String, LSA> lsdb;//LSA数据库，以集群名称标示该集群发出的LSA消息
	//心跳管理器
	public static long threshold;//失效阀值的缺省值
	public static long sendPeriod;//发送频率的缺省值
	public static long refreshPeriod;//刷新集群内拓扑的频率
}
