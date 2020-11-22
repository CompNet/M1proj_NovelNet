package implementation;

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
        System.out.println("new paragraph");
        for (CoreSentence sentence : this.sentences){
            System.out.println(sentence.text());
        }
        
    }
}
