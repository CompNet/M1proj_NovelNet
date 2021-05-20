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
import novelnet.pipeline.CreateBook;
import novelnet.pipeline.CustomCorefChainCreator;
import novelnet.pipeline.GraphCreator;
import novelnet.pipeline.TextNormalization;
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
		String path = "res/corpus/en/Joe_Smith.txt";

		FileInputStream is = new FileInputStream(path);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		String language = path.substring(11, 13);
		content = TextNormalization.preProcess(content, language);

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

		String language = path.substring(11, 13);
		content = TextNormalization.preProcess(content, language);

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref,natlog,openie");
		props.setProperty("ner.applyFineGrained", "false");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		CoreDocument document = new CoreDocument(content);

		pipeline.annotate(document);

		/*System.out.println(document.entityMentions());
		System.out.println(document.corefChains());*/

		Annotation annotation = document.annotation();

		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples
			for (RelationTriple triple : triples) {
				System.out.println();
				System.out.println(triple.subjectGloss() + "\t " + triple.subject + "\t" + triple.canonicalSubject + "\t" + triple.subjectHead());
				System.out.println(triple.relationLemmaGloss() + "\t " + triple.relation + "\t" + triple.relationHead());
				System.out.println(triple.objectGloss() + "\t " + triple.canonicalObject + "\t" + triple.objectHead() + "\t");
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

		String language = path.substring(11, 13);
		content = TextNormalization.preProcess(content, language);

		String annotators="tokenize,ssplit,pos,lemma,ner,parse,coref,natlog,openie";
		System.out.println("les annotateurs séléctionés sont: "+annotators);

		Properties props = new Properties();
		props.setProperty("annotators",annotators);
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
		List<CustomCorefChain> cccList = CustomCorefChainCreator.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 0.4);

		/*for (CustomCorefChain ccc : cccList){
			System.out.println(ccc);
		}*/

		Book book = CreateBook.createBook(document, false, cccList);

		DirectInteractionTable it = DirectInteractionTableCreator.createTable(book);

		Graph test = GraphCreator.createGraph(it, "graph_test_interaction", true, true);

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

		String language = path.substring(11, 13);
		content = TextNormalization.preProcess(content, language);

		String annotators="tokenize,ssplit,pos,lemma,ner";
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
		/*for (CorefChain cc : document.corefChains().values()){
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
		}*/
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

		String language = path.substring(11, 13);
		content = TextNormalization.preProcess(content, language);

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


	public static void menu() throws IOException, NullDocumentException {
		Scanner sc = new Scanner(System.in);
		System.out.println("saisir chemin du fichier à traiter:");
		String path = sc.nextLine();

		String annotators = "";
		int operation;

		Boolean oriented = false;

		while (annotators.equals("")){
			System.out.println("Opération à réaliser :");
			System.out.println("1) graphe de co-occurence");
			System.out.println("2) graphe d'action directe");
			operation = sc.nextInt();
			if (operation == 1) {
				annotators="tokenize,ssplit,pos,lemma,ner,parse,coref";
			}
			else if (operation == 2){
				annotators="tokenize,ssplit,pos,lemma,ner,parse,coref,natlog,openie";
				oriented = true;
			}
			System.out.println();
		}

		String windowType = "";
		int covering = 0;
		int windowSize = 0;
		Boolean notDone = true;

		if(!annotators.contains("openie")){

			while (windowType.equals("")){
				System.out.println("type de déplacement de la fenêtre :");
				System.out.println("1) phrase");
				System.out.println("2) paragraphe");
				operation = sc.nextInt();
				if (operation == 1) {
					windowType="Sentence";
				}
				else if (operation == 2){
					windowType="Paragraph";
				}
				System.out.println();
			}

			System.out.println("Entrez la taille de votre fenêtre : ");
			windowSize = sc.nextInt();
			System.out.println();

			while (notDone){
				System.out.println("type de déplacement de la fenêtre");
				System.out.println("1) séquentiel");
				System.out.println("2) avec recouvrement");
				operation = sc.nextInt();
				System.out.println();
				if (operation == 1) notDone = false;
				else if (operation == 2 ){
					covering = Integer.MAX_VALUE;
					while (covering >= windowSize){
						System.out.println("Entrez la taille de votre recouvrement (inférieur à " + windowSize +") : ");
						covering = sc.nextInt();
						System.out.println();
					}
					notDone = false;
				}
			}
		}

		String graphType = "";
		int graphCovering = 0;
		int graphWindowSize = 0;

		while (graphType.equals("")){
			System.out.println("type de graphe :");
			System.out.println("1) statique");
			System.out.println("2) dynamique");
			operation = sc.nextInt();
			if (operation == 1) {
				graphType="Static";
			}

			else if (operation == 2){
				graphType="dynamic";

				while (windowType.equals("")){
					System.out.println("type de déplacement de la fenêtre :");
					System.out.println("1) phrase");
					System.out.println("2) paragraphe");
					operation = sc.nextInt();
					if (operation == 1) {
						windowType="Sentence";
					}
					else if (operation == 2){
						windowType="Paragraph";
					}
					System.out.println();
				}

				graphWindowSize = Integer.MIN_VALUE;
				while (graphWindowSize <= windowSize){
					System.out.println("Entrez la taille de la fenêtre temporelle du graphe (supérieur à " + windowSize +") : ");
					graphWindowSize = sc.nextInt();
					System.out.println();
				}
				
				notDone = true;

				while (notDone){
					System.out.println("type de déplacement de la fenêtre temporelle du graphe : ");
					System.out.println("1) séquentiel");
					System.out.println("2) avec recouvrement");
					operation = sc.nextInt();
					System.out.println();
					if (operation == 1) notDone = false;
					else if (operation == 2 ){
						graphCovering = Integer.MAX_VALUE;
						while (graphCovering >= graphWindowSize){
							System.out.println("Entrez la taille de votre recouvrement (inférieur à " + graphWindowSize +") : ");
							graphCovering = sc.nextInt();
							System.out.println();
						}
						notDone = false;
					}
				}
			}

			System.out.println();
		}

		sc.close();


		FileInputStream is = new FileInputStream(path);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		String language = path.substring(11, 13);
		content = TextNormalization.preProcess(content, language);

		System.out.println("les annotateurs séléctionés sont: "+annotators);

		Properties props = new Properties();
		props.setProperty("annotators",annotators);
		props.setProperty("ner.applyFineGrained", "false");
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		ImpUtils.setDocument(document);
		pipeline.annotate(document);
		

		List<CustomCorefChain> cccList = CustomCorefChainCreator.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 0.45);

		Book book = CreateBook.createBook(document, false, cccList);

		InteractionTable table;

		if (annotators.contains("openie")){
			table = DirectInteractionTableCreator.createTable(book);
		}
		else {
			if (windowType.equals("Sentence")){
				WindowingCooccurrenceSentence windowingCooccurrence = new WindowingCooccurrenceSentence(windowSize, covering, false, book);
				table = windowingCooccurrence.createTab();

			}
			else{
				WindowingCooccurrenceParagraph windowingCooccurrence = new WindowingCooccurrenceParagraph(windowSize, covering, false, book);
				table = windowingCooccurrence.createTab();
			}
		}
		String graphTitle = "";

		if (annotators.contains("openie")) graphTitle = "graph_"+path.substring(14, path.length()-4)+"_DI";
		else graphTitle = "graph_"+path.substring(14, path.length()-4)+"_CoOc_"+windowType+"_"+windowSize+"_"+covering;


		if(graphType.equals("Static")){
			GraphCreator.createGraph(table, graphTitle, oriented, true).graphMLPrinter("res/results");
		}
		else if (windowType.equals("Sentence")){
			int cpt = 0;
			WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, table);
			for (InteractionTable t : dgs.dynamicTableSentences(graphWindowSize, graphCovering)){
				cpt++;
				GraphCreator.createGraph(t, graphTitle+"_"+cpt, oriented, true).graphMLPrinter("res/results");
			}
		}
		else{
			int cpt = 0;
			WindowingDynamicGraphFromParagraphTable dgs = new WindowingDynamicGraphFromParagraphTable(book, table);
			for (InteractionTable t : dgs.dynamicTableSentences(graphWindowSize, graphCovering)){
				cpt++;
				GraphCreator.createGraph(t, graphTitle+"_"+cpt, oriented, true).graphMLPrinter("res/results");
			}
		}
	}
	
	/**
	 * @param args
	 * @author Quay Baptiste, Lemaire Tewis
	 * @throws IOException 
	 * @throws NullDocumentException
	*/
	public static void main(String[] args) throws IOException, NullDocumentException {
		testOnFrenchTextWithStanford();
		//menu();
	}

}