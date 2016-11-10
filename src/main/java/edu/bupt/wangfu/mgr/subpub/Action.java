package edu.bupt.wangfu.mgr.subpub;

/**
 * Created by lenovo on 2016-11-2.
 */
//由host发消息时调用，用来标记这条消息是什么类型
public enum Action {
	SUB, UNSUB, PUB, UNPUB;
}
