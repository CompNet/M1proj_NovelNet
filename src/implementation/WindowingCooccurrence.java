/**
 * 
 */
package implementation;


import java.util.List;

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
	 * @param ponderation 
	 * @param size Window's size
	 * @param covering 
	 */
	protected WindowingCooccurrence(boolean weighting, int size, int covering)
	{
		this.weighting = weighting;
		this.size = size;
		this.covering = covering;
	}
	
	protected Tableau setTab(CoreDocument document) {
		return null;
	}
	
	protected Tableau createGraph(CoreDocument document,Graph graph) {
		return null;
	}
	
	/**
	 * Checks if the given list of edges contains the same edge given but reversed
	 * 
	 * @param l List of edges
	 * @param e Edge given 
	 * @return true if it already does contain the reversed edge, false if not
	 */
	protected boolean containInverseLink(List<Edge> l, Edge e)
	{
		for (Edge el : l) // For each edge in the list
		{
			if (el.nodeLeft.equals(e.nodeRight) && el.nodeRight.equals(e.nodeLeft)) // Checks if the left node is equal to the right node and if the right node is equal to the left one
				return true; // Returns true if yes
		}
		return false; //Returns false if conditions are not met
	}
}
