package pipeline;

import java.util.LinkedList;
import java.util.List;

public class WindowingDynamicGraphFromParagraphTable implements WindowingDynamicGraph {

    Book book;
	CooccurrenceTable cooccurrenceTable;

    
    protected WindowingDynamicGraphFromParagraphTable(Book book, CooccurrenceTable cooccurrenceTable) {
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
		int countDynamicWindow = 0;
		int countLine = 0;
		int beginLine = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicWindowEnding = (countDynamicWindow+1)*size-(countDynamicWindow*covering)-1;
		List<CooccurrenceTable> result = new LinkedList<>();
		for (int i = 0; i < cooccurrenceTable.listCharA.size(); i++){
			windowBegin = cooccurrenceTable.listBeginingWindow.get(i);
			windowEnd = cooccurrenceTable.listEndingWindow.get(i);
			while ((countLine == 0)){
				if (((windowBegin - dynamicWindowEnding) - (windowEnd - dynamicWindowEnding) < 0)){
					break;
				}
				else{
					countDynamicWindow++;
					dynamicWindowEnding = (countDynamicWindow+1)*size-(countDynamicWindow*covering)-1;
				}
			}
			if ( (windowBegin - dynamicWindowEnding) - (windowEnd - dynamicWindowEnding) < 0){
				countLine++;
			}
			else{
				result.add(cooccurrenceTable.subTable(beginLine, beginLine+countLine));
				beginLine = i;
				countLine = 0;
			}
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
