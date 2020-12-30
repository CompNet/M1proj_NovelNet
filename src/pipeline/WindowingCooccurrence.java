package pipeline;

import java.util.List;
import java.util.LinkedList;
import java.util.Properties;

import book.Book;
import book.Chapter;
import book.Paragraph;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import util.EntityMention;

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

	public List<CoreEntityMention> findEntity(CoreDocument document){
		LinkedList<CoreEntityMention> result = new LinkedList<>();
		for (CoreEntityMention em : document.entityMentions()){
			if (em.entityType().equals("PERSON") ){
				result.add(em);
			}
		}
		return result;
	}
	
	protected CooccurrenceTable createTab(CoreDocument document) {
		return null;
	}
	
	public List<List<EntityMention>> createWindow(CoreDocument document) {
		return new LinkedList<>();
	}

	private static void testWindowingCooccurrenceSentence(){
		// set up pipeline
		Properties props = new Properties();
    	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// manual book creation
		Book book = new Book();
		WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(2, 1, false, book);
		WindowingCooccurrenceSentence wcs2 = new WindowingCooccurrenceSentence(2, 1, true, book);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.addSentence(document.sentences().get(3));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(3);

		Chapter c1 = new Chapter();
		c1.addParagraph(p1);
		c1.addParagraph(p2);

		Chapter c2 = new Chapter();
		c2.addParagraph(p3);
		
		book.addChapter(c1);
		book.addChapter(c2);

		//book display
		System.out.println("---");
		book.placeEntitites(wcs.findEntity(document));
		book.display();

		//window to create table display
		/*System.out.println("---");
		List<List<EntityMention>> tmp = wcs.createWindow(document);
		System.out.println(tmp);*/

		//table display
		System.out.println("--- table co-occurrence Sentence without chapter limitation ---");
		CooccurrenceTableSentence table = wcs.createTab(document);
		table.display();

		//window to create table display
		/*System.out.println("---");
		List<List<EntityMention>> tmp2 = wcs2.createWindow(document);
		System.out.println(tmp2);*/

		//table display
		System.out.println("--- table co-occurrence Sentence with chapter limitation ---");
		CooccurrenceTableSentence table2 = wcs2.createTab(document);
		table2.display();
	}

	public static void main(String[] args) { 
		testWindowingCooccurrenceSentence();
		testWindowingCooccurrenceParagraphs();
	}

	private static void testWindowingCooccurrenceParagraphs(){
		// set up pipeline
		Properties props = new Properties();
    	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// manual book creation
		Book book = new Book();
		WindowingCooccurrenceParagraph wcp = new WindowingCooccurrenceParagraph(2, 1, false, book);
		WindowingCooccurrenceParagraph wcp2 = new WindowingCooccurrenceParagraph(2, 1, true, book);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(1);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(2);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(3);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(4);

		Chapter c1 = new Chapter();
		c1.addParagraph(p1);
		c1.addParagraph(p2);

		Chapter c2 = new Chapter();
		c2.addParagraph(p3);
		c2.addParagraph(p4);
		
		book.addChapter(c1);
		book.addChapter(c2);

		//book display
		System.out.println("---");
		book.placeEntitites(wcp.findEntity(document));
		book.display();

		//window to create table display
		/*System.out.println("---");
		List<List<EntityMention>> tmp = wcp.createWindow(document);
		System.out.println(tmp);*/

		//table display
		System.out.println("--- table co-occurrence Paragraph without chapter limitation ---");
		CooccurrenceTableParagraph table = wcp.createTab(document);
		table.display();

		//window to create table display
		/*System.out.println("---");
		List<List<EntityMention>> tmp2 = wcp2.createWindow(document);
		System.out.println(tmp2);*/

		//table display
		System.out.println("--- table co-occurrence Paragraph with chapter limitation ---");
		CooccurrenceTableParagraph table2 = wcp2.createTab(document);
		table2.display();
	}
}
