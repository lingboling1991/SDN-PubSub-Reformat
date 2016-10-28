package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;

/**
 * Created by lenovo on 2016-10-26.
 */
public class NewSub implements Serializable {
	private static final long serialVersionUID = 1L;

	public String group;
	public String topic;
	public String hostMac;
	public String port;
	public String swtId;
}
