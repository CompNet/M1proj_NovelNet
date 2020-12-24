package pipeline;

public class GraphCreator {
	public Graph createGraph(CooccurrenceTable tab, boolean weighting) {
		Graph graph = new Graph();
		for (int i=0; i<tab.listCharA.size(); i++){ // Until we reach the size of the list of sentences
			Node nA = new Node(tab.listCharA.get(i)); // We create a new node
			Node nB = new Node(tab.listCharB.get(i)); // We create a new node
			graph.addNode(nA);
			graph.addNode(nB);
			graph.addEdge(nA, nB, weighting, tab.listDistanceWord.get(i)); // We create a new edge which links the two nodes with their weight	
		}
		return graph;
	}	
	
	public Graph createGraph(CooccurrenceTable tab, boolean weighting, String name) {
		Graph graph = createGraph(tab, weighting);
		graph.setName(name);
		return graph;
	}
}

