package pipeline;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.util.Pair;
import util.EntityMention;
import util.ImpUtils;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import book.Book;
import book.Chapter;
import book.Paragraph;

/**
 * Find the co-occurrences with a window dimension set in paragraphs.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
 *
 */
public class WindowingCooccurrenceParagraph extends WindowingCooccurrence{


	/**
	 * Class Contructor
	 * 
	 * @param chapterLimitation	if true the cooccurrence by paragraphs will stop on the end of a chapter.
	 * @param book the book you want to detect co-occurrences from.
	 * @param size size of the window used to find co-occurrences in.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential detection.
	 */
	public WindowingCooccurrenceParagraph( int size, int covering, boolean chapterLimitation, Book book){
		super(size, covering, chapterLimitation, book);
	}

	/**
	 * Creates and returns a list of lists of tokens based on the size and covering chosen by the user.
	 * 
	 * @param CoreDocument represents the text chosen by the user.
	 * @return The list of lists of tokens.
	 * 
	 */
	@Override
	public List<List<EntityMention>> createWindow(CoreDocument document) {
		if (!book.getEntitiesPlaced()){
			book.placeEntitites(findEntity(document));
		}
		if (chapterLimitation)return createWindowWithChapterLimitation();
		else return createWindowWithoutChapterLimitation();
	}

	private List<List<EntityMention>> createWindowWithChapterLimitation(){
		List<EntityMention> window = new LinkedList<>(); // List of tokens
		List<List<EntityMention>> result = new LinkedList<>(); // List of lists of tokens
		int beginingParagraph;
		int endingParagraph;
		boolean done;
		for (Chapter c : book.getChapters()){
			done = false;
			beginingParagraph = c.getBeginingParagraph();
			endingParagraph = beginingParagraph+size-1;
			while(beginingParagraph < c.getEndingParagraph() && !done){
				for (CoreEntityMention entity : c.getEntities()){	
					CoreLabel tmp = entity.tokens().get(0);
					Paragraph p = book.getParagraph(endingParagraph);
					if (p==null) p = book.getParagraph(book.getEndingParagraph());
					if (tmp.sentIndex() >= book.getParagraph(beginingParagraph).getBeginingSentence() && tmp.sentIndex() <= p.getEndingSentence()){
						window.add(new EntityMention(entity, new Pair<Integer,Integer>(beginingParagraph,endingParagraph)));
					}
				}
				if (endingParagraph < c.getEndingParagraph()){
					result.add(window);
					window = new LinkedList<>();
					beginingParagraph = endingParagraph - covering + 1;
					endingParagraph = beginingParagraph + size - 1;
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
		int beginingParagraph = 0;
		int endingParagraph = beginingParagraph+size-1;
		boolean done = false;
		while(beginingParagraph < book.getEndingParagraph() && !done){
			for (CoreEntityMention entity : book.getEntities()){
				CoreLabel tmp = entity.tokens().get(0);
				Paragraph p = book.getParagraph(endingParagraph);
				if (p==null) p = book.getParagraph(book.getEndingParagraph());
				if (tmp.sentIndex() >= book.getParagraph(beginingParagraph).getBeginingSentence() && tmp.sentIndex() <= p.getEndingSentence()){
					window.add(new EntityMention(entity, new Pair<Integer,Integer>(beginingParagraph,endingParagraph)));
				}
			}
			if (endingParagraph < book.getEndingParagraph()){
				result.add(window);
				window = new LinkedList<>();
				beginingParagraph = endingParagraph - covering + 1;
				endingParagraph = beginingParagraph + size - 1;
			}
			else {
				done = true;
				result.add(window);
			}
		}
		return result;
	}
	
	/**
	 * Creates and returns a CooccurrenceTableParagraph.
	 * 
	 * @param document A CoreDocument represents the text chosen by the user
	 * @return The full table after adding all the lines
	 * 
	 */
	@Override
	public CooccurrenceTableParagraph createTab(CoreDocument document) {
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
		CooccurrenceTableParagraph tab = new CooccurrenceTableParagraph(); // Creates a new table
		Map<Integer, CorefChain> corefChains = document.corefChains(); // We create a map of corefChains (each represents a set of mentions which corresponds to the same entity)
		for (List<EntityMention> entities : result){ // For each window.
			for (int i = 0; i < entities.size(); i++){ // For each token for Character A
				characterA = entities.get(i);
				for (int j = i+1; j < entities.size(); j++){ // For each token for Character B
					characterB = entities.get(j);
					if (!ImpUtils.sameCharacter(characterA.getCoreEntityMention(), characterB.getCoreEntityMention(), document)){ // if the distance is strictly superior to 0
						tokenListA = characterA.getCoreEntityMention().tokens();
						tokenListB = characterB.getCoreEntityMention().tokens();
						
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