package edu.bupt.wangfu.info.msg.udp;

import edu.bupt.wangfu.info.device.Controller;

import java.io.Serializable;

/**
 * Created by lenovo on 2016-6-22.
 */
public class MsgDetectGroupCtl_ implements Serializable {
	private static final long serialVersionUID = 1L;

	public Controller groupCtl;
	public String groupName;

	public MsgDetectGroupCtl_(String groupName, Controller groupCtl) {
		this.groupCtl = groupCtl;
		this.groupName = groupName;
	}
}
