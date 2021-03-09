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
    
    public static void decompose(String path, int sentenceJump) throws IOException{
        FileInputStream is = new FileInputStream(path);
        String content = IOUtils.toString(is, StandardCharsets.UTF_8);

        content = TextNormalization.addDotEndOfLine(content);

        System.out.println("decomposition for analyse.");

        Properties props = ImpUtils.getFrenchProperties();
        props.setProperty("annotators", "tokenize,ssplit");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(content);
        pipeline.annotate(document);
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < document.sentences().size(); i++){
            if (i != 0 && i%sentenceJump == 0){
                System.out.println(" press enter to continue ");
                sc.nextLine();
            }
            System.out.println("Sentence number "+(i+1));
            for (CoreLabel token : document.sentences().get(i).tokens()){
                System.out.println("Text : " + token.originalText() + "\t | Position : " + (document.sentences().get(i).tokens().lastIndexOf(token)+1));
            }
            System.out.println();
        }
        sc.close();

    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("enter the path to the file to analyse : ");
        String path = sc.nextLine();
        System.out.println("enter the number of sentences to show before asking to continue : ");
        int sentenceJump = sc.nextInt();
        sc.nextLine();
		AnnotationTool.decompose(path, sentenceJump);
	}
}
