package implementation;

import java.util.List;

import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * @author Quay Baptiste, Lemaire Tewis
*/
public abstract class WindowingDynamicGraph {

	Book book;

	protected WindowingDynamicGraph(Book book){
		this.book = book;
	}

	public List<List<CoreSentence>> toSentences(int size, int covering){
		return null;
	};
	
	public List<List<Paragraph>> toParagraphs(int size, int covering){
		return null;
	};

}