package pipeline;

import java.util.List;

import book.Book;

/**
 * Generic class for the creation of dynamic graphs.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public abstract class WindowingDynamicGraph {

	/**
	 * the Book created from the original text.
	*/ 
	protected Book book;
	/**
	 * the co-occurrence table you want to create the dynamic graphs from.
	*/ 
	protected CooccurrenceTable cooccurrenceTable;

	/**
	 * Class Constructor
	 * 
	 * @param book the Book created from the original text.
	 * @param cooccurrenceTable the co-occurrence table you want to create the dynamic graphs from.
	*/
	protected WindowingDynamicGraph(Book book, CooccurrenceTable cooccurrenceTable) {
		this.book = book;
		this.cooccurrenceTable = cooccurrenceTable;
    }
	
	public Book getBook() {
		return this.book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public CooccurrenceTable getCooccurrenceTable() {
		return this.cooccurrenceTable;
	}

	public void setCooccurrenceTable(CooccurrenceTable cooccurrenceTable) {
		this.cooccurrenceTable = cooccurrenceTable;
	}

	@Override
	public String toString() {
		return "{" +
			" book='" + getBook() + "'" +
			"}";
	}

	public List<CooccurrenceTable> dynamicTableSentences(int size, int covering){
		return null;
	}
	
	public List<CooccurrenceTable> dynamicTableParagraphs(int size, int covering){
		return null;
	}

	public List<CooccurrenceTable> dynamicTableChapters(int size, int covering){
		return null;
	}
}