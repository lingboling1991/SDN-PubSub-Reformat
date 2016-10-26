package edu.bupt.wangfu.mgr.topology.graph;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Del {
	public Del() {

	}

	public void Del(String id, Set<Node> select, Set<Node> open, Set<Edge> g) {
		Degree de = new Degree();
		Node delNode = idToNode(id, open);
		int degr = 0;
		degr = de.degree(id, g);
		System.out.println("当前节点度：" + degr);
		if (degr == 0) {
			System.out.println("当前节点未订阅，无法删除");
		} else if (degr == 1) {
			//当前订阅节点度为1，可以直接删除该节点所有信息
			select.remove(delNode);
			for (Edge ed : g) {
				if (ed.getStart().equals(id) || ed.getFinish().equals(id)) {
					g.remove(ed);
					break;
				}
			}
		} else {
			//当前订阅节点起到承接作用，删除后会分裂树
			select.remove(delNode);
			Set<String> partNode = new TreeSet<>();
			Set<Edge> delEdge = new TreeSet<>();
			for (Edge ed : g) {
				if (ed.getStart().equals(id)) {
					delEdge.add(ed);
					partNode.add(ed.getFinish());
				} else if (ed.getFinish().equals(id)) {
					delEdge.add(ed);
					partNode.add(ed.getStart());
				}
			}
			g.removeAll(delEdge);
			//将partNode中的连接点求彼此间的最小生成树
			Kruskal k = new Kruskal();
			Set<Edge> partEdge;
			Set<Edge> partE = new TreeSet<>();
			Dijkstra d = new Dijkstra();
			for (String st : partNode) {
				Map<String, Integer> dis = d.DIJKSTRA(idToNode(st, open), open);
				for (String other : partNode) {
					if (other.equals(st)) {
						//自己与自己的距离不计
					} else {
						Edge newEdge = new Edge(st, other, dis.get(other));
						partE.add(newEdge);
					}
				}
			}

			partEdge = k.KRUSKAL(select, partE);
			g.addAll(partEdge);

		}
	}

	public Node idToNode(String id, Set<Node> open) {
		for (Node node : open) {
			if (node.getId().equals(id))
				return node;
		}
		return null;
	}
}
