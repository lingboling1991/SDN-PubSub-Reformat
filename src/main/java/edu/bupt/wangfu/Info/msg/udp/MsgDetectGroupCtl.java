package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;

/**
 * Created by lenovo on 2016-6-22.
 */
public class MsgDetectGroupCtl implements Serializable {
	private static final long serialVersionUID = 1L;

	public String indicator;//集群名

	public MsgDetectGroupCtl(String indicator) {
		this.indicator = indicator;
	}
}