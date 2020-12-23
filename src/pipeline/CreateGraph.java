package pipeline;

import java.util.LinkedList;
import java.util.List;

public class CreateGraph {
	public void graphC(CooccurrenceTable tab,Graph graph) {
		List<Node> nodeListA = new LinkedList<Node>();
		List<Node> nodeListB = new LinkedList<Node>();
 		List<Edge> edgeList = new LinkedList<Edge>(); // List of edges
 		int cpt = 0;
		for (int i=0; i<tab.listCharA.size(); i++){ // Until we reach the size of the list of sentences
			Node nA = new Node(tab.listCharA.get(i),tab.listCharA.get(i),0); // We create a new node
			Node nB = new Node(tab.listCharB.get(i),tab.listCharB.get(i),0); // We create a new node
			nodeListA.add(nA);
			nodeListB.add(nB);
			nodeListA.get(i).addWeight((1f/tab.listDistanceWord.get(i))); //If weighting is true, we add weight to the mention in the map
		}
		while (cpt != nodeListA.size()){
			Edge edge = new Edge(nodeListA.get(cpt).id,nodeListA.get(cpt),nodeListB.get(cpt),false,nodeListA.get(cpt).weight); // We create a new edge which links the two nodes with their weight
			edgeList.add(edge); // If the list does not already contain the edge, be it reversed or not, we add it to the list
			cpt++;
		}
		for (Node n : nodeListA) { // For each node in the map
			graph.addNode(n); // We add it to the graph
		}
		for (Edge e : edgeList) { // For each edge in the list
			graph.addEdgeWithWeighting(e); // We add it to the graph with weighting			
		}
		edgeList.clear(); // We clear the list of edges
		nodeListA.clear();
		nodeListB.clear();
	}
}
