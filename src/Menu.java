import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import book.Book;
import book.CreateBook;

import util.TextNormalization;
import util.CustomCorefChain;
import util.ImpUtils;
import util.NullDocumentException;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import graph.Graph;
import pipeline.CooccurrenceTable;
import pipeline.CooccurrenceTableParagraph;
import pipeline.CooccurrenceTableSentence;
import pipeline.CorefChainFuser;
import pipeline.CustomCorefChainMaker;
import pipeline.GraphCreator;
import pipeline.DirectInteractionTable;
import pipeline.DirectInteractionTableCreator;
import pipeline.WindowingCooccurrenceParagraph;
import pipeline.WindowingCooccurrenceSentence;
import pipeline.WindowingDynamicGraphFromParagraphTable;
import pipeline.WindowingDynamicGraphFromSentenceTable;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Menu {

	public static void test() throws IOException {

		Scanner sc = new Scanner(System.in);
		System.out.println("saisir chemin du fichier à traiter:");
		String path = sc.nextLine();

		FileInputStream is = new FileInputStream(path);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		content = TextNormalization.addDotEndOfLine(content);

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref,natlog,openie");
		props.setProperty("ner.applyFineGrained", "false");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		CoreDocument document = new CoreDocument(content);

		pipeline.annotate(document);

		System.out.println(document.entityMentions());
		System.out.println(document.corefChains());

		Annotation annotation = document.annotation();

		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples
			for (RelationTriple triple : triples) {
				System.out.println();
				System.out.println(triple.subjectGloss() + "\t " + triple.subject);
				System.out.println(triple.relationLemmaGloss() + "\t " + triple.relation);
				System.out.println(triple.objectGloss() + "\t " + triple.canonicalObject);
			}
		}
		sc.close();
	}

	public static void testInteractionTableCreator() throws IOException, NullDocumentException {
		Scanner sc = new Scanner(System.in);
		System.out.println("saisir chemin du fichier à traiter:");
		String path = sc.nextLine();

		FileInputStream is = new FileInputStream(path);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		content = TextNormalization.addDotEndOfLine(content);

		String prop="tokenize,ssplit,pos,lemma,ner,parse,coref,natlog,openie";
		System.out.println("les annotateurs séléctionés sont: "+prop);

		Properties props = new Properties();
		props.setProperty("annotators",prop);
		props.setProperty("ner.applyFineGrained", "false");
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		ImpUtils.setDocument(document);
		pipeline.annotate(document);
		
		/*for (CoreLabel cem : document.tokens()){
			System.out.println(cem.originalText() + "\t " + cem.ner());
		}*/
		//System.out.println(document.corefChains());
		// CorefChain Fusion
		List<CustomCorefChain> cccList = CustomCorefChainMaker.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 2, 0.4);

		/*for (CustomCorefChain ccc : cccList){
			System.out.println(ccc);
		}*/

		Book book = CreateBook.createBook(document, false, cccList);

		DirectInteractionTable it = DirectInteractionTableCreator.createTable(book);

		Graph test = GraphCreator.createGraph(it, "graph_test_interaction");

		it.display();

		System.out.println(test);

		test.graphMLPrinter("res/results");

		sc.close();
	}

	/**
	 * @param args
	 * @author Quay Baptiste, Lemaire Tewis
	 * @throws IOException 
	 * @throws NullDocumentException
	*/
	public static void main(String[] args) throws IOException, NullDocumentException {
		testInteractionTableCreator();
		if (args.length == 1)
		{
			/*Scanner sc = new Scanner(System.in);
			System.out.println("saisir chemin du fichier à traiter:");
			String path = sc.nextLine();
*/
			String path = "res/tests/The Double - Fyodor Dostoevsky.txt";
			FileInputStream is = new FileInputStream(path);
			String content = IOUtils.toString(is, StandardCharsets.UTF_8);

			content = TextNormalization.addDotEndOfLine(content);

			//String prop="tokenize,ssplit";
			String prop="tokenize,ssplit,pos,lemma,ner";
			System.out.println("les annotateurs séléctionés sont: "+prop);

			Properties props = new Properties();
			props.setProperty("annotators",prop);
			props.setProperty("ner.applyFineGrained", "false");
			
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			CoreDocument document = new CoreDocument(content);
			ImpUtils.setDocument(document);
			pipeline.annotate(document);
			//Annotation annotation = document.annotation();
			// print the result of the annotation in a file
			/*PrintWriter out = new PrintWriter("res/results/"+path.substring(11, path.length()-4)+"_output.txt");
			pipeline.prettyPrint(annotation, out );*/

			//System.out.println(document.sentences().get(1).tokens().get(0).originalText() + " " + document.sentences().get(1).tokens().get(0).ner());
			/*for (CoreEntityMention cem: document.entityMentions()){
				System.out.print(cem.text() + " : " + cem.entityType() +", ");
			}
			System.out.println();*/
			
			//System.out.println(document.corefChains());
			// CorefChain Fusion

			List<CustomCorefChain> cccList = CustomCorefChainMaker.makeCustomCorefChains(document);

			/*for (CustomCorefChain ccc : cccList){
				System.out.println(ccc);
			}*/

			CorefChainFuser corefChainFuser = new CorefChainFuser();
			cccList = corefChainFuser.corefChainsClusteringRO(cccList, 2, 0.50);

			Book book = CreateBook.createBook(document, false, cccList);

			//Create a table from Sentences 
			WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(10, 1, false, book);
			CooccurrenceTableSentence table = wcs.createTab();

			//Create the global Sentence graph
			String graphTitle = "graph_"+path.substring(11, path.length()-4)+"_Sentence101";
			GraphCreator.createGraph(table, true, graphTitle).graphMLPrinter("res/results");
			
			//Create a dynamic graph sequence from sentences table with Sentence window.
			WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, table);

			int cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableSentences(700, 75)){
				cpt++;
				GraphCreator.createGraph(t, true, graphTitle+"_Sentence101_"+cpt).graphMLPrinter("res/results");
			}

			//Create a table from Sentences 
			wcs = new WindowingCooccurrenceSentence(15, 1, false, book);
			table = wcs.createTab();

			//Create the global Sentence graph
			graphTitle = "graph_"+path.substring(11, path.length()-4)+"_Sentence151";
			GraphCreator.createGraph(table, true, graphTitle).graphMLPrinter("res/results");
			
			//Create a dynamic graph sequence from sentences table with Sentence window.
			dgs = new WindowingDynamicGraphFromSentenceTable(book, table);

			cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableSentences(700, 75)){
				cpt++;
				GraphCreator.createGraph(t, true, graphTitle+"_Sentence151_"+cpt).graphMLPrinter("res/results");
			}

			//Create a table from Sentences 
			wcs = new WindowingCooccurrenceSentence(20, 2, false, book);
			table = wcs.createTab();

			//Create the global Sentence graph
			graphTitle = "graph_"+path.substring(11, path.length()-4)+"_Sentence202";
			GraphCreator.createGraph(table, true, graphTitle).graphMLPrinter("res/results");
			
			//Create a dynamic graph sequence from sentences table with Sentence window.
			dgs = new WindowingDynamicGraphFromSentenceTable(book, table);

			cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableSentences(700, 75)){
				cpt++;
				GraphCreator.createGraph(t, true, graphTitle+"_Sentence202_"+cpt).graphMLPrinter("res/results");
			}
			

			/*cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableParagraphs(5, 2)){
				cpt++;
				GraphCreator.createGraph(t, true, graphTitle+"_Paragraphs_"+cpt).graphMLPrinter("res/results");
			}

			cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableChapters(1, 0)){
				cpt++;
				GraphCreator.createGraph(t, true, graphTitle+"_Chapters_"+cpt).graphMLPrinter("res/results");
			}

			

			//Create a table from Paragraphs
			WindowingCooccurrenceParagraph wcp = new WindowingCooccurrenceParagraph(5, 1, false, book);
			//List<List<EntityMention>> tmp = wcp.createWindow(document);
			//System.out.println(tmp);
			CooccurrenceTableParagraph tableP = wcp.createTab();

			FileWriter fileWriter = new FileWriter("res/results/"+path.substring(11, path.length()-4)+"_bookClass.txt");
			book.printToFile(fileWriter);
			fileWriter.close();

			//Create the global Paragraphs graph
			String graphTitle2 = "graph_"+path.substring(11, path.length()-4)+"_Paragraphs";
			GraphCreator.createGraph(tableP, true, graphTitle2).graphMLPrinter("res/results");
				
			//Create a dynamic graph sequence from Paragraphs table with Paragraphs window.
			WindowingDynamicGraphFromParagraphTable dgp = new WindowingDynamicGraphFromParagraphTable(book, tableP);
			cpt = 0;
			for (CooccurrenceTable t : dgp.dynamicTableSentences(20,3)){
				cpt++;
				GraphCreator.createGraph(t, true, graphTitle2+"_Sentence_"+cpt).graphMLPrinter("res/results");
			}
			

			// print book object in a file
			/*FileWriter fileWriter = new FileWriter("res/results/"+path.substring(11, path.length()-4)+"_bookClass.txt");
			book.printToFile(fileWriter);
			fileWriter.close();*/
			
			//sc.close();
		}


	}

}