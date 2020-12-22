package pipeline;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * Paragraphs in a chapter
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
 */
public class Paragraph {

    /**
     * list of sentences in the paragraph
    */
    protected LinkedList<CoreSentence> sentences;
    /**
     * Chapter containing the paragraph
    */
    protected Chapter chapter;
    /**
     * the number of the Paragraph in the book starting from 1
    */
    protected int paragraphNumber;
    /**
     * the index of the sentence begining the paragraph in the CoreCocument
    */
    protected int beginingSentence;
    /**
     * the index of the sentence ending the paragraph in the CoreCocument
    */
    protected int endingSentence;

    /**
     * Class Constructor
     * 
    */
    public Paragraph() {
    }

    /**
     * Class Constructor specifying the book containing the chapter.
     * 
     * @param book The Book containing the chapter
     * 
    */
    Paragraph(Chapter chapter){
        this.chapter = chapter;
        sentences = new LinkedList<>();
    }

    /**
     * Class Constructor specifying the book containing the chapter and the number of the chapter in this book.
     * 
     * @param book The Book containing the chapter
     * @param number An Integer representing the number of the chapter in the book starting from 1
     * 
    */
    Paragraph(Chapter chapter, int number){
        paragraphNumber = number;
        this.chapter = chapter;
        sentences = new LinkedList<>();
    }

    public List<CoreSentence> getSentences() {
        return this.sentences;
    }

    public void setSentences(LinkedList<CoreSentence> sentences) {
        this.sentences = sentences;
    }

    public Chapter getChapter() {
        return this.chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public int getParagraphNumber() {
        return this.paragraphNumber;
    }

    public void setParagraphNumber(int paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
    }

    public int getBeginingSentence() {
        return this.beginingSentence;
    }

    public void setBeginingSentence(int beginingSentence) {
        this.beginingSentence = beginingSentence;
    }

    public int getEndingSentence() {
        return this.endingSentence;
    }

    public void setEndingSentence(int endingSentence) {
        this.endingSentence = endingSentence;
    }

    @Override
    public String toString() {
        return "{" +
            " sentences='" + getSentences() + "'" +
            ", chapter='" + getChapter() + "'" +
            ", paragraphNumber='" + getParagraphNumber() + "'" +
            ", beginingSentence='" + getBeginingSentence() + "'" +
            ", endingSentence='" + getEndingSentence() + "'" +
            "}";
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
