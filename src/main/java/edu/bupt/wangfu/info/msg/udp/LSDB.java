package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;
import java.util.ArrayList;

public class LSDB implements Serializable {
	private static final long serialVersionUID = 1L;

	public ArrayList<LSA> lsdb;

	public LSDB() {
		lsdb = new ArrayList<>();
	}
}