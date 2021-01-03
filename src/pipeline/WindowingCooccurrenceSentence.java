/**
 * 
 */
package pipeline;

import java.util.LinkedList;
import java.util.List;

import book.Book;
import book.Chapter;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.util.Pair;
import util.EntityMention;

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
	public List<List<EntityMention>> createWindow(){
		if (!book.getEntitiesPlaced()){
			book.placeEntitites();
		}
		if (chapterLimitation)return createWindowWithChapterLimitation();
		else return createWindowWithoutChapterLimitation();
	}

	private List<List<EntityMention>> createWindowWithChapterLimitation(){
		List<EntityMention> window = new LinkedList<>(); // List of tokens
		List<List<EntityMention>> result = new LinkedList<>(); // List of lists of tokens
		int beginingSentence;
		int endingSentence;
		boolean done;
		for (Chapter c : book.getChapters()){
			done = false;
			beginingSentence = c.getBeginingSentence();
			endingSentence = beginingSentence+size-1;
			if (endingSentence > c.getEndingSentence()) endingSentence = c.getEndingSentence();
			while(beginingSentence < c.getEndingSentence() && !done){
				for (EntityMention entity : c.getEntities()){
					CoreLabel tmp = entity.getCoreEntityMention().tokens().get(0);
					if (tmp.sentIndex() >= beginingSentence && tmp.sentIndex() <= endingSentence){
						entity.setWindow(new Pair<Integer,Integer>(beginingSentence,endingSentence));
						window.add(entity);
					}
				}
				if (endingSentence < c.getEndingSentence()){
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

	private List<List<EntityMention>> createWindowWithoutChapterLimitation(){
		List<EntityMention> window = new LinkedList<>(); // List of tokens
		List<List<EntityMention>> result = new LinkedList<>(); // List of lists of tokens
		int beginingSentence = book.getBeginingSentence();
		int endingSentence = beginingSentence+size-1;
		boolean done = false;
		while(beginingSentence < book.getEndingSentence() && !done){
			for (EntityMention entity : book.getEntities()){
				CoreLabel tmp = entity.getCoreEntityMention().tokens().get(0);
				if (tmp.sentIndex() >= beginingSentence && tmp.sentIndex() <= endingSentence){
					entity.setWindow(new Pair<Integer,Integer>(beginingSentence,endingSentence));
					window.add(entity);
				}
			}
			if (endingSentence < book.getEndingSentence()){
				result.add(window);
				window = new LinkedList<>();
				beginingSentence = endingSentence - covering + 1;
				endingSentence = beginingSentence + size - 1;
				if (endingSentence > book.getEndingSentence()) endingSentence = book.getEndingSentence();
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
		List<List<EntityMention>> result = createWindow(); // We get the list of lists of tokens
		List<CoreLabel> tokenListA;
		List<CoreLabel> tokenListB;
		EntityMention characterA;
		EntityMention characterB;
		int distanceChar = 0; // We create an int for the distance between characters in characters
		int distanceWord = 0; // We create an int for the distance between characters in words
		CooccurrenceTableSentence tab = new CooccurrenceTableSentence(); // Creates a new table
		CoreDocument document = book.getDocument();
		for (List<EntityMention> entities : result){ // For each window.
			for (int i = 0; i < entities.size(); i++){ // For each token for Character A
				characterA = entities.get(i);
				for (int j = i+1; j < entities.size(); j++){ // For each token for Character B
					characterB = entities.get(j);
					if (!characterA.getBestName().equals(characterB.getBestName())){ // if the distance is strictly superior to 0
						tokenListA = characterA.getCoreEntityMention().tokens();
						tokenListB = characterB.getCoreEntityMention().tokens();
						
						distanceChar = tokenListB.get(0).beginPosition() - tokenListA.get(tokenListA.size()-1).endPosition();	// We get the distance between the two tokens in characters
						if (distanceChar < 0) {
							distanceChar = tokenListA.get(0).beginPosition() - tokenListB.get(tokenListB.size()-1).endPosition();
							distanceWord = document.tokens().indexOf(tokenListA.get(0)) - document.tokens().indexOf(tokenListB.get(tokenListB.size()-1)) -1; // We get the distance between the two tokens in words
						}
						else distanceWord = document.tokens().indexOf(tokenListB.get(0)) - document.tokens().indexOf(tokenListA.get(tokenListA.size()-1)) -1; // We get the distance between the two tokens in words						
						tab.add(characterA.getBestName(), characterB.getBestName(), distanceChar, distanceWord, characterA.getWindowBegining(), characterA.getWindowEnding()); // If the two strings aren't equal we add a line to the Table
					}
				}
			}
		}
		return tab; // Returns finished table
	}
}
