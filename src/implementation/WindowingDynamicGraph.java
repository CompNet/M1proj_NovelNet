package implementation;

import edu.stanford.nlp.pipeline.CoreDocument;

public class WindowingDynamicGraph {
	/*
	*  Size of the text block analysed.
	*  sentence
	*  paragraph
	*  chapter
	*/
	String optionSize;		
	boolean sequential;		//If true sequential movement for the window. If false option will be smoothing.
	int windowSize;			//Size of the window
	int coveringSize;		//Size of the covering between windows. (must be stricly inferior to windowSize).
	CoreDocument document;
	Graph graph;
	boolean ponderation;
	Book book;
	
    public WindowingDynamicGraph()
	{
		 
	}

	/**
	* 
	* @param optionSize
	* @param sequentiel;
	* @param windowSize
	* @param coveringSize
	* @param document
	* @param graph
	* @param ponderation
	*/
	public WindowingDynamicGraph(String optionSize, boolean sequential, int windowSize, int coveringSize, CoreDocument document, Graph graph, boolean ponderation)
	{
		this.optionSize = optionSize;
		this.sequential = sequential;
		this.windowSize = windowSize;
		if (coveringSize < windowSize) this.coveringSize = coveringSize;
		else {
			System.out.println("covering size supperior or equal to window size. Putting the option back to Sequential");
			sequential = true;
		}
		this.document = document;
		this.graph = graph;
		this.ponderation = ponderation;
	}

}