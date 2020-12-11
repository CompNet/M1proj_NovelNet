/**
 * 
 */
package implementation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * @author Quay Baptiste, Lemaire Tewis
 * 
 */
public class WindowingCooccurrenceSentence extends WindowingCooccurrence  {
	
	/**
	 * Constructor for the class WindowingCooccurrenceSentence
	 * 
	 * @param weighting Used to add weight to edges of the sociogram
	 * @param size Window's size set by the user
	 * @param covering Window's covering set by the user
	 */
	public WindowingCooccurrenceSentence(boolean weighting , int size, int covering){
		super(weighting, size, covering);
	}
	
	/**
	 * Fills a graph with nodes and edges depending on a text's character's co-occurrences 
	 * 
	 * @param content List of sentences from the text
	 * @param graph New graph that we will fill in the function
	 */
	@Override
	public Tableau createGraph(CoreDocument document ,Graph graph) {
		int distance = 0;
		int distance1 = 0;
		int distance2 = 0;
		int i = 0;
		String charA = null;
		String charB = null;
		Tableau tab = new Tableau();
		List<CoreSentence> sentences = document.sentences();
		for (int j=0; j<sentences.size(); j++){
			CoreSentence sentence = sentences.get(j);
			for (CoreLabel token1 : sentence.tokens()){ // For each token
				for (CoreLabel token2 : sentence.tokens()){ // For each token
					if(token1.ner().equals("PERSON")){
						if(token2.ner().equals("PERSON")){
							charA =  token1.originalText();
							distance1 = token1.beginPosition();
							charB = token2.originalText(); 
							distance2 = token2.beginPosition();
							distance = distance1 - distance2;		
							if (!charA.equals(charB)) {
								if (distance < 0) continue;
								tab.setTableau(charA, charB, distance, i);
								distance1 = 0;
								distance2 = 0;
								i++;
							}
						}
					}
				}
			}
		}
		tab.display(i);
		return tab;
	}
	//corpus/Coraline2.txt
	//Vielle fonction d'extraction de graphe
	/*@Override
	public void createGraph(CoreDocument document,Graph graph) {
		int cpt = 0; // Counter made to reach the given size
		Map<String,Node> charMap = new HashMap<String, Node>(); // Map of Nodes with their IDs		
		List<CoreSentence> sentences = document.sentences();
		List<Edge> edgeList = new LinkedList<Edge>(); // List of edges
		for (int i=0; i<setTab(document).i; i++){ // Until we reach the size of the list of sentences
			if (cpt == size){ //If the counter reaches the given size
				for (Node n : charMap.values()) { // For each node in the map
					graph.addNode(n); // We add it to the graph
				}
				for (Edge e : edgeList) { // For each edge in the list
					graph.addEdgeWithWeighting(e); // We add it to the graph with weighting
				}
				charMap.clear(); //We clear the map of nodes
				edgeList.clear(); // We clear the list of edges
				cpt = 0; // We reset the counter
				i -= covering; // We apply the set covering 
			}
			CoreSentence sentence = sentences.get(i);
			for (CoreLabel token : sentence.tokens()){ // For each token
				if(token.ner().equals("PERSON")){ // We check if its NER corresponds with a person
					Map<Integer, CorefChain> corefChains = document.corefChains(); //We create a map of corefChains (each represents a set of mentions which corresponds to the same entity)
					for (CorefChain corefChain : corefChains.values()) { // For each corefChain
						Node n = new Node(token.word(),token.word(),0); // We create a new node
						charMap.put(n.id, n); // Add it to the map
						for (CorefMention mention : corefChain.getMentionsInTextualOrder()) { //For each mention in the corefChains
							if (charMap.containsKey(mention.mentionSpan)) { // If the map already contains the mention
								if (weighting) charMap.get(mention.mentionSpan).addWeight(1); //If weighting is true, we add weight to the mention in the map
							}
							for (Node nodeL : charMap.values()){ // For each left node
								for (Node nodeR : charMap.values()){ // For each right node
									if (nodeL.equals(nodeR)) continue; // We skip if they're the same node
									Edge edge = new Edge(nodeL.id,nodeL,nodeR,false,nodeL.weight+nodeR.weight); // We create a new edge which links the two nodes with their weight
									if (!edgeList.contains(edge)&&!containInverseLink(edgeList,edge)) edgeList.add(edge); // If the list does not already contain the edge, be it reversed or not, we add it to the list
								}
							}
						}
					}
				}
			}
			cpt++; // Increase of the counter by 1
		}
		for (Node n : charMap.values()) { // For each node in the map
			graph.addNode(n); // We add it to the graph
		}
		for (Edge e : edgeList) { // For each edge in the list
			graph.addEdgeWithWeighting(e); // We add it to the graph with weighting
		}
		charMap.clear(); //We clear the map of nodes
		edgeList.clear(); // We clear the list of edges
	}*/
}
