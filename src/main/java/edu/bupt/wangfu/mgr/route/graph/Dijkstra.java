package edu.bupt.wangfu.mgr.route.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dijkstra {
	public static Map<String, Integer> DIJKSTRA(Node start, Set<Node> op) {
		Set<Node> open = new HashSet<>();
		open.addAll(op);
		open.remove(start);
		Set<Node> close = new HashSet<>();
		close.add(start);

		//path存储其他节点到当前节点的距离
		Map<String, Integer> path = new HashMap<>();
		//初始化path，与start节点不相邻则为-1
		for (Node node : open) {
			path.put(node.getId(), -1);
		}
		//设置与start节点直接相邻的节点距离
		for (Node node : open) {
			if (start.getNeighbors().containsKey(node)) {
				path.put(node.getId(), start.getNeighbors().get(node));
			}
		}
		//结束条件：open集合为空
		while (!open.isEmpty()) {
			//查询距离start最近的节点
			Node nearest = getShortestPath(start, path, open);
			close.add(nearest);
			//距离start最近节点nearest
			open.remove(nearest);
			//更新path，只需要查询open集合内剩余节点
			int dis_1 = path.get(nearest.getId());
			for (Node node : open) {
				//当前节点到start节点的距离
				int dis_2 = path.get(node.getId());
				//当前节点到nearest节点的距离
				int dis_3;
				Map<Node, Integer> neighbors = nearest.getNeighbors();
				if (neighbors.containsKey(node)) {
					dis_3 = neighbors.get(node);
				} else {
					dis_3 = -1;
				}

				if (dis_3 == -1) {
					//当前节点没有与nearest节点直接相邻，不操作
				} else if (dis_2 == -1 || dis_2 > dis_1 + dis_3) {
					//当前节点没有与start节点相邻或者通过nearest节点的距离更短，更新
					path.put(node.getId(), dis_1 + dis_3);
				}
			}

		}
		System.out.println("起始节点 " + start.getId());
		System.out.println("各节点最短路径为 " + path.toString());
		return path;
	}

	//获取与node节点距离最近的节点
	private static Node getShortestPath(Node start, Map<String, Integer> path, Set<Node> open) {
		Node res = null;
		int minDis = Integer.MAX_VALUE;
		//只需查询在open集合中最小的路径所对应的节点
		for (Node node : open) {
			int distance = path.get(node.getId());
			if (distance == -1) {
				//当前节点未与start节点相邻，不操作
			} else if (distance < minDis) {
				minDis = distance;
				res = node;
			}
		}
		return res;
	}

}
