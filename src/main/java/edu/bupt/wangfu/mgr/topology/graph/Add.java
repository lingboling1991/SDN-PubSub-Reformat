package edu.bupt.wangfu.mgr.topology.graph;

import java.util.Map;
import java.util.Set;

public class Add {
	public Add(){
		
	}
	
	public void add(String id, Set<Node> select, Set<Node> open, Set<Edge> g){
		
		Edge minEdge = new Edge();
		minEdge.setValue(Integer.MAX_VALUE);
		minEdge.setStart(id);
		if(isSelect(id, select)){
			System.out.println("纳尼");
		}
		else{
			Dijkstra d = new Dijkstra();
			Node addNode = idToNode(id, open);
			Map<String, Integer> dis = d.DIJKSTRA(addNode, open);
			for(Node other : select){
				String edgeFinish = other.getId();
				int edgeValue = dis.get(edgeFinish);
				if(edgeValue < minEdge.getValue()){
					minEdge.setFinish(edgeFinish);
					minEdge.setValue(edgeValue);
				}
			}
			minEdge.sequence();
			g.add(minEdge);
			select.add(addNode);
		}
	}
	
	public boolean isSelect(String id, Set<Node> select){
		for(Node node: select){
			if(node.getId().equals(id))
				return true;
		}
		return false;
	}
	
	public Node idToNode(String id, Set<Node> open){
		for(Node node : open){
			if(node.getId().equals(id))
				return node;
		}
		return null;
	}

}
