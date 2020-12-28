/**
 * 
 */
package pipeline;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import book.Book;
import book.Chapter;
import book.Paragraph;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;

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
	public List<List<CoreLabel>> createWindow(){
		List<CoreLabel> window = new LinkedList<>(); // List of tokens
		List<List<CoreLabel>> result = new LinkedList<>(); // List of lists of tokens
		CoreSentence sentence;
		int cpt = 0; // We set a counter for the size
		for (Chapter c : book.getChapters()){
			if (chapterLimitation){
				result.add(window); // We add the list of tokens to the list of lists of tokens
				window = new LinkedList<>(); // We reset the list of tokens
				cpt = 0; // We reset the counter
			}
			for (Paragraph p : c.getParagraphs()){
				for (int i=0; i< p.getSentences().size(); i++){
					sentence = p.getSentences().get(i);
					for (CoreLabel token : sentence.tokens()) { // For each of tokens in the sentence
						window.add(token); // We add a token to the list of tokens
					}
					cpt++; // We increment one to the counter
					if (cpt == size){ // If counter reaches given size
						result.add(window); // We add the list of tokens to the list of lists of tokens
						window = new LinkedList<>(); // We reset the list of tokens
						cpt = 0; // We reset the counter
						i -= covering; // We apply the given covering
					}
				}
			}
		}
		return result; // Returns list of lists of tokens
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
		List<List<CoreLabel>> result = createWindow(); // We get the list of lists of tokens
		String charA = null; // We create a string for Character A
		String charB = null; // We create a string for Character B
		CorefChain tempA; // We create a CorefChain for Character A
		CorefChain tempB; // We create a CorefChain for Character B
		int distanceChar = 0; // We create an int for the distance between characters in characters
		int distanceWord = 0; // We create an int for the distance between characters in words
		int beginingSentence = 0;
		int endingSentence = 0;
		CooccurrenceTableSentence tab = new CooccurrenceTableSentence(); // Creates a new table
		Map<Integer, CorefChain> corefChains = document.corefChains(); // We create a map of corefChains (each represents a set of mentions which corresponds to the same entity)
		for (List<CoreLabel> tokens : result){ // For each sentence in the window.
			beginingSentence = tokens.get(0).sentIndex();
			endingSentence = tokens.get(tokens.size()-1).sentIndex();
			for (CoreLabel tokenA : tokens){ // For each token for Character A
				if(tokenA.ner().equals("PERSON")){ // If the token is considered a person
					for (CoreLabel tokenB : tokens){ // For each token for Character B
						if(tokenB.ner().equals("PERSON")){ // If the token is considered a person
							distanceChar = tokenA.beginPosition() - tokenB.beginPosition();	// We get the distance between the two tokens in characters
							distanceWord = tokens.indexOf(tokenA) - tokens.indexOf(tokenB); // We get the distance between the two tokens in words
							if ( distanceChar > 0) { // if the distance is strictly superior to 0
								tempA = ImpUtils.corefByToken(corefChains, tokenA); // We get the corresponding corefChain for token A
								tempB = ImpUtils.corefByToken(corefChains, tokenB); // We get the corresponding corefChain for token B
								if (tempA != null && tempB != null) { // If both aren't empty
									charA = tempA.getRepresentativeMention().mentionSpan; // We assign the string of the most representative mention to Character A
									charB = tempB.getRepresentativeMention().mentionSpan; // We assign the string of the most representative mention to Character B
									if (!charA.equals(charB)) tab.add(charA, charB, distanceChar, distanceWord, beginingSentence, endingSentence); // If the two strings aren't equal we add a line to the Table
								}
							}
						}
					}
				}
			}
		}
		return tab; // Returns finished table
	}
}
