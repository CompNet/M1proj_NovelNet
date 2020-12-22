package pipeline;

import java.util.LinkedList;
import java.util.List;

import book.Book;

/**
 * Create dynamic graphs from a CooccurrenceTable where the window dimension is in Sentence.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class WindowingDynamicGraphFromSentenceTable extends WindowingDynamicGraph {

	/**
	 * Class Constructor
	 * 
	 * @param book the Book created from the original text.
	 * @param cooccurrenceTable the co-occurrence table you want to create the dynamic graphs from.
	*/
    public WindowingDynamicGraphFromSentenceTable(Book book, CooccurrenceTableSentence cooccurrenceTable) {
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
	
	/**
	 * Create a list of CooccurrenceTable to make graphs from.
	 * 
	 * @param size size of the window (in Paragraphs) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	@Override
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
			dynamicWindowBegin = book.getBeginIndexOfParagraph(dynamicCpt*size - dynamicCpt*covering);
			dynamicWindowEnd = book.getEndIndexOfParagraph((dynamicCpt + 1)*size - dynamicCpt*covering -1);
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
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicWindowBegin = 0;
		int dynamicWindowEnd = 0;
		while(!done){
			dynamicWindowBegin = book.getBeginIndexOfChapter(dynamicCpt*size - dynamicCpt*covering);
			dynamicWindowEnd = book.getEndIndexOfChapter((dynamicCpt + 1)*size - dynamicCpt*covering -1);
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
