package pipeline;

import java.util.List;

import book.Book;

/**
 * @author Quay Baptiste, Lemaire Tewis
*/
public interface WindowingDynamicGraph {
	
	public Book getBook();

	public void setBook(Book book);

	public CooccurrenceTable getCooccurrenceTable();

	public void setCooccurrenceTable(CooccurrenceTable cooccurrenceTable);

	public List<CooccurrenceTable> dynamicTableSentences(int size, int covering);
	
	public List<CooccurrenceTable> dynamicTableParagraphs(int size, int covering);

	public List<CooccurrenceTable> dynamicTableChapters(int size, int covering);
}