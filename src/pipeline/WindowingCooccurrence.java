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
	boolean weighting; 
	int size; 
	int covering;
	
	/**
	 * 
	 * @param weighting 
	 * @param size Window's size
	 * @param covering 
	 */
	protected WindowingCooccurrence(boolean weighting, int size, int covering){
		this.weighting = weighting;
		this.size = size;
		this.covering = covering;
	}
	
	protected Table createTab(CoreDocument document) {
		return null;
	}
	
	public List<List<CoreLabel>> createWindow(CoreDocument document) {
		return null;
	}
}