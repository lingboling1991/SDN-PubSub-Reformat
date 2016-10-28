package edu.bupt.wangfu.mgr.base;

import edu.bupt.wangfu.info.device.Controller;
import edu.bupt.wangfu.info.device.Host;
import edu.bupt.wangfu.info.device.Switch;
import edu.bupt.wangfu.info.msg.udp.LSA;
import edu.bupt.wangfu.mgr.topology.graph.Edge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lenovo on 2016-6-22.
 */

public abstract class SysInfo {
	//本地属性
	public static String localMac;//本wsn节点所在计算机的mac地址
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
	public static Set<Edge> edges;//集群内所有swt连接的边的集合
	// 	public static Map<String, Broker> neighbors; //邻居集群，与上面groupMap区分开，这个是直接连接的；
	// key是连接的端口，value是对面的集群名
	public static HashMap<String, Host> hostMap;//当前集群所有host，key是mac
	public static HashMap<String, Switch> switchMap;//当前集群所有switch，key是id
	public volatile static HashMap<String, Switch> outSwtMap;//对外端口所在的swt，key是id
	//订阅表
	public static Set<String> localSubTopic;//本地订阅表，value是本地的订阅主题
	public static Map<String, Set<String>> groupSubMap;//本地的订阅信息，key是topic，value是swtId的集合
	public static Map<String, Set<String>> outerSubMap;//全网的订阅信息，key是topic，value是groupName的集合
//	public static LSA cacheLSA; //缓存LSA，缓存需要发送的LSA数据
	//管理员属性
	public static String adminAddr;//管理者的地址
	public static int adminPort;//管理者的地址
	//LSDB
	public static int lsaSeqNum;//LSA的序列号
	public static HashMap<String, LSA> lsdb;//LSA数据库，以swtId标示该集群发出的LSA消息
	//心跳管理器
	public static long threshold;//失效阀值的缺省值
	public static long sendPeriod;//发送频率的缺省值
	public static long refreshPeriod;//刷新集群内拓扑的频率
}
