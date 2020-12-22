package pipeline;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;

/**
 * A generic class for the windowing of co-occurrences.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
 *
 */
public abstract class WindowingCooccurrence {

	/**
	 * size of the window used to find co-occurrences.
	*/
	int size;
	/**
	 * size of the covering between 2 windows. Set to 0 for sequential detection.
	*/
	int covering;
	
	/**
	 * Class constructor
	 * 
	 * @param size size of the window used to find co-occurrences.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential detection.
	 */
	protected WindowingCooccurrence(int size, int covering){
		this.size = size;
		this.covering = covering;
	}
	
	protected CooccurrenceTable createTab(CoreDocument document) {
		return null;
	}
	
	public List<List<CoreLabel>> createWindow(CoreDocument document) {
		return null;
	}
}
