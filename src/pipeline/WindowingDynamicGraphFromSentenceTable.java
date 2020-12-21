package pipeline;

import java.util.LinkedList;
import java.util.List;

public class WindowingDynamicGraphFromSentenceTable implements WindowingDynamicGraph {
    
    Book book;
	CooccurrenceTable cooccurrenceTable;

    protected WindowingDynamicGraphFromSentenceTable(Book book, CooccurrenceTable cooccurrenceTable) {
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
		CooccurrenceTableSentence tmp;
		boolean done = false;
		boolean searchEnd = false;
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicWindowBegin = 0;
		int dynamicWindowEnd = 0;
		while(!done){
			dynamicWindowBegin = dynamicCpt*size - dynamicCpt*covering;
			System.out.println("wb " + dynamicWindowBegin);
			dynamicWindowEnd = (dynamicCpt + 1)*size - dynamicCpt*covering -1;
			System.out.println("we " + dynamicWindowEnd);
			for (int i = 0; i < cooccurrenceTable.listCharA.size(); i++){
				windowBegin = cooccurrenceTable.listBeginingWindow.get(i);
				windowEnd = cooccurrenceTable.listEndingWindow.get(i);
				if ((windowBegin+windowEnd)/2 >= dynamicWindowBegin){
					if( (windowBegin+windowEnd)/2 < dynamicWindowEnd){
						if (searchEnd) {
							cpt ++;
							if (i == cooccurrenceTable.listCharA.size()-1){
								result.add(cooccurrenceTable.subTable(begin, i));
								done = true;
							}
						}
						else {
							if (i == cooccurrenceTable.listCharA.size()-1){
								result.add(cooccurrenceTable.subTable(i, i));
								done = true;
								break;
							}
							searchEnd = true;
							begin = i;
						}
					}
					else if (searchEnd){
						result.add(cooccurrenceTable.subTable(begin, begin+cpt));
						searchEnd = false;
						cpt = 0;
						break;
					}
					else break;
				}
			}
			dynamicCpt++;
		}
		return result;
	}
	
	public List<CooccurrenceTable> dynamicTableParagraphs(int size, int covering){
		List<CooccurrenceTable> result = new LinkedList<>();
		return result;
	};

	public List<CooccurrenceTable> dynamicTableChapters(int size, int covering){
		List<CooccurrenceTable> result = new LinkedList<>();
		return result;
	};
}
