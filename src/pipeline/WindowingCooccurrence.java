package pipeline;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import java.util.List;
import java.util.LinkedList;
import java.util.Properties;

import util.CustomCorefChain;
import util.CustomEntityMention;
import util.ImpUtils;
import util.NullDocumentException;
import util.TextNormalization;

import book.Book;
import book.CreateBook;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


/**
 * A generic class for the windowing of co-occurrences.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
 *
 */
public abstract class WindowingCooccurrence {

	/**
	 * size of the window used to find co-occurrences.
	*/
	int size;
	/**
	 * size of the covering between 2 windows. Set to 0 for sequential detection.
	*/
	int covering;
	/*
	 * True if you want to stop the detection at the end of a chapter even if the end of the window is not reached. 
	 * The detection will continue with a new window at the begining of the next chapter.
	*/
	boolean chapterLimitation;

	/*
	* the book you want to detect co-occurrences from.
	*/
	Book book;
	
	/**
	 * Class constructor
	 * 
	 * @param size size of the window used to find co-occurrences.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential detection.
	 */
	protected WindowingCooccurrence(int size, int covering, boolean chapterLimitation, Book book){
		this.size = size;
		this.covering = covering;
		this.chapterLimitation = chapterLimitation;
		this.book = book;
	}
	
	protected CooccurrenceTable createTab() {
		return null;
	}
	
	public List<List<CustomEntityMention>> createWindow() {
		return new LinkedList<>();
	}

	private static void testWindowingCooccurrenceSentence() throws IOException, NullDocumentException{
		// set up pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		//getting content
		String path = "res/tests/clusteringTestSample.txt";
		FileInputStream is = new FileInputStream(path);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		//normalizing content
		content = TextNormalization.addDotEndOfLine(content);

		// make the document
		CoreDocument document = new CoreDocument(content);
		
		// annotate the document
		pipeline.annotate(document);
		ImpUtils.setDocument(document);

		// CorefChain Fusion
		List<CustomCorefChain> cccList = CustomCorefChainMaker.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 2, 0.45);

		// automatic book creation
		boolean noTitle = true;
		Book book = CreateBook.createBook(document, noTitle, cccList);
		WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(2, 1, false, book);
		WindowingCooccurrenceSentence wcs2 = new WindowingCooccurrenceSentence(2, 1, true, book);

		//book display
		System.out.println("---");
		book.display();
		
		System.out.println("\n--- entités détectées ---\n");
		for (CoreEntityMention e : document.entityMentions()){
			System.out.println(e);
		}

		System.out.println("\n--- chaines de coréférences ---\n");
		for ( CorefChain c :document.corefChains().values()){
			System.out.println(c);
		}

		System.out.println("\n--- chaines de coréférences custom fusionées ---\n");
		for ( CustomCorefChain c : cccList){
			System.out.println(c);
		}

		//window to create table display
		System.out.println("\n--- fenêtres de co-occurrences pour faire le tableau ---\n");
		List<List<CustomEntityMention>> tmp = wcs.createWindow();
		for(List<CustomEntityMention> l : tmp){
			System.out.println(l + "\n");
		}

		//table display
		System.out.println("\n--- table de co-occurrence en Phrases sans limite aux chapitres ---\n");
		CooccurrenceTableSentence table = wcs.createTab();
		table.display();

		//window to create table display
		System.out.println("\n--- fenêtres de co-occurrences pour faire le tableau ---\n");
		List<List<CustomEntityMention>> tmp2 = wcs2.createWindow();
		for(List<CustomEntityMention> l : tmp2){
			System.out.println(l + "\n");
		}

		//table display
		System.out.println("\n--- table de co-occurrence en Phrases avec limite aux chapitres ---\n");
		CooccurrenceTableSentence table2 = wcs2.createTab();
		table2.display();

	}

	private static void testWindowingCooccurrenceParagraphs() throws IOException, NullDocumentException{
		// set up pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		//getting content
		String path = "res/tests/clusteringTestSample.txt";
		FileInputStream is = new FileInputStream(path);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		//normalizing content
		content = TextNormalization.addDotEndOfLine(content);

		// make the document
		CoreDocument document = new CoreDocument(content);
		
		// annotate the document
		pipeline.annotate(document);
		ImpUtils.setDocument(document);

		// CorefChain Fusion
		List<CustomCorefChain> cccList = CustomCorefChainMaker.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		System.out.println("\n--- chaines de coréférences custom avant fusion ---\n");
		for ( CustomCorefChain c : cccList){
			System.out.println(c + "\t" + c.getCEMList().size());
		}
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 2, 0.5);

		// automatic book creation
		boolean noTitle = true;
		Book book = CreateBook.createBook(document, noTitle, cccList);
		WindowingCooccurrenceParagraph wcp = new WindowingCooccurrenceParagraph(2, 1, false, book);
		WindowingCooccurrenceParagraph wcp2 = new WindowingCooccurrenceParagraph(2, 1, true, book);

		//book display
		System.out.println("---");
		book.display();
		
		System.out.println("\n--- entités détectées ---\n");
		for (CoreEntityMention e : document.entityMentions()){
			System.out.println(e);
		}

		System.out.println("\n--- chaines de coréférences ---\n");
		for ( CorefChain c :document.corefChains().values()){
			System.out.println(c);
		}

		System.out.println("\n--- chaines de coréférences custom fusionées ---\n");
		for ( CustomCorefChain c : cccList){
			System.out.println(c);
		}

		//window to create table display
		System.out.println("\n--- fenêtres de co-occurrences pour faire le tableau ---\n");
		List<List<CustomEntityMention>> tmp = wcp.createWindow();
		for(List<CustomEntityMention> l : tmp){
			System.out.println(l + "\n");
		}

		//table display
		System.out.println("\n--- table de co-occurrence en Paragraphes sans limite aux chapitres ---\n");
		CooccurrenceTableParagraph table = wcp.createTab();
		table.display();

		//window to create table display
		System.out.println("\n--- fenêtres de co-occurrences pour faire le tableau ---\n");
		List<List<CustomEntityMention>> tmp2 = wcp2.createWindow();
		for(List<CustomEntityMention> l : tmp2){
			System.out.println(l + "\n");
		}


		//table display
		System.out.println("\n--- table de co-occurrence en Paragraphes avec limite aux chapitres ---\n");
		CooccurrenceTableParagraph table2 = wcp2.createTab();
		table2.display();
	}

	
	public static void main(String[] args) throws IOException, NullDocumentException { 
		testWindowingCooccurrenceSentence();
		testWindowingCooccurrenceParagraphs();
	}
}
