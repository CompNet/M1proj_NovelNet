package performance;

import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import novelnet.util.ImpUtils;
import novelnet.util.TextNormalization;

public class AnnotationTool {

    private AnnotationTool(){
        
    }
    
    public static void decompose(String path) throws IOException{
        FileInputStream is = new FileInputStream(path);
        String content = IOUtils.toString(is, StandardCharsets.UTF_8);

        content = TextNormalization.addDotEndOfLine(content);

        System.out.println("decomposition for analyse.");

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(content);
        pipeline.annotate(document);
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < document.sentences().size(); i++){
            if (i != 0 && i%2 == 0){
                System.out.println(" press enter to continue ");
                sc.nextLine();
            }
            for (CoreLabel token : document.sentences().get(i).tokens()){
                System.out.println("Text : " + token.originalText() + "\t | Sentence : " + (token.sentIndex()+1) + "\t | Position : " + (document.sentences().get(i).tokens().lastIndexOf(token)+1));
            }
            System.out.println();
        }
        sc.close();

    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("saisir chemin du fichier à traiter:");
        String path = sc.nextLine();
		AnnotationTool.decompose(path);
	}
}
