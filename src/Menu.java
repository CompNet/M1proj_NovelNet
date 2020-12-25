import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import book.Book;
import book.CreateBook;
import book.TextNormalization;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import pipeline.CooccurrenceTable;
import pipeline.CooccurrenceTableSentence;
import pipeline.WindowingCooccurrenceSentence;
import pipeline.WindowingDynamicGraphFromSentenceTable;

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
			String content = IOUtils.toString(is, "StandardCharsets.UTF-8");

			TextNormalization adapt = new TextNormalization(content);
			adapt.addDotEndOfLine();
			content = adapt.getText();

			//String prop="tokenize,ssplit";
			String prop="tokenize,ssplit,pos,lemma,ner,parse,coref";
			System.out.println("les annotateurs séléctionés sont: "+prop);

			Properties props = new Properties();
			props.setProperty("annotators",prop);

			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			CoreDocument document = new CoreDocument(content);
			pipeline.annotate(document);
			
			Annotation annotation = new Annotation(content);
			// annotate the annotation
			pipeline.annotate(annotation);
			// print the result of the annotation in a file
			/*PrintWriter out = new PrintWriter("res/results/"+path.substring(9, path.length()-4)+"_output.txt");
			pipeline.prettyPrint(annotation, out );*/

			/*Book book = CreateBook.createBook(document);
			
			//Create a table from Sentences
			WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(5, 1);
			CooccurrenceTableSentence table = wcs.createTab(document);
			table.display();

			WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, table);
			for (CooccurrenceTable t : dgs.dynamicTableParagraphs(5, 1)){
				System.out.println("nouveau tableau P 5 1 ");
				t.display();
			}

			//Create a table from paragraphs
			/*WindowingCooccurrenceParagraph wcp = new WindowingCooccurrenceParagraph(true, false, book, 5, 1);
			CooccurrenceTableParagraph table2 = wcp.createTab(document);
			table2.display();*/

			// print book object in a file
			FileWriter fileWriter = new FileWriter("res/results/"+path.substring(9, path.length()-4)+"_bookClass.txt");
			book.printToFile(fileWriter);
			fileWriter.close();
			
			//Create a graph
			WindowingCooccurrenceSentence w = new WindowingCooccurrenceSentence(5, 1);	
			CooccurrenceTableSentence table3 = w.createTab(document);
			GraphCreator c = new GraphCreator();
			c.createGraph(table3,true,"graph_"+path.substring(13)).graphMLPrinter("res/resultats");	
			sc.close();
		}


	}

}