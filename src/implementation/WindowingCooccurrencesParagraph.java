package implementation;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WindowingCooccurrencesParagraph extends WindowingCooccurrence{

	boolean chapterLimitation;
	Book book;

	/**
	 * Constructor for the class WindowingCooccurrencesParagraph
	 * 
	 * @param weighting 
	 * @param chapterLimitation
	 * @param book
	 * @param size Window's size
	 * @param covering 

	 */
	public WindowingCooccurrencesParagraph(boolean weighting, boolean chapterLimitation, Book book, int size, int covering){
		super(weighting, size, covering);
		this.chapterLimitation = chapterLimitation;
		this.book = book;
	}


	@Override
	public List<List<CoreLabel>> createWindow(CoreDocument document) {
		CreateBook cb = new CreateBook();
		cb.createBook(document);
		book = cb.getBook();
		List<CoreLabel> window = new LinkedList<>();
		List<List<CoreLabel>> result = new LinkedList<>();
		int cpt = 0;
		
		for (Chapter chapter : book.chapters){
			if (chapterLimitation) cpt = 0;
			for (int i = 0 ; i < chapter.paragraphs.size(); i++){
				for (int j=0; j<chapter.paragraphs.get(i).sentences.size(); j++){
					List<CoreLabel> sentence = chapter.paragraphs.get(i).sentences.get(j).tokens();
					for (CoreLabel token : sentence) {
						window.add(token);
					}
				}
				cpt++;
				if (cpt == size){
					result.add(window);
					window = new LinkedList<>();
					cpt = 0;
					i -= covering;
				}
			}
		}
		
		return result;
	}
	
	@Override
	public Tableau createTab(CoreDocument document) {
		List<List<CoreLabel>> result = createWindow(document);
		String charA = null;
		String charB = null;
		CorefChain tempA;
		CorefChain tempB;
		int distanceChar = 0;
		int distanceMot = 0;
		Tableau tab = new Tableau(size, book);
		Map<Integer, CorefChain> corefChains = document.corefChains(); //We create a map of corefChains (each represents a set of mentions which corresponds to the same entity)
		for (List<CoreLabel> tokens : result){ // For each token list
			for (CoreLabel tokenA : tokens){ // For each token
				if(tokenA.ner().equals("PERSON")){
					for (CoreLabel tokenB : tokens){ // For each token list
						if(tokenB.ner().equals("PERSON")){
							distanceChar = tokenA.beginPosition() - tokenB.beginPosition();	
							distanceMot = tokens.indexOf(tokenA) - tokens.indexOf(tokenB);
							if (!(tokenA.equals(tokenB)) && distanceChar > 0) {
								tempA = ImpUtils.corefByToken(corefChains, tokenA);
								tempB = ImpUtils.corefByToken(corefChains, tokenB);
								if (tempA != null && tempB != null) {
									charA = tempA.getRepresentativeMention().mentionSpan;
									charB = tempB.getRepresentativeMention().mentionSpan;
									if (!charA.equals(charB)) tab.addLign(charA, charB, distanceChar, distanceMot);
								}
							}
						}
					}
				}
			}
		}
		tab.display();
		return tab;
	}
}
