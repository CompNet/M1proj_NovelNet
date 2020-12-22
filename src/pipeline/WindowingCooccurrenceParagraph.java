package pipeline;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import book.Book;
import book.Chapter;
import book.CreateBook;

public class WindowingCooccurrenceParagraph extends WindowingCooccurrence{

	boolean chapterLimitation;
	Book book;

	/**
	 * Constructor for the class WindowingCooccurrencesParagraph
	 * 
	 * @param chapterLimitation	if true the cooccurrence by paragraphs will stop on the end of a chapter
	 * @param book used to create a border between chapters and paragraphs
	 * @param size Window's size set by the user
	 * @param covering Window's covering set by the user
	 */
	public WindowingCooccurrenceParagraph(boolean chapterLimitation, Book book, int size, int covering){
		super(size, covering);
		this.chapterLimitation = chapterLimitation;
		this.book = book;
	}

	/**
	 * Creates and returns a list of lists of tokens based on the size and covering chosen by the user
	 * 
	 * @param CoreDocument represents the text chosen by the user
	 * @return The list of lists of tokens
	 * 
	 */
	@Override
	public List<List<CoreLabel>> createWindow(CoreDocument document) {
		book = CreateBook.createBook(document);
		List<CoreLabel> window = new LinkedList<>(); // List of tokens
		List<List<CoreLabel>> result = new LinkedList<>(); // List of lists of tokens
		int cpt = 0; // We set a counter for the size
		for (Chapter chapter : book.getChapters()){ // For each chapters 
			if (chapterLimitation) cpt = 0; // If chapterLimitation is true we reset the counter
			for (int i = 0 ; i < chapter.getParagraphs().size(); i++){ // For the paragraphs
				for (int j = 0 ; j < chapter.getParagraphs().get(i).getSentences().size(); j++){ // For the sentences in paragraphs
					List<CoreLabel> sentence = chapter.getParagraphs().get(i).getSentences().get(j).tokens(); // We add the sentence to the list of sentences
					for (CoreLabel token : sentence) { // For each of tokens in the sentence
						window.add(token); // We add a token to the list of tokens
					}
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
		return result; // Returns list of lists of tokens
	}
	
	
	/**
	 * Creates and returns a tab with a String Character A, a String Character B, an int Distance in characcter and an int Distance in words
	 * 
	 * @param document A CoreDocument represents the text chosen by the user
	 * @return The full table after adding all the lines
	 * 
	 */
	@Override
	public CooccurrenceTableParagraph createTab(CoreDocument document) {
		List<List<CoreLabel>> result = createWindow(document); // We get the list of lists of tokens
		String charA = null; // We create a string for Character A
		String charB = null; // We create a string for Character B
		CorefChain tempA; // We create a CorefChain for Character A
		CorefChain tempB; // We create a CorefChain for Character B
		int distanceChar = 0; // We create an int for the distance between characters in characters
		int distanceWord = 0; // We create an int for the distance between characters in words
		int beginingParagraph = 0;
		int endingParagraph = 0;
		int cpt = 0;
		CooccurrenceTableParagraph tab = new CooccurrenceTableParagraph();
		Map<Integer, CorefChain> corefChains = document.corefChains(); //We create a map of corefChains (each represents a set of mentions which corresponds to the same entity)
		for (List<CoreLabel> tokens : result){ // For each token in the list
			beginingParagraph = cpt*size-(cpt*covering);
			endingParagraph = (cpt+1)*size-(cpt*covering)-1;
			for (CoreLabel tokenA : tokens){ // For each token for Character A
				if(tokenA.ner().equals("PERSON")){ // If the token is considered a person
					for (CoreLabel tokenB : tokens){ // For each token for Character B
						if(tokenB.ner().equals("PERSON")){ // If the token is considered a person
							distanceChar = tokenA.beginPosition() - tokenB.beginPosition();	// We get the distance between the two tokens in characters
							distanceWord = tokens.indexOf(tokenA) - tokens.indexOf(tokenB); // We get the distance between the two tokens in words
							if (!(tokenA.equals(tokenB)) && distanceChar > 0) { // if the two tokens are equals or if the distance is below or equal 0
								tempA = ImpUtils.corefByToken(corefChains, tokenA); // We get the corresponding corefChain for token A
								tempB = ImpUtils.corefByToken(corefChains, tokenB); // We get the corresponding corefChain for token B
								if (tempA != null && tempB != null) { // If both aren't empty
									charA = tempA.getRepresentativeMention().mentionSpan; // We assign the string of the most representative mention to Character A
									charB = tempB.getRepresentativeMention().mentionSpan; // We assign the string of the most representative mention to Character B
									if (!charA.equals(charB)) tab.add(charA, charB, distanceChar, distanceWord, beginingParagraph, endingParagraph); // If the two strings aren't equal we add a line to the Table
								}
							}
						}
					}
				}
			}
			cpt++;
		}
		return tab; // Returns finished table
	}
}