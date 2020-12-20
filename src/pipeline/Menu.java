/**
 * 
 */
package pipeline;

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

			TextNormalization adapt = new TextNormalization(content);
			adapt.addDotEndOfLine();
			content = adapt.getText();

			String prop="tokenize,ssplit";
			//String prop="tokenize,ssplit,pos,lemma,ner,parse,depparse,coref";
			System.out.println("les annotateurs séléctionés sont: "+prop);

			Properties props = new Properties();
			props.setProperty("annotators",prop);

			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			CoreDocument document = new CoreDocument(content);
			pipeline.annotate(document);
						
			// OUTPUT
			
			Annotation annotation = new Annotation(content);
			// annotate the annotation
			pipeline.annotate(annotation);
			// print result in a file
			PrintWriter out = new PrintWriter("resultats/"+path.substring(7)+"_output.txt");
			pipeline.prettyPrint(annotation, out );

			Book book = CreateBook.createBook(document);

			// print book object in a file
			FileWriter fileWriter = new FileWriter("resultats/"+path.substring(7, path.length()-4)+"_bookClass.txt");
			book.printToFile(fileWriter);
			fileWriter.close();

			/*Graph graph = new Graph();
			graph.setName("graph_sliding_1s_"+path.substring(7));
			WindowingCooccurrenceSentence w = new WindowingCooccurrenceSentence(true, 4, 1);
			w.createTab(document);
				
			/*graph = new Graph();
			graph.setName("graph_sliding_2s_"+path.substring(7));
			w = new WindowingCooccurrenceSentence(true, 3, 2);
			w.createGraph(document,graph);
			graph.graphMLPrinter("resultats");
			
			graph = new Graph();
			graph.setName("graph_sequential_1s_"+path.substring(7));
			w = new WindowingCooccurrenceSentence(true, 1, 0);
			w.createGraph(document,graph);
			graph.graphMLPrinter("resultats");
				
			graph = new Graph();
			graph.setName("graph_sequential_2s_"+path.substring(7));
			w = new WindowingCooccurrenceSentence(true, 2, 0);
			w.createGraph(document,graph);
			graph.graphMLPrinter("resultats");*/
	
			sc.close();
		}


	}

}