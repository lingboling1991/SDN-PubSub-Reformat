package edu.bupt.wangfu.info.ldap.policy;

/**
 * @author shoren
 * @date 2013-3-29
 */

public class TargetMsg implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	protected String name;

	public TargetMsg(String name) {
		this.name = name;
	}

	public TargetMsg() {
		this(null);
	}

	public static void main(String[] args) {
		TargetMsg t1 = new TargetMsg("a");
		TargetMsg t2 = new TargetMsg("a");

		System.out.println(t1.equals(t2));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return getName();
	}

	public boolean equals(Object anObject) {
		if (!(anObject instanceof TargetMsg))
			return false;
		TargetMsg msg = (TargetMsg) anObject;
		if (this.getName().equals(msg.getName()))
			return true;
		return false;
	}
}
