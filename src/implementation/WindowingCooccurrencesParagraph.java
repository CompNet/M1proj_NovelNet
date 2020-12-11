package implementation;

import edu.stanford.nlp.pipeline.CoreDocument;

public class WindowingCooccurrencesParagraph extends WindowingCooccurrence{


	/**
	 * Constructor for the class WindowingCooccurrencesParagraph
	 * 
	 * @param weighting 
	 * @param size Window's size
	 * @param covering 
	 */
	public WindowingCooccurrencesParagraph(boolean weighting , int size, int covering){
		super(weighting, size, covering);
	}

}
