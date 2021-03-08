package novelnet.pipeline;

import java.util.LinkedList;
import java.util.List;

import novelnet.book.Book;
import novelnet.book.Chapter;
import novelnet.table.CooccurrenceTableSentence;
import novelnet.util.CustomEntityMention;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.util.Pair;


/**
 * Find the co-occurrences with a window dimension set in sentences.
 * 
 * @author Quay Baptiste, Lemaire Tewis
 * 
 */
public class WindowingCooccurrenceSentence extends WindowingCooccurrence  {
	
	/**
	 * Class Constructor.
	 * 
	 * @param size size of the window used to find co-occurrences in.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential detection.
	 */
	public WindowingCooccurrenceSentence(int size, int covering, boolean chapterLimitation, Book book){
		super(size, covering, chapterLimitation, book);
	}
	
	/**
	 * Creates and returns a list of lists of tokens based on the size and covering chosen by the user
	 * 
	 * @param CoreDocument represents the text chosen by the user
	 * @return The list of lists of tokens
	 * 
	 */
	@Override
	public List<List<CustomEntityMention>> createWindow(){
		if (!book.getEntitiesPlaced()){
			book.placeEntitites();
		}
		if (chapterLimitation)return createWindowWithChapterLimitation();
		else return createWindowWithoutChapterLimitation();
	}

	private List<List<CustomEntityMention>> createWindowWithChapterLimitation(){
		List<CustomEntityMention> window = new LinkedList<>(); // List of tokens
		List<List<CustomEntityMention>> result = new LinkedList<>(); // List of lists of tokens
		int beginingSentence;
		int endingSentence;
		boolean done;
		int tmp;
		for (Chapter c : book.getChapters()){
			done = false;
			beginingSentence = c.getBeginingSentenceIndex();
			endingSentence = beginingSentence+size-1;
			if (endingSentence > c.getEndingSentenceIndex()) endingSentence = c.getEndingSentenceIndex();
			while(beginingSentence < c.getEndingSentenceIndex() && !done){
				for (CustomEntityMention entity : c.getEntities()){
					tmp = entity.getSentenceIndex();
					if (tmp >= beginingSentence && tmp <= endingSentence){
						entity.setWindow(new Pair<>(beginingSentence,endingSentence));
						window.add(entity);
					}
				}
				if (endingSentence < c.getEndingSentenceIndex()){
					result.add(window);
					window = new LinkedList<>();
					beginingSentence = endingSentence - covering + 1;
					endingSentence = beginingSentence + size - 1;
				}
				else {
					done = true;
					result.add(window);
					window = new LinkedList<>();
				}
			}
		}
		return result;
	}

	private List<List<CustomEntityMention>> createWindowWithoutChapterLimitation(){
		List<CustomEntityMention> window = new LinkedList<>(); // List of tokens
		List<List<CustomEntityMention>> result = new LinkedList<>(); // List of lists of tokens
		int beginingSentence = book.getBeginingSentenceIndex();
		int endingSentence = beginingSentence+size-1;
		boolean done = false;
		int tmp;
		while(beginingSentence < book.getEndingSentenceIndex() && !done){
			for (CustomEntityMention entity : book.getEntities()){
				tmp = entity.getSentenceIndex();
				if (tmp >= beginingSentence && tmp <= endingSentence){
					entity.setWindow(new Pair<>(beginingSentence,endingSentence));
					window.add(entity);
				}
			}
			if (endingSentence < book.getEndingSentenceIndex()){
				result.add(window);
				window = new LinkedList<>();
				beginingSentence = endingSentence - covering + 1;
				endingSentence = beginingSentence + size - 1;
				if (endingSentence > book.getEndingSentenceIndex()) endingSentence = book.getEndingSentenceIndex();
			}
			else {
				done = true;
				result.add(window);
			}
		}
		return result;
		
	}
	
	
	/**
	 * Creates and returns a CooccurrenceTableSentence.
	 * 
	 * @param document A CoreDocument represents the text chosen by the user
	 * @return The full table after adding all the lines
	 * 
	 */
	@Override
	public CooccurrenceTableSentence createTab() {
		List<List<CustomEntityMention>> result = createWindow(); // We get the list of lists of tokens
		Pair<Integer,Integer> charOffsetA;
		Pair<Integer,Integer> charOffsetB;
		CustomEntityMention characterA;
		CustomEntityMention characterB;
		int distanceChar = 0; // We create an int for the distance between characters in characters
		int distanceWord = 0; // We create an int for the distance between characters in words
		CooccurrenceTableSentence tab = new CooccurrenceTableSentence(); // Creates a new table
		CoreDocument document = book.getDocument();
		for (List<CustomEntityMention> entities : result){ // For each window.
			for (int i = 0; i < entities.size(); i++){ // For each token for Character A
				characterA = entities.get(i);
				for (int j = i+1; j < entities.size(); j++){ // For each token for Character B
					characterB = entities.get(j);
					if (!characterA.getBestName().equals(characterB.getBestName())){
						charOffsetA = characterA.charOffsets();
						charOffsetB = characterB.charOffsets();
						
						distanceChar = charOffsetB.first() - charOffsetA.second();	// We get the distance between the two tokens in characters
						if (distanceChar < 0) {
							distanceChar = charOffsetA.first() - charOffsetB.second();
							distanceWord = document.tokens().indexOf(characterA.getBeginToken()) - document.tokens().indexOf(characterB.getEndToken()) -1; // We get the distance between the two tokens in words
						}
						else distanceWord = document.tokens().indexOf(characterB.getBeginToken()) - document.tokens().indexOf(characterA.getEndToken()) -1; // We get the distance between the two tokens in words						
						tab.add(characterA.getBestName(), characterB.getBestName(), distanceChar, distanceWord, characterA.getWindowBegining(), characterA.getWindowEnding(), null); // If the two strings aren't equal we add a line to the Table
					}
				}
			}
		}
		return tab; // Returns finished table
	}
}
