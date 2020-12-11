/**
 * 
 */
package implementation;

import java.io.FileInputStream;
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
			String[] annotatorList = new  String[10];
			for (int i=0;i<10;i++)
			{
				annotatorList[i] = "";
			}
			Scanner sc = new Scanner(System.in);

			annotatorList[0]= "tokenize";
			annotatorList[1]= "ssplit";
			annotatorList[2]= "pos";
			annotatorList[3]= "lemma";
			annotatorList[4]= "ner";
			annotatorList[5]= "parse";
			annotatorList[6]= "depparse";
			annotatorList[7]= "coref";
			
			
			System.out.println("saisir chemin du fichier à traiter:");
			String path = sc.nextLine();
			String prop="";
			for (int i=0; i<10; i++)
			{
				if (!annotatorList[i].equals("")) prop+=annotatorList[i]+",";
			}
			prop = prop.substring(0, prop.length()-1);
			
			System.out.println("les annotateurs séléctionés sont: "+prop);
			
			Properties props = new Properties();
			props.setProperty("annotators",prop);
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			FileInputStream is = new FileInputStream(path);     
			String content = IOUtils.toString(is, "UTF-8");
			CoreDocument document = new CoreDocument(content);
			pipeline.annotate(document);
						
			// OUTPUT
			PrintWriter out = new PrintWriter("resultats/"+path.substring(7)+"_output.txt");
			Annotation annotation = new Annotation(content);
			// annotate the annotation
			pipeline.annotate(annotation);
			// print result on a file
			pipeline.prettyPrint(annotation, out );
			
			Graph graph = new Graph();
			graph.setName("graph_sliding_1s_"+path.substring(7));
			WindowingCooccurrenceSentence w = new WindowingCooccurrenceSentence(true, 2, 1);
			w.createGraph(document,graph);
			graph.graphMLPrinter("resultats");
				
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
