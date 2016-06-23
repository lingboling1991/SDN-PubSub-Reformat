package edu.bupt.wangfu.info.ldap;

import edu.bupt.wangfu.info.ldap.policy.WsnPolicyMsg;

public class TopicEntry implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String topicName = null;
	private String topicCode = null;
	private String topicPath = null;
	private WsnPolicyMsg wsnpolicymsg = null;

	public TopicEntry() {
	}

	public TopicEntry(String _topicName, String _topicCode,
	                  String _topicPath, WsnPolicyMsg _wsnpolicymsg) {
		this.topicName = _topicName;
		this.topicCode = _topicCode;
		this.topicPath = _topicPath;
		this.wsnpolicymsg = _wsnpolicymsg;
	}

	public String getTopicPath() {
		return topicPath;
	}

	public void setTopicPath(String topicPath) {
		this.topicPath = topicPath;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getTopicCode() {
		return topicCode;
	}

	public void setTopicCode(String topicCode) {
		this.topicCode = topicCode;
	}

	public WsnPolicyMsg getWsnpolicymsg() {
		return wsnpolicymsg;
	}

	public void setWsnpolicymsg(WsnPolicyMsg wsnpolicymsg) {
		this.wsnpolicymsg = wsnpolicymsg;
	}

	@Override
	public String toString() {
		return getTopicName();
	}
}
