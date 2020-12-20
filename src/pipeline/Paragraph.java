package pipeline;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * Paragraphs in a chapter
 * 
 * @author Quay Baptiste, Lemaire Tewis
 */
public class Paragraph {
    protected LinkedList<CoreSentence> sentences;   //list of sentences in the paragraph
    protected Chapter chapter; //Chapter containing the paragraph
    protected int paragraphNumber;

    /**
     * Constructor
     * 
    */
    Paragraph(Chapter chapter, int number){
        paragraphNumber = number;
        this.chapter = chapter;
        sentences = new LinkedList<>();
    }

     /**
     * Constructor
     * 
    */
    Paragraph(Chapter chapter){
        this.chapter = chapter;
        sentences = new LinkedList<>();
    }


    /**
     * add a sentence to the paragraph
     * 
     * @param sentence sentence to add to the paragraph
    */
    void addSentence(CoreSentence sentence){
        sentences.add(sentence);
    }

    /**
     * display the content in the console
    */
    void display(){
        for (CoreSentence sentence : this.sentences){
            System.out.println(sentence.text());
        }
        
    }

    /**
     * write the content in a file
     *  
     * @param fileWriter object used to write in the file (it contains the file destination and name)
    */
    void printToFile(FileWriter fileWriter) throws IOException {
        for (CoreSentence sentence : this.sentences){
            fileWriter.write(sentence.text()+ " ");
        }
    }
}
