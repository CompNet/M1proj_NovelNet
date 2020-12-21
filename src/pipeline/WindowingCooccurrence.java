/**
 * 
 */
package pipeline;


import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */


public class WindowingCooccurrence {
	int size; 
	int covering;
	
	/**
	 * 
	 * @param size Window's size
	 * @param covering 
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
