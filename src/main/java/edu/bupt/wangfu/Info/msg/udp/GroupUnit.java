package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;

/**
 * Created by lenovo on 2016-6-22.
 */
public class GroupUnit implements Serializable {
	private static final long serialVersionUID = 1L;

	public String name;

	public GroupUnit(String name) {
		this.name = name;
	}
}
