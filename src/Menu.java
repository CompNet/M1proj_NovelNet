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
import util.CustomEntityMention;
import util.ImpUtils;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import pipeline.CooccurrenceTable;
import pipeline.CooccurrenceTableParagraph;
import pipeline.CooccurrenceTableSentence;
import pipeline.CorefChainFuser;
import pipeline.CustomCorefChainMaker;
import pipeline.GraphCreator;
import pipeline.WindowingCooccurrenceParagraph;
import pipeline.WindowingCooccurrenceSentence;
import pipeline.WindowingDynamicGraphFromParagraphTable;
import pipeline.WindowingDynamicGraphFromSentenceTable;


/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Menu {

	public static void test(){
		// Create the Stanford CoreNLP pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	
		// Annotate an example document.
		Annotation doc = new Annotation("Joe Smith and Sara Jackson helped Bill Farmer");
		pipeline.annotate(doc);
	
		// Loop over sentences in the document
		for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
		  // Get the OpenIE triples for the sentence
		  Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
		  // Print the triples
		  for (RelationTriple triple : triples) {
			System.out.println(triple.confidence + "\t" +
				triple.subjectLemmaGloss() + "\t" +
				triple.relationLemmaGloss() + "\t" +
				triple.objectLemmaGloss());
		  }
		}
	}

	/**
	 * @param args
	 * @author Quay Baptiste, Lemaire Tewis
	 * @throws IOException 
	*/
	public static void main(String[] args) throws IOException {
		if (args.length == 0)
		{
			//test();
			Scanner sc = new Scanner(System.in);
			System.out.println("saisir chemin du fichier à traiter:");
			String path = sc.nextLine();

			FileInputStream is = new FileInputStream(path);
			String content = IOUtils.toString(is, StandardCharsets.UTF_8);

			content = TextNormalization.addDotEndOfLine(content);

			//String prop="tokenize,ssplit";
			String prop="tokenize,ssplit,pos,lemma,ner,parse,coref";
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
			System.out.println(document.entityMentions());
			System.out.println(document.corefChains());
			// CorefChain Fusion
			List<CustomCorefChain> cccList = CustomCorefChainMaker.makeCustomCorefChains(document);

			CorefChainFuser corefChainFuser = new CorefChainFuser();
			cccList = corefChainFuser.corefChainsClusteringRO(cccList, 2, 0.4);

			Book book = CreateBook.createBook(document, false, cccList);

			GraphCreator c = new GraphCreator();

			//Create a table from Sentences 
			WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(5, 1, false, book);
			CooccurrenceTableSentence table = wcs.createTab();

			//Create the global Sentence graph
			String graphTitle = "graph_"+path.substring(11, path.length()-4)+"_Sentence";
			c.createGraph(table, true, graphTitle).graphMLPrinter("res/results");
			
			//Create a dynamic graph sequence from sentences table with Sentence window.
			WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, table);

			int cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableSentences(10, 1)){
				cpt++;
				c.createGraph(t, true, graphTitle+"_Sentence_"+cpt).graphMLPrinter("res/results");
			}

			cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableParagraphs(5, 2)){
				cpt++;
				c.createGraph(t, true, graphTitle+"_Paragraphs_"+cpt).graphMLPrinter("res/results");
			}

			cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableChapters(1, 0)){
				cpt++;
				c.createGraph(t, true, graphTitle+"_Chapters_"+cpt).graphMLPrinter("res/results");
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
			c.createGraph(tableP, true, graphTitle2).graphMLPrinter("res/results");
				
			//Create a dynamic graph sequence from Paragraphs table with Paragraphs window.
			WindowingDynamicGraphFromParagraphTable dgp = new WindowingDynamicGraphFromParagraphTable(book, tableP);
			cpt = 0;
			for (CooccurrenceTable t : dgp.dynamicTableSentences(20,3)){
				cpt++;
				c.createGraph(t, true, graphTitle2+"_Sentence_"+cpt).graphMLPrinter("res/results");
			}
			

			// print book object in a file
			/*FileWriter fileWriter = new FileWriter("res/results/"+path.substring(11, path.length()-4)+"_bookClass.txt");
			book.printToFile(fileWriter);
			fileWriter.close();*/
			
			sc.close();
		}


	}

}