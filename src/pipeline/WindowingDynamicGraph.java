package pipeline;

import java.util.List;

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

	public static void main(String[] args) {
		CooccurrenceTableSentence test = new CooccurrenceTableSentence();
		test.createCustom();
		test.display();
		Book book = new Book();

		WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, test);
		for (CooccurrenceTable t : dgs.dynamicTableSentences(10, 2)){
			System.out.println("nouveau tableau 10 2 ");
			t.display();
		}

		for (CooccurrenceTable t : dgs.dynamicTableSentences(5, 1)){
			System.out.println("nouveau tableau 5 1");
			t.display();
		}
	}
}