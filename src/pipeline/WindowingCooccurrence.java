package pipeline;

import java.util.List;

import book.Book;
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
	/*
	 * True if you want to stop the detection at the end of a chapter even if the end of the window is not reached. 
	 * The detection will continue with a new window at the begining of the next chapter.
	*/
	boolean chapterLimitation;

	/*
	* the book you want to detect co-occurrences from.
	*/
	Book book;
	
	/**
	 * Class constructor
	 * 
	 * @param size size of the window used to find co-occurrences.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential detection.
	 */
	protected WindowingCooccurrence(int size, int covering, boolean chapterLimitation, Book book){
		this.size = size;
		this.covering = covering;
		this.chapterLimitation = chapterLimitation;
		this.book = book;
	}
	
	protected CooccurrenceTable createTab(CoreDocument document) {
		return null;
	}
	
	public List<List<CoreLabel>> createWindow() {
		return null;
	}
}
