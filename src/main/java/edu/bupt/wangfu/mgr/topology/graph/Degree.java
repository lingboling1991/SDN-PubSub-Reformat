package edu.bupt.wangfu.mgr.topology.graph;

import java.util.Set;

public class Degree {
	public Degree(){
		
	}
	public int degree(String id, Set<Edge> g){
		int degree = 0;
		for(Edge e : g){
			if(e.getFinish().equals(id) || e.getStart().equals(id))
				degree++;
		}
		return degree;
	}
}
