import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import ca.umontreal.rali.reverbfr.FrenchReverbUtils;
import ca.umontreal.rali.reverbfr.ReverbConfiguration;
import novelnet.book.Book;
import novelnet.book.CreateBook;
import novelnet.util.TextNormalization;
import novelnet.util.CustomCorefChain;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;
import novelnet.table.CooccurrenceTable;
import novelnet.table.CooccurrenceTableParagraph;
import novelnet.table.CooccurrenceTableSentence;
import novelnet.table.DirectInteractionTable;
import novelnet.table.InteractionTable;
import novelnet.pipeline.DirectInteractionTableCreator;
import novelnet.pipeline.CorefChainFuser;
import novelnet.pipeline.CustomCorefChainMaker;
import novelnet.pipeline.GraphCreator;
import novelnet.pipeline.WindowingCooccurrenceParagraph;
import novelnet.pipeline.WindowingCooccurrenceSentence;
import novelnet.pipeline.WindowingDynamicGraphFromParagraphTable;
import novelnet.pipeline.WindowingDynamicGraphFromSentenceTable;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import novelnet.graph.Graph;



/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Menu {


	public static void testNER() throws IOException{
		String path = "res/corpus/Joe_Smith.txt";

		FileInputStream is = new FileInputStream(path);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		content = TextNormalization.addDotEndOfLine(content);

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		props.setProperty("ner.applyFineGrained", "false");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		CoreDocument document = new CoreDocument(content);

		pipeline.annotate(document);

		boolean person;

		for (CoreEntityMention e : document.entityMentions()){
			person = false;
			if (e.entityType().equals("PERSON")) {
				for(CoreLabel token : e.tokens()){
					if (token.ner().equals("PERSON")){
						person = true;
					}
				}
				
				if (person) System.out.println(e);
			}
		}
	}

	public static void testOpenIE() throws IOException {

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

		Graph test = GraphCreator.createGraph(it, "graph_test_interaction", true, false);

		it.display();

		System.out.println(test);

		test.graphMLPrinter("res/results");

		sc.close();
	}
	public static void testOnFrenchTextWithStanford() throws IOException{
		Scanner sc = new Scanner(System.in);
		System.out.println("saisir chemin du fichier à traiter:");
		String path = sc.nextLine();
		sc.close();

		FileInputStream is = new FileInputStream(path);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		content = TextNormalization.addDotEndOfLine(content);

		String annotators="tokenize,ssplit,pos,lemma,ner,depparse,coref,natlog,openie";
		//String annotators="tokenize,ssplit,pos,lemma,ner,depparse,coref";
		System.out.println("les annotateurs séléctionés sont: "+annotators);

		Properties props = ImpUtils.getFrenchProperties();
		props.setProperty("annotators",annotators);
		props.setProperty("ner.applyFineGrained", "false");
		

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		ImpUtils.setDocument(document);
		pipeline.annotate(document);
		for (CoreEntityMention cem : document.entityMentions()){
			System.out.println(cem);
		}
		for (CorefChain cc : document.corefChains().values()){
			System.out.println(cc);
		}
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
	}

	public static void testOnFrenchTextReverb() throws IOException{
		
		ReverbConfiguration.setLocale(Locale.FRENCH);
		OpenNlpSentenceChunker chunker = new OpenNlpSentenceChunker();
		ReVerbExtractor reverb = new ReVerbExtractor();

		Scanner sc = new Scanner(System.in);
		System.out.println("saisir chemin du fichier à traiter:");
		String path = sc.nextLine();
		sc.close();

		FileInputStream is = new FileInputStream(path);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		content = TextNormalization.addDotEndOfLine(content);

		String annotators="tokenize,ssplit";
		System.out.println("running Stanford's sentence splitting ");

		Properties props = ImpUtils.getFrenchProperties();
		props.setProperty("annotators",annotators);
		props.setProperty("tokenize.keepeol","true");
		//props.setProperty("tokenize.options", "stokenize.splitAssimilations=true,splitHyphenated=false");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		ImpUtils.setDocument(document);
		pipeline.annotate(document);

		for (CoreSentence stanfordSentence : document.sentences()){
            
            // chunk sentence
            ChunkedSentence sent = chunker.chunkSentence(stanfordSentence.toString());

			System.out.println(stanfordSentence.text());
			System.out.println(sent.toString());

			for(CoreLabel token : stanfordSentence.tokens()){
				System.out.println("st : \t " + token.originalText());
				System.out.println("reverb : " + sent.getToken(token.index()-1));
			}

            // extract with reverb and iterate on results
            /*for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
                
                System.out.println("Arg1=" + extr.getArgument1().getText().trim() + ", " + extr.getArgument1().getStart() + ", " + extr.getArgument1().getTokens());
                System.out.println("Rel=" + extr.getRelation().getText().trim());
                System.out.println("Arg2=" + extr.getArgument2().getText().trim());
				
                
                // lemmatization and canonization (optional operations)
                FrenchReverbUtils.addLemmas(extr.getSentence());
                System.out.println("RelLemma=" + FrenchReverbUtils.formatLemmatizedRelation(extr));
                System.out.println("RelCanon=" + FrenchReverbUtils.getCanonicalRelation(extr));
                
                System.out.println();
            }*/

        }
	}

	/**
	 * @param args
	 * @author Quay Baptiste, Lemaire Tewis
	 * @throws IOException 
	 * @throws NullDocumentException
	*/
	public static void main(String[] args) throws IOException, NullDocumentException {
		testNER();
		if (args.length == 1)
		{
			Scanner sc = new Scanner(System.in);
			System.out.println("saisir chemin du fichier à traiter:");
			String path = sc.nextLine();
			sc.close();


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
			for (CoreEntityMention cem: document.entityMentions()){
				if (cem.entityType().equals("PERSON")) System.out.println(cem.text() + " : " + cem.sentence().tokens().get(0).sentIndex());
			}
			System.out.println();
			

			for ( CorefChain cc : document.corefChains().values()){
				System.out.print(cc.getRepresentativeMention().mentionSpan + " : [ ");
				for (CorefMention cm : cc.getMentionsInTextualOrder()){
					System.out.print(cm.mentionSpan + ", " + cm.sentNum + " ; ");
				}
				System.out.println();
			}
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
			GraphCreator.createGraph(table, graphTitle, false, true).graphMLPrinter("res/results");
			
			//Create a dynamic graph sequence from sentences table with Sentence window.
			WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, table);

			int cpt = 0;
			for (InteractionTable t : dgs.dynamicTableSentences(700, 75)){
				cpt++;
				GraphCreator.createGraph(t, graphTitle+"_Sentence101_"+cpt, false, true).graphMLPrinter("res/results");
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
		}


	}

}