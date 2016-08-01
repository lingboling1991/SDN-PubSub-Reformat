package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;

public class DistanceBetweenNeighbor implements Serializable {
	public int dist; // 与该邻居的距离

	public DistanceBetweenNeighbor(int dist) {
		this.dist = dist;
	}

	public int getDist() {
		return dist;
	}
}
