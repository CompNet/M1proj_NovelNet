/**
 * 
 */
package implementation;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Menu {

	/**
	 * @param args
	 * @author Quay Baptiste, Lemaire Tewis
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
		if (args.length == 0)
		{
			
			Scanner sc = new Scanner(System.in);
			System.out.println("saisir chemin du fichier à traiter:");
			String path = sc.nextLine();

			FileInputStream is = new FileInputStream(path);
			String content = IOUtils.toString(is, "UTF-8");
			
			String prop="tokenize,ssplit,pos,lemma,ner,parse,depparse,coref";
			//String prop="tokenize,ssplit,pos,lemma";
			System.out.println("les annotateurs séléctionés sont: "+prop);

			Properties props = new Properties();
			props.setProperty("annotators",prop);
			/*props.put("tokenizer.keepeol", "true");
			props.put("ssplit.newlineIsSentenceBreak", "always");*/

			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			CoreDocument document = new CoreDocument(content);
			pipeline.annotate(document);
						
			// OUTPUT
			PrintWriter out = new PrintWriter("resultats/"+path.substring(7)+"_output.txt");
			Annotation annotation = new Annotation(content);
			// annotate the annotation
			pipeline.annotate(annotation);
			// print result on a file
			pipeline.prettyPrint(annotation, out );

			CreateBook bookCreator = new CreateBook();
			bookCreator.createBook(document);
			Book book = bookCreator.getBook();

			FileWriter fileWriter = new FileWriter("resultats/"+path.substring(7)+"_bookClass.txt");
			book.printToFile(fileWriter);
			fileWriter.close();

			/*
			Graph graph = new Graph();
			graph.setName("graph_sliding_1s_"+path.substring(7));
			WindowingCooccurence w = new WindowingCooccurence(document,graph,true,"SENTENCE", false,1, 0);
			w.mainWork();
			graph.graphMLPrinter("resultats");
				
			graph = new Graph();
			graph.setName("graph_sliding_2s_"+path.substring(7));
			w = new WindowingCooccurence(document,graph,true,"SENTENCE", false,2, 1);
			w.mainWork();
			graph.graphMLPrinter("resultats");
			
			graph = new Graph();
			graph.setName("graph_sequential_1s_"+path.substring(7));
			w = new WindowingCooccurence(document,graph,true,"SENTENCE", true,1,0);
			w.mainWork();
			graph.graphMLPrinter("resultats");
				
			graph = new Graph();
			graph.setName("graph_sequential_2s_"+path.substring(7));
			w = new WindowingCooccurence(document,graph,true,"SENTENCE", true,2,1);
			w.mainWork();
			graph.graphMLPrinter("resultats");*/
	
			sc.close();
		}


	}

}
