package pipeline;

import java.util.List;
import java.util.Properties;

import book.Book;
import book.Chapter;
import book.Paragraph;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Generic class for the creation of dynamic graphs.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public abstract class WindowingDynamicGraph {

	/**
	 * the Book created from the original text.
	*/ 
	protected Book book;
	/**
	 * the co-occurrence table you want to create the dynamic graphs from.
	*/ 
	protected CooccurrenceTable cooccurrenceTable;

	/**
	 * Class Constructor
	 * 
	 * @param book the Book created from the original text.
	 * @param cooccurrenceTable the co-occurrence table you want to create the dynamic graphs from.
	*/
	protected WindowingDynamicGraph(Book book, CooccurrenceTable cooccurrenceTable) {
		this.book = book;
		this.cooccurrenceTable = cooccurrenceTable;
    }
	
	public Book getBook() {
		return this.book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public CooccurrenceTable getCooccurrenceTable() {
		return this.cooccurrenceTable;
	}

	public void setCooccurrenceTable(CooccurrenceTable cooccurrenceTable) {
		this.cooccurrenceTable = cooccurrenceTable;
	}

	@Override
	public String toString() {
		return "{" +
			" book='" + getBook() + "'" +
			"}";
	}

	public List<CooccurrenceTable> dynamicTableSentences(int size, int covering){
		return null;
	}
	
	public List<CooccurrenceTable> dynamicTableParagraphs(int size, int covering){
		return null;
	}

	public List<CooccurrenceTable> dynamicTableChapters(int size, int covering){
		return null;
	}

	private static void testGeneratingTableForDynamicGraphsFromParagraphsToSentence(){
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

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(3);

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

		//table display
		System.out.println("\n--- table co-occurrence Paragraph without chapter limitation ---\n");
		CooccurrenceTableParagraph table = wcp.createTab(document);
		table.display();

		WindowingDynamicGraphFromParagraphTable dgp = new WindowingDynamicGraphFromParagraphTable(book, table);

		int cpt =0;
		for (CooccurrenceTable t : dgp.dynamicTableSentences(2, 1)){
			System.out.println("\n--- table co-occurrence from Paragraph to Sentences "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

	}

	public static void testGeneratingTableForDynamicGraphsFromParagraphsToParagraphs(){
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

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(3);

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

		//table display
		System.out.println("\n--- table co-occurrence Paragraph without chapter limitation ---\n");
		CooccurrenceTableParagraph table = wcp.createTab(document);
		table.display();

		WindowingDynamicGraphFromParagraphTable dgp = new WindowingDynamicGraphFromParagraphTable(book, table);

		int cpt =0;
		for (CooccurrenceTable t : dgp.dynamicTableParagraphs(2, 1)){
			System.out.println("\n--- table co-occurrence from Paragraph to Paragraphs "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

	}

	public static void testGeneratingTableForDynamicGraphsFromParagraphsToChapters(){
		Properties props = new Properties();
    	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// manual book creation
		Book book = new Book();
		WindowingCooccurrenceParagraph wcp = new WindowingCooccurrenceParagraph(1, 0, false, book);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(3);

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

		//table display
		System.out.println("\n--- table co-occurrence Paragraph without chapter limitation ---\n");
		CooccurrenceTableParagraph table = wcp.createTab(document);
		table.display();

		WindowingDynamicGraphFromParagraphTable dgp = new WindowingDynamicGraphFromParagraphTable(book, table);

		int cpt =0;
		for (CooccurrenceTable t : dgp.dynamicTableChapters(1, 0)){
			System.out.println("\n--- table co-occurrence from Paragraph to Chapters "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

		cpt =0;
		for (CooccurrenceTable t : dgp.dynamicTableChapters(2, 0)){
			System.out.println("\n--- table co-occurrence from Paragraph to Chapters "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

	}

	public static void main(String[] args) { 
		testGeneratingTableForDynamicGraphsFromParagraphsToSentence();
		testGeneratingTableForDynamicGraphsFromParagraphsToParagraphs();
		testGeneratingTableForDynamicGraphsFromParagraphsToChapters();
		/*testGeneratingTableForDynamicGraphsFromSentenceToSentence();
		testGeneratingTableForDynamicGraphsFromSentenceToParagraphs();
		testGeneratingTableForDynamicGraphsFromSentenceToChapters();*/

	}
}