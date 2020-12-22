package pipeline;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreateGraph {
	public void graphC(CooccurrenceTable tab,Graph graph) {
		Map<String,Node> charMapA = new HashMap<String, Node>(); // Map of Nodes with their IDs		
 		List<Edge> edgeList = new LinkedList<Edge>(); // List of edges
		for (int i=0; i<tab.listCharA.size(); i++){ // Until we reach the size of the list of sentences
			Node nA = new Node(tab.listCharA.get(i),tab.listCharA.get(i),0); // We create a new node
			charMapA.put(nA.id, nA); // Add it to the map
			charMapA.get(tab.listCharA.get(i)).addWeight((1f/tab.listDistanceWord.get(i))); //If weighting is true, we add weight to the mention in the map
		}
		for (int i=0; i<tab.listCharA.size(); i++){
			for (Node nodeL : charMapA.values()){ // For each left node
				for (Node nodeR : charMapA.values()){ // For each right node
					Edge edge = new Edge(nodeL.id,nodeL,nodeR,false,nodeL.weight); // We create a new edge which links the two nodes with their weight
					if (tab.listCharA.get(i) == nodeL.getName() && tab.listCharB.get(i) == nodeR.getName()) edgeList.add(edge); // If the list does not already contain the edge, be it reversed or not, we add it to the list
				}
			}
		}
		for (Node n : charMapA.values()) { // For each node in the map
			graph.addNode(n); // We add it to the graph
		}
		for (Edge e : edgeList) { // For each edge in the list
			graph.addEdgeWithWeighting(e); // We add it to the graph with weighting
		}
		System.out.println(charMapA.toString());
		charMapA.clear(); //We clear the map of nodes
		edgeList.clear(); // We clear the list of edges
	}
}
