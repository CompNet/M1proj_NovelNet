package pipeline;

import java.io.IOException;

public class GraphCreator {

	public GraphCreator() {

	}

	public Graph createGraph(CooccurrenceTable tab, boolean weighting) {
		Graph graph = new Graph();
		for (int i = 0; i < tab.listCharA.size(); i++) { // Until we reach the size of the list of sentences
			Node nA = new Node(tab.listCharA.get(i)); // We create a new node
			Node nB = new Node(tab.listCharB.get(i)); // We create a new node
			graph.addNode(nA);
			graph.addNode(nB);
			graph.addEdge(nA, nB, weighting, tab.listDistanceWord.get(i)); // We create a new edge which links the two
																			// nodes with their weight
		}
		return graph;
	}

	public Graph createGraph(CooccurrenceTable tab, boolean weighting, String name) {
		Graph graph = createGraph(tab, weighting);
		graph.setName(name);
		return graph;
	}

	public static void main(String[] args) throws IOException {

		CooccurrenceTableSentence cts= new CooccurrenceTableSentence();
		cts.add("A", "B", 25, 5, 0, 4);
		cts.add("A", "C", 20, 4, 0, 4);
		cts.add("B", "C", 15, 3, 0, 4);
		cts.add("A", "C", 15, 3, 4, 8);
		cts.add("B", "C", 25, 5, 4, 8);
		cts.add("A", "C", 10, 2, 12, 16);
		cts.add("B", "A", 15, 3, 12, 16);
		cts.add("A", "E", 20, 4, 16, 20);
		cts.add("E", "C", 20, 4, 16, 20);

		GraphCreator gC = new GraphCreator();
		Graph g = gC.createGraph(cts,true,"graph_test");
		g.graphMLPrinter("res/resultats");
		//System.out.println(g.toString());

	}
}

