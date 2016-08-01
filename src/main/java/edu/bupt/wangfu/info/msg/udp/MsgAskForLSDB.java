package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;

public class MsgAskForLSDB implements Serializable {
	private static final long serialVersionUID = 1L;
	public String askMessage;

	public MsgAskForLSDB(String msg) {
		this.askMessage = msg;
	}
}