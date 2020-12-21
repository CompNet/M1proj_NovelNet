package pipeline;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreateGraph {
	public void graphC(CooccurrenceTable tab,Graph graph) {
		Map<String,Node> charMap = new HashMap<String, Node>(); // Map of Nodes with their IDs		
 		List<Edge> edgeList = new LinkedList<Edge>(); // List of edges
		for (int i=0; i<tab.listCharA.size(); i++){ // Until we reach the size of the list of sentences
			for (int j=0; j<tab.listCharB.size(); j++){
				Node n = new Node(tab.listCharA.get(i),tab.listCharA.get(i),0); // We create a new node
				charMap.put(n.id, n); // Add it to the map
				if (charMap.containsKey(tab.listCharA.get(i))) { // If the map already contains the mention
					charMap.get(tab.listCharA.get(i)).addWeight(1); //If weighting is true, we add weight to the mention in the map
				}
				for (Node nodeL : charMap.values()){ // For each left node
					for (Node nodeR : charMap.values()){ // For each right node
						if (nodeL.equals(nodeR)) continue; // We skip if they're the same node
						if () {
							Edge edge = new Edge(nodeL.id,nodeL,nodeR,false,nodeL.weight+nodeR.weight); // We create a new edge which links the two nodes with their weight
							if (!edgeList.contains(edge)) edgeList.add(edge); // If the list does not already contain the edge, be it reversed or not, we add it to the list
						}
					}
				}
			}
		}
		for (Node n : charMap.values()) { // For each node in the map
			graph.addNode(n); // We add it to the graph
		}
		for (Edge e : edgeList) { // For each edge in the list
			graph.addEdgeWithWeighting(e); // We add it to the graph with weighting
		}
		charMap.clear(); //We clear the map of nodes
		edgeList.clear(); // We clear the list of edges
	}
}
