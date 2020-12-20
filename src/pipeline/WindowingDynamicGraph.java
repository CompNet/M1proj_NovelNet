package pipeline;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Quay Baptiste, Lemaire Tewis
*/
public class WindowingDynamicGraph {

	Book book;
	CooccurrenceTable cooccurrenceTable;

	public WindowingDynamicGraph(Book book, CooccurrenceTable cooccurrenceTable) {
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
		List<CooccurrenceTable> result = new LinkedList<>();
		for (int i = 0; i < cooccurrenceTable.listCharA.size(); i++){
		}

		return result;
	};
	
	public List<CooccurrenceTable> dynamicTableParagraphs(int size, int covering){
		List<CooccurrenceTable> result = new LinkedList<>();
		return result;
	};

	public List<CooccurrenceTable> dynamicTableChapters(int size, int covering){
		List<CooccurrenceTable> result = new LinkedList<>();
		return result;
	};

}