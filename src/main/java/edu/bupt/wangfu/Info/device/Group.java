package edu.bupt.wangfu.info.device;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lenovo on 2016-10-31.
 */
public class Group extends DevInfo {
	public long updateTime;
	public String groupName;
	public Map<String, Integer> distances = new ConcurrentHashMap<>();//实现了neighbor的功能，不需要用DevInfo里面的neighbors了。key是邻居groupName，value是二者相连的链路的距离

	public Group(String groupName) {
		this.groupName = groupName;
	}
}