package implementation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Paragraph {
    protected ArrayList<CoreSentence> sentences;

    Paragraph(){
        sentences = new ArrayList<CoreSentence>();
    }

    void addSentence(CoreSentence sentence){
        sentences.add(sentence);
    }

    void display(){
        for (CoreSentence sentence : this.sentences){
            System.out.println(sentence.text());
        }
        
    }

    void printToFile(FileWriter fileWriter) throws IOException {
        for (CoreSentence sentence : this.sentences){
            fileWriter.write(sentence.text()+ " ");
        }
    }
}
