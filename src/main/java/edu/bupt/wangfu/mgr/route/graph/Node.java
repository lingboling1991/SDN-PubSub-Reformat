package edu.bupt.wangfu.mgr.route.graph;

import java.util.HashMap;
import java.util.Map;

public class Node implements Comparable {
	private String id;
	//相邻节点信息，map键值对形式存储
	private Map<Node, Integer> neighbors = new HashMap<>();

	public Node(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<Node, Integer> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(Map<Node, Integer> neighbors) {
		this.neighbors = neighbors;
	}

	public void addNeighbor(Node node, int value) {
		this.neighbors.put(node, value);
	}

	@Override
	public int compareTo(Object arg0) {
		Node other = (Node) arg0;
		if (this.id.compareTo(other.id) < 0)
			return -1;
		else if (this.id.compareTo(other.id) == 0)
			return 0;
		return 1;
	}
}
