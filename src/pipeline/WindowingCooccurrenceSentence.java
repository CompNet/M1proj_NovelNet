/**
 * 
 */
package pipeline;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import book.Book;
import book.Chapter;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.util.Pair;
import util.EntityMention;
import util.ImpUtils;

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
	public List<List<EntityMention>> createWindow(CoreDocument document){
		if (!book.getEntitiesPlaced()){
			book.placeEntitites(findEntity(document));
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
			while(beginingSentence < c.getEndingSentence() && !done){
				for (CoreEntityMention entity : c.getEntities()){
					CoreLabel tmp = entity.tokens().get(0);
					if (tmp.sentIndex() >= beginingSentence && tmp.sentIndex() <= endingSentence){
						window.add(new EntityMention(entity, new Pair<Integer,Integer>(beginingSentence,endingSentence)));
					}
				}
				if (endingSentence <= c.getEndingSentence()){
					result.add(window);
					window = new LinkedList<>();
					beginingSentence = endingSentence - covering + 1;
					endingSentence = beginingSentence + size - 1;
				}
				else {
					done = true;
					result.add(window);
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
			for (CoreEntityMention entity : book.getEntities()){
				CoreLabel tmp = entity.tokens().get(0);
				if (tmp.sentIndex() >= beginingSentence && tmp.sentIndex() <= endingSentence){
					window.add(new EntityMention(entity, new Pair<Integer,Integer>(beginingSentence,endingSentence)));
				}
			}
			if (endingSentence <= book.getEndingSentence()){
				result.add(window);
				window = new LinkedList<>();
				beginingSentence = endingSentence - covering + 1;
				endingSentence = beginingSentence + size - 1;
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
	public CooccurrenceTableSentence createTab(CoreDocument document) {
		List<List<EntityMention>> result = createWindow(document); // We get the list of lists of tokens
		String charA = null; // We create a string for Character A
		String charB = null; // We create a string for Character B
		CorefChain tempA; // We create a CorefChain for Character A
		CorefChain tempB; // We create a CorefChain for Character B
		List<CoreLabel> tokenListA;
		List<CoreLabel> tokenListB;
		EntityMention characterA;
		EntityMention characterB;
		int distanceChar = 0; // We create an int for the distance between characters in characters
		int distanceWord = 0; // We create an int for the distance between characters in words
		CooccurrenceTableSentence tab = new CooccurrenceTableSentence(); // Creates a new table
		Map<Integer, CorefChain> corefChains = document.corefChains(); // We create a map of corefChains (each represents a set of mentions which corresponds to the same entity)
		for (List<EntityMention> entities : result){ // For each window.
			for (int i = 0; i < entities.size(); i++){ // For each token for Character A
				characterA = entities.get(i);
				for (int j = i+1; j < entities.size(); j++){ // For each token for Character B
					characterB = entities.get(j);
					if (!ImpUtils.sameCharacter(characterA.getCoreEntityMention(), characterB.getCoreEntityMention(), document)){ // if the distance is strictly superior to 0
						tokenListA = characterA.getCoreEntityMention().tokens();
						tokenListB = characterB.getCoreEntityMention().tokens();
						
						/*System.out.println("charA :\t" + characterA.getCoreEntityMention().text() + "\tfin :\t" +tokenListA.get(tokenListA.size()-1).endPosition() );
						System.out.println("charB :\t" + characterB.getCoreEntityMention().text() + "\tdéb :\t" +tokenListB.get(0).beginPosition());
						System.out.println("fin-début :\t" + (tokenListA.get(tokenListA.size()-1).endPosition() - tokenListB.get(0).beginPosition()));
						*/
						distanceChar = tokenListB.get(0).beginPosition() - tokenListA.get(tokenListA.size()-1).endPosition();	// We get the distance between the two tokens in characters
						if (distanceChar < 0) {
							distanceChar = tokenListA.get(0).beginPosition() - tokenListB.get(tokenListB.size()-1).endPosition();
							distanceWord = document.tokens().indexOf(tokenListA.get(0)) - document.tokens().indexOf(tokenListB.get(tokenListB.size()-1)) -1; // We get the distance between the two tokens in words
						}
						else distanceWord = document.tokens().indexOf(tokenListB.get(0)) - document.tokens().indexOf(tokenListA.get(tokenListA.size()-1)) -1; // We get the distance between the two tokens in words
						
						tempA = ImpUtils.corefByEntityMention(corefChains, characterA.getCoreEntityMention()); // We get the corresponding corefChain for token A
						tempB = ImpUtils.corefByEntityMention(corefChains, characterB.getCoreEntityMention()); // We get the corresponding corefChain for token B
						
						if(tempA != null) charA = tempA.getRepresentativeMention().mentionSpan; // We assign the string of the most representative mention to Character A
						else charA = characterA.getCoreEntityMention().text();
						if (tempB != null) charB = tempB.getRepresentativeMention().mentionSpan; // We assign the string of the most representative mention to Character B
						else charB = characterB.getCoreEntityMention().text();
						
						tab.add(charA, charB, distanceChar, distanceWord, characterA.getWindowBegining(), characterA.getWindowEnding()); // If the two strings aren't equal we add a line to the Table
					}
				}
			}
		}
		return tab; // Returns finished table
	}
}
