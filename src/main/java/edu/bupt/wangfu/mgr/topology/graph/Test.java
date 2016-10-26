package edu.bupt.wangfu.mgr.topology.graph;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/* open�����нڵ㼯��
 * select�����ж��Ľڵ㼯��
 * e�����Ľڵ�����бߵļ���
 * g�����Ľڵ㹹�ɵ���С�������ߵļ���
 */

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//select�洢���Ľڵ㼯��
		Set<Node> select = new TreeSet<Node>();
		//g�洢��С�������ı߼���
		Set<Edge> g = new TreeSet<Edge>();
		//e�洢���бߵļ���
		Set<Edge> e = new TreeSet<Edge>();
		//openΪ���нڵ㼯��
		Set<Node> open = new TreeSet<Node>();

		//���������нڵ�
		System.out.println("����������ͼ�����нڵ㣨����end��������");
		Scanner in = new Scanner(System.in);

		String id = in.next();
		//Node node = new Node(null);
		while (!id.equals("end")) {
			//��ʱû�и��õķ������˴�����ֻ��Ϊ��ֵͬ
			Node node = new Node(id);
			node.setId(id);
			open.add(node);
			id = in.next();
		}
		System.out.println(open.size());

		//�������ڽڵ���Ϣ
		System.out.println("���������ڽڵ㼰��Ȩֵ������end��������");
		Node startNode = null, finishNode = null;
		String start = in.next();
		while (!start.equals("end")) {
			String finish = in.next();
			int value = in.nextInt();
			//�ҵ���ʼ����ֹ�ڵ�
			for (Node node : open) {
				if (node.getId().equals(start)) {
					startNode = node;
					break;
				}
			}

			for (Node node : open) {
				if (node.getId().equals(finish)) {
					finishNode = node;
					break;
				}
			}
			//����ͼ���ߵ���ϢΪ˫��
			startNode.addNeighbor(finishNode, value);
			finishNode.addNeighbor(startNode, value);
			start = in.next();
		}

//		for(Node node : open){
//			System.out.println("��ǰ�ڵ㣺" + node.getId());
//			Set keys = node.getNeighbors().entrySet();
//			Iterator it = keys.iterator();
//			while (it.hasNext()){
//				Map.Entry e = (Map.Entry)it.next();
//				System.out.println("�յ㣺" + e.getKey() + "        Ȩֵ��" + e.getValue());
//			}
//		}

		System.out.println("game over~");
		//Dijkstra a = new Dijkstra(startNode, open);

		System.out.println("�����붩�Ľڵ㣨����end��������");
		id = in.next();

		while (!id.equals("end")) {
			for (Node node : open) {
				if (node.getId().equals(id)) {
					select.add(node);
					break;
				}
			}
			id = in.next();
		}

		System.out.println("Dijkstra����������룺");
		Dijkstra d = new Dijkstra();

		//��Dijkstra����select��Ԫ���������ڵ���룬�����������Edge��
		for (Node node : select) {
			Map<String, Integer> dis = d.DIJKSTRA(node, open);
			for (Node other : select) {
				if (other.getId().equals(node.getId())) {
					//�Լ����Լ��ľ��벻��
				} else {
					String edgeStart = node.getId();
					String edgeFinish = other.getId();
					int edgeValue = dis.get(edgeFinish);
					Edge newEdge = new Edge(edgeStart, edgeFinish, edgeValue);
					e.add(newEdge);
				}
			}
		}

		//Arrays.sort(e.toArray());
		//TreeSet ts = new TreeSet(e);
		//ts.comparator();
//	    for(Edge ed : e){
//	    	System.out.println(ed.getStart() + "        " + ed.getFinish() + "        " + ed.getValue());
//	    }

		System.out.println("Kruskal����С��ͨͼ��");
		Kruskal k = new Kruskal();
		g = k.KRUSKAL(select, e);
		System.out.println("game over~");

		System.out.println("��ǰ���Ľڵ�Ϊ��");
		for (Node node : select) {
			System.out.println(node.getId());
		}
		System.out.println("��ǰ�ߵļ���Ϊ��");
		for (Edge ed : g) {
			System.out.println(ed.getStart() + "  ->  " + ed.getFinish() + "    �ߵĳ���Ϊ��" + ed.getValue());
		}
		System.out.println("��������Ҫ���еĲ���������Add��Ӷ��Ľڵ㣬����delɾ�����Ľڵ㣬����end������������");
		String op = in.next();
		while (!op.equals("end")) {
			switch (op) {
				case "Add":
					System.out.println("��������Ӷ��Ľڵ�ֵ��");
					id = in.next();
					Add ad = new Add();
					ad.add(id, select, open, g);
					break;
				case "del":
					System.out.println("������ɾ�����Ľڵ�ֵ��");
					id = in.next();
					Del del = new Del();
					del.Del(id, select, open, g);
					break;
			}

			System.out.println("��ǰ���Ľڵ�Ϊ��");
			for (Node node : select) {
				System.out.println(node.getId());
			}
			System.out.println("��ǰ�ߵļ���Ϊ��");
			for (Edge ed : g) {
				System.out.println(ed.getStart() + "  ->  " + ed.getFinish() + "    �ߵĳ���Ϊ��" + ed.getValue());
			}

			System.out.println("��������Ҫ���еĲ���������Add��Ӷ��Ľڵ㣬����delɾ�����Ľڵ㣬����end������������");
			op = in.next();
		}
		System.out.println("game over~");
	}

}
