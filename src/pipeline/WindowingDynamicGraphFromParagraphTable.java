package pipeline;

import java.util.LinkedList;
import java.util.List;

public class WindowingDynamicGraphFromParagraphTable implements WindowingDynamicGraph {

    protected Book book;
	protected CooccurrenceTable cooccurrenceTable;

    public WindowingDynamicGraphFromParagraphTable(Book book, CooccurrenceTable cooccurrenceTable) {
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
		boolean done = false;
		boolean searchingEnd = false;
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicWindowBegin = 0;
		int dynamicWindowEnd = 0;
		while(!done){
			dynamicWindowBegin = dynamicCpt*size - dynamicCpt*covering;
			dynamicWindowEnd = (dynamicCpt + 1)*size - dynamicCpt*covering -1;
			for (int i = 0; i < cooccurrenceTable.listCharA.size(); i++){
				windowBegin = book.getBeginIndexOfParagraph(cooccurrenceTable.listBeginingWindow.get(i));
				windowEnd = book.getEndIndexOfParagraph(cooccurrenceTable.listEndingWindow.get(i));
				if ((windowBegin+windowEnd)/2 >= dynamicWindowBegin){
					if( (windowBegin+windowEnd)/2 < dynamicWindowEnd){
						if (searchingEnd) {
							cpt ++;
						}
						else {
							searchingEnd = true;
							begin = i;
						}
						if (i == cooccurrenceTable.listCharA.size()-1){
							result.add(cooccurrenceTable.subTable(begin, i));
							done = true;
						}
					}
					else{
						if (searchingEnd){
							result.add(cooccurrenceTable.subTable(begin, begin+cpt));
							searchingEnd = false;
							cpt = 0;
						}
						break;
					}
				}
			}
			dynamicCpt++;
		}
		return result;
	}
	
	public List<CooccurrenceTable> dynamicTableParagraphs(int size, int covering){
		List<CooccurrenceTable> result = new LinkedList<>();
		boolean done = false;
		boolean searchingEnd = false;
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicWindowBegin = 0;
		int dynamicWindowEnd = 0;
		while(!done){
			dynamicWindowBegin = dynamicCpt*size - dynamicCpt*covering;
			dynamicWindowEnd = dynamicCpt*size - dynamicCpt*covering;
			for (int i = 0; i < cooccurrenceTable.listCharA.size(); i++){
				windowBegin = cooccurrenceTable.listBeginingWindow.get(i);
				windowEnd = cooccurrenceTable.listEndingWindow.get(i);
				if ((windowBegin+windowEnd)/2 >= dynamicWindowBegin){
					if( (windowBegin+windowEnd)/2 < dynamicWindowEnd){
						if (searchingEnd) {
							cpt ++;
						}
						else {
							searchingEnd = true;
							begin = i;
						}
						if (i == cooccurrenceTable.listCharA.size()-1){
							result.add(cooccurrenceTable.subTable(begin, i));
							done = true;
						}
					}
					else{
						if (searchingEnd){
							result.add(cooccurrenceTable.subTable(begin, begin+cpt));
							searchingEnd = false;
							cpt = 0;
						}
						break;
					}
				}
			}
			dynamicCpt++;
		}
		return result;
	}

	public List<CooccurrenceTable> dynamicTableChapters(int size, int covering){
		List<CooccurrenceTable> result = new LinkedList<>();
		boolean done = false;
		boolean searchingEnd = false;
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicWindowBegin = 0;
		int dynamicWindowEnd = 0;
		while(!done){
			dynamicWindowBegin = book.chapters.get(dynamicCpt*size - dynamicCpt*covering).paragraphs.getFirst().paragraphNumber;
			dynamicWindowEnd = book.chapters.get(dynamicCpt*size - dynamicCpt*covering).paragraphs.getLast().paragraphNumber;
			for (int i = 0; i < cooccurrenceTable.listCharA.size(); i++){
				windowBegin = cooccurrenceTable.listBeginingWindow.get(i);
				windowEnd = cooccurrenceTable.listEndingWindow.get(i);
				if ((windowBegin+windowEnd)/2 >= dynamicWindowBegin){
					if( (windowBegin+windowEnd)/2 < dynamicWindowEnd){
						if (searchingEnd) {
							cpt ++;
						}
						else {
							searchingEnd = true;
							begin = i;
						}
						if (i == cooccurrenceTable.listCharA.size()-1){
							result.add(cooccurrenceTable.subTable(begin, i));
							done = true;
						}
					}
					else{
						if (searchingEnd){
							result.add(cooccurrenceTable.subTable(begin, begin+cpt));
							searchingEnd = false;
							cpt = 0;
						}
						break;
					}
				}
			}
			dynamicCpt++;
		}
		return result;
	}
}
