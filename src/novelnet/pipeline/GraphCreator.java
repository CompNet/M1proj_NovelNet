package novelnet.pipeline;

import java.io.IOException;

import novelnet.graph.*;

import novelnet.table.CooccurrenceTableSentence;
import novelnet.table.DirectInteractionTable;
import novelnet.table.InteractionTable;

public class GraphCreator {

	private GraphCreator() {

	}

	public static Graph createGraph(InteractionTable tab, String name ,boolean oriented, boolean weighting) {
		Graph graph = new Graph(name, oriented, weighting);
		for (int i = 0; i < tab.getListCharA().size(); i++) { // Until we reach the size of the list of sentences
			Node nA = new Node(tab.getListCharA().get(i)); // We create a new node
			Node nB = new Node(tab.getListCharB().get(i)); // We create a new node
			graph.addNode(nA);
			graph.addNode(nB);
			graph.addEdge(nA, nB, tab.getListDistanceWord().get(i), tab.getListType().get(i)); // We create a new edge which links the two
																			// nodes with their weight
		}
		graph.setName(name);
		return graph;
	}

	public static void testCooccurrence() throws IOException{
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

		Graph g = GraphCreator.createGraph(cts,"graph_test_cooccurrence", false, true);
		g.graphMLPrinter("res/results");
		System.out.println(g.toString());
	}

	public static void testInteraction() throws IOException{
		DirectInteractionTable it = new DirectInteractionTable();
		it.add("A", "B", 0, "bad");
		it.add("A", "C", 0, "nice");
		it.add("C", "A", 0, "nice");
		it.add("C", "B", 0, "bad");
		it.add("B", "A", 1, "bad");
		it.add("C", "A", 1, "nice");
		it.add("C", "A", 1, "bad");

		DirectInteractionTable itNull = new DirectInteractionTable();
		itNull.add("A", "B", 0);
		itNull.add("A", "C", 0);
		itNull.add("C", "A", 0);
		itNull.add("C", "B", 0);
		itNull.add("B", "A", 1);
		itNull.add("C", "A", 1);
		itNull.add("C", "A", 1);

		it.display();
		itNull.display();

		Graph g = GraphCreator.createGraph(itNull, "graph_test_interaction", true, false);
		g.graphMLPrinter("res/results");
		System.out.println(g.toString());
	}

	public static void main(String[] args) throws IOException {
		//testCooccurrence();
		testInteraction();
	}
}

