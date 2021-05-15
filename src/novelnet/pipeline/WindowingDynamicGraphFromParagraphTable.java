package novelnet.pipeline;

import java.util.LinkedList;
import java.util.List;

import novelnet.book.Book;
import novelnet.book.Paragraph;
import novelnet.table.CooccurrenceTableParagraph;
import novelnet.table.InteractionTable;

/**
 * Create dynamic graphs from a cooccurrenceTableParagraph where the window dimension is in Paragraphs.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class WindowingDynamicGraphFromParagraphTable extends WindowingDynamicGraph {

	/**
	 * Class Constructor
	 * 
	 * @param book the Book created from the original text.
	 * @param cooccurrenceTableParagraph the co-occurrence table you want to create the dynamic graphs from.
	*/
    public WindowingDynamicGraphFromParagraphTable(Book book, InteractionTable cooccurrenceTableParagraph) {
		super(book, cooccurrenceTableParagraph);
	}

	/**
	 * Create a list of cooccurrenceTableParagraph to make graphs from.
	 * 
	 * @param size size of the window (in sentences) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	@Override
	public List<InteractionTable> dynamicTableSentences(int size, int covering){
		List<InteractionTable> result = new LinkedList<>();
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
			while( i < interactionTable.getListCharA().size() && !whileEnd){
				windowBegin = book.getBeginIndexOfParagraph(interactionTable.getListBeginingWindow().get(i));
				windowEnd = book.getEndIndexOfParagraph(interactionTable.getListEndingWindow().get(i));
				if (windowEnd==-1) windowEnd = book.getEndIndexOfParagraph(book.getEndingParagraphNumber());
				if ((windowBegin+windowEnd)/2 >= dynamicGraphBegin){
					if( (windowBegin+windowEnd)/2 <= dynamicGraphEnd){
						if (searchingEnd) {
							cpt ++;
						}
						else {
							searchingEnd = true;
							begin = i;
						}
						if (i == interactionTable.getListCharA().size()-1){
							result.add(interactionTable.subTable(begin, i));
							done = true;
						}
					}
					else{
						if (searchingEnd){
							result.add(interactionTable.subTable(begin, begin+cpt));
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
	 * Create a list of cooccurrenceTableParagraph to make graphs from.
	 * 
	 * @param size size of the window (in Paragraphs) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	@Override
	public List<InteractionTable> dynamicTableParagraphs(int size, int covering){
		List<InteractionTable> result = new LinkedList<>();
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
			while( i < interactionTable.getListCharA().size() && !whileEnd){
				windowBegin = interactionTable.getListBeginingWindow().get(i);
				windowEnd = interactionTable.getListEndingWindow().get(i);
				if (windowEnd==-1) windowEnd = book.getEndIndexOfParagraph(book.getEndingParagraphNumber());
				if ((windowBegin+windowEnd)/2 >= dynamicGraphBegin){
					if( (windowBegin+windowEnd)/2 <= dynamicGraphEnd){
						if (searchingEnd) {
							cpt ++;
						}
						else {
							searchingEnd = true;
							begin = i;
						}
						if (i == interactionTable.getListCharA().size()-1){
							result.add(interactionTable.subTable(begin, i));
							done = true;
						}
					}
					else{
						if (searchingEnd){
							result.add(interactionTable.subTable(begin, begin+cpt));
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
	 * Create a list of cooccurrenceTableParagraph to make graphs from.
	 * 
	 * @param size size of the window (in Chapters) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	@Override
	public List<InteractionTable> dynamicTableChapters(int size, int covering){
		List<InteractionTable> result = new LinkedList<>();
		List<Paragraph> paragraphs;
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
			dynamicGraphBegin = book.getChapters().get(dynamicCpt*size - dynamicCpt*covering).getParagraphs().get(0).getParagraphNumber();
			
			paragraphs = book.getChapters().get((dynamicCpt+1)*size - dynamicCpt*covering-1).getParagraphs();
			dynamicGraphEnd = paragraphs.get(paragraphs.size()-1).getParagraphNumber();
			
			while(i < interactionTable.getListCharA().size() && !whileEnd){
				windowBegin = interactionTable.getListBeginingWindow().get(i);
				windowEnd = interactionTable.getListEndingWindow().get(i);
				if (windowEnd==-1) windowEnd = book.getEndIndexOfParagraph(book.getEndingParagraphNumber());
				if ((windowBegin+windowEnd)/2 >= dynamicGraphBegin){
					if( (windowBegin+windowEnd)/2 <= dynamicGraphEnd){
						if (searchingEnd) {
							cpt ++;
						}
						else {
							searchingEnd = true;
							begin = i;
						}
						if (i == interactionTable.getListCharA().size()-1){
							result.add(interactionTable.subTable(begin, i));
							done = true;
						}
					}
					else{
						if (searchingEnd){
							result.add(interactionTable.subTable(begin, begin+cpt));
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
