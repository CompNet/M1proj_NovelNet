package pipeline;

import java.util.LinkedList;
import java.util.List;

import book.Book;

/**
 * Create dynamic graphs from a CooccurrenceTable where the window dimension is in Paragraphs.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class WindowingDynamicGraphFromParagraphTable extends WindowingDynamicGraph {

	/**
	 * Class Constructor
	 * 
	 * @param book the Book created from the original text.
	 * @param cooccurrenceTable the co-occurrence table you want to create the dynamic graphs from.
	*/
    public WindowingDynamicGraphFromParagraphTable(Book book, CooccurrenceTableParagraph cooccurrenceTable) {
		super(book, cooccurrenceTable);
	}

	/**
	 * Create a list of CooccurrenceTable to make graphs from.
	 * 
	 * @param size size of the window (in sentences) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	@Override
	public List<CooccurrenceTable> dynamicTableSentences(int size, int covering){
		List<CooccurrenceTable> result = new LinkedList<>();
		boolean done = false;
		boolean searchingEnd = false;
		boolean whileEnd;
		int i;
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicGraphBegin = 0;
		int dynamicGraphEnd = 0;
		while(!done){
			i = 0;
			whileEnd = false;
			dynamicGraphBegin = dynamicCpt*size - dynamicCpt*covering;
			dynamicGraphEnd = (dynamicCpt + 1)*size - dynamicCpt*covering -1;
			//System.out.println("dynamicGraphBegin, dynamicGraphEnd : " + dynamicGraphBegin + ", " + dynamicGraphEnd);
			while( i < cooccurrenceTable.listCharA.size() && !whileEnd){
				windowBegin = book.getBeginIndexOfParagraph(cooccurrenceTable.listBeginingWindow.get(i));
				windowEnd = book.getEndIndexOfParagraph(cooccurrenceTable.listEndingWindow.get(i));
				if (windowEnd==-1) windowEnd = book.getEndIndexOfParagraph(book.getEndingParagraph());
				//System.out.println("windowBegin, windowEnd : " + windowBegin + ", " + windowEnd);
				if ((windowBegin+windowEnd)/2 >= dynamicGraphBegin){
					if( (windowBegin+windowEnd)/2 <= dynamicGraphEnd){
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
						whileEnd = true;
					}
				}
				i++;
			}
			dynamicCpt++;
		}
		return result;
	}

	/**
	 * Create a list of CooccurrenceTable to make graphs from.
	 * 
	 * @param size size of the window (in Paragraphs) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	public List<CooccurrenceTable> dynamicTableParagraphs(int size, int covering){
		List<CooccurrenceTable> result = new LinkedList<>();
		boolean done = false;
		boolean searchingEnd = false;
		boolean whileEnd;
		int begin = 0;
		int cpt = 0;
		int i;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicGraphBegin = 0;
		int dynamicGraphEnd = 0;
		while(!done){
			dynamicGraphBegin = dynamicCpt*size - dynamicCpt*covering;
			dynamicGraphEnd = (dynamicCpt+1)*size - dynamicCpt*covering-1;
			i = 0;
			whileEnd = false;
			while( i < cooccurrenceTable.listCharA.size() && !whileEnd){
				windowBegin = cooccurrenceTable.listBeginingWindow.get(i);
				windowEnd = cooccurrenceTable.listEndingWindow.get(i);
				if (windowEnd==-1) windowEnd = book.getEndIndexOfParagraph(book.getEndingParagraph());
				if ((windowBegin+windowEnd)/2 >= dynamicGraphBegin){
					if( (windowBegin+windowEnd)/2 <= dynamicGraphEnd){
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
						whileEnd = true;
					}
				}
				i++;
			}
			dynamicCpt++;
		}
		return result;
	}

	/**
	 * Create a list of CooccurrenceTable to make graphs from.
	 * 
	 * @param size size of the window (in Chapters) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	@Override
	public List<CooccurrenceTable> dynamicTableChapters(int size, int covering){
		List<CooccurrenceTable> result = new LinkedList<>();
		boolean done = false;
		boolean searchingEnd = false;
		boolean whileEnd;
		int i;
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicGraphBegin = 0;
		int dynamicGraphEnd = 0;
		while(!done){
			i=0;
			whileEnd = false;
			dynamicGraphBegin = book.getChapters().get(dynamicCpt*size - dynamicCpt*covering).getParagraphs().getFirst().getParagraphNumber();
			dynamicGraphEnd = book.getChapters().get((dynamicCpt+1)*size - dynamicCpt*covering-1).getParagraphs().getLast().getParagraphNumber();
			//System.out.println("dynamicGraphBegin, dynamicGraphEnd : " + dynamicGraphBegin + ", " + dynamicGraphEnd);
			while(i < cooccurrenceTable.listCharA.size() && !whileEnd){
				windowBegin = cooccurrenceTable.listBeginingWindow.get(i);
				windowEnd = cooccurrenceTable.listEndingWindow.get(i);
				if (windowEnd==-1) windowEnd = book.getEndIndexOfParagraph(book.getEndingParagraph());
				//System.out.println("windowBegin, windowEnd : " + windowBegin + ", " + windowEnd);
				if ((windowBegin+windowEnd)/2 >= dynamicGraphBegin){
					//System.out.println("1");
					if( (windowBegin+windowEnd)/2 <= dynamicGraphEnd){
						//System.out.println("2");
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
						whileEnd = true;
					}
				}
				i++;
			}
			dynamicCpt++;
		}
		return result;
	}
}
