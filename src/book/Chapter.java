package book;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * Represent a Chapter in a Book
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class Chapter {

    /**
     * A list of all the paragraphs in the chapter
    */
    protected LinkedList<Paragraph> paragraphs;

    /**
     * A list of all the Sentences representing the title (and number) of the chapter
    */
    protected LinkedList<CoreSentence> titles;
    /**
     * The book containing the chapter
    */
    protected Book book;
    /**
     * An Integer representing the number of the chapter in the book starting from 1
    */
    protected int chapterNumber;

    /**
     * Class Constructor
     * 
    */
    public Chapter() {
    }

    /**
     * Class Constructor specifying the book containing the chapter.
     * 
     * @param book The Book containing the chapter
     * 
    */
    public Chapter(Book book){
        this.book = book;
        paragraphs = new LinkedList<>();
        titles = new LinkedList<>();
    }

    /**
     * Class Constructor specifying the book containing the chapter and the number of the chapter in this book.
     * 
     * @param book The Book containing the chapter
     * @param number An Integer representing the number of the chapter in the book starting from 1
     * 
    */
    public Chapter(Book book, int number){
        this.book = book;
        chapterNumber = number;
        paragraphs = new LinkedList<>();
        titles = new LinkedList<>();
    }

    public LinkedList<Paragraph> getParagraphs(){
        return this.paragraphs;
    }

    public void setParagraphs(LinkedList<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public List<CoreSentence> getTitles() {
        return this.titles;
    }

    public void setTitles(LinkedList<CoreSentence> titles) {
        this.titles = titles;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getChapterNumber() {
        return this.chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    @Override
    public String toString() {
        return "{" +
            " paragraphs='" + getParagraphs() + "'" +
            ", titles='" + getTitles() + "'" +
            ", book='" + getBook() + "'" +
            ", chapterNumber='" + getChapterNumber() + "'" +
            "}";
    }


    /**
     * add a title to the paragraph
     * 
     * @param sentence title to add to the chapter
    */
    void addTitle(CoreSentence sentence){
        titles.add(sentence);
    }

    /**
     * add an empty paragraph to the chapter
    */
    void addVoidParagraph(){
        paragraphs.add(new Paragraph(this));
    }

    /**
     * add a paragraph to the chapter
     * 
     * @param paragraph paragraph to add to the chapter
    */
    void addParagraph(Paragraph paragraph){
        paragraphs.add(paragraph);
    }

    /**
     * display the content in the console
    */
    void display(int i){
        System.out.println("Chapter " + i + " : ");
        for (CoreSentence sentence : this.titles){
            System.out.print(sentence.text() + " ");
        }
        System.out.println(" ");
        int cpt = 1;
        for (Paragraph paragraph : this.paragraphs){
            System.out.println(" ");
            System.out.println("Paragraph " + cpt + " : ");
            paragraph.display();
            cpt++;
        }
        
    }

    /**
     * write the content in a file
     *  
     * @param fileWriter object used to write in the file (it contains the file destination and name)
    */
    void printToFile(FileWriter fileWriter, int i) throws IOException {
        fileWriter.write("Chapter " + i + " : ");
        for (CoreSentence sentence : this.titles){
            fileWriter.write(sentence.text() + " ");
        }
        int cpt = 1;
        for (Paragraph paragraph : this.paragraphs) {
            fileWriter.write("\n Paragraph " + cpt + " : ");
            paragraph.printToFile(fileWriter);
            cpt++;
        }
    }

     /**
     * get the index (in the coreDocument) of the sentence begining the paragraph
     * 
     * @param paragraphNumber An Integer representing the paragraph number in the book
     * @return An Integer representing the index of the sentence begining the paragraph. -1 if the paragraph is not in the chapter.
    */
    public int getBeginIndexOfParagraph(int paragraphNumber){
        for (Paragraph paragraph : this.paragraphs) {
            if (paragraph.paragraphNumber == paragraphNumber) return paragraph.beginingSentence;
        }
        return -1;
    }

    /**
     * get the index (in the coreDocument) of the sentence ending the paragraph
     * 
     * @param paragraphNumber An Integer representing the paragraph number in the book
     * @return An Integer representing the index of the sentence ending the paragraph. -1 if the paragraph is not in the chapter.
    */
    public int getEndIndexOfParagraph(int paragraphNumber){
        for (Paragraph paragraph : this.paragraphs) {
            if (paragraph.paragraphNumber == paragraphNumber) return paragraph.endingSentence;
        }
        return -1;
    }

    /**
     * get the index (in the coreDocument) of the sentence begining the chapter
     * 
     * @return An Integer representing the index of the sentence begining the chapter.
    */
	public int getBeginingSentence() {
		return paragraphs.getFirst().beginingSentence;
	}

    /**
     * get the index (in the coreDocument) of the sentence ending the chapter
     * 
     * @return An Integer representing the index of the sentence ending the chapter.
    */
	public int getEndingSentence() {
		return paragraphs.getLast().endingSentence;
	}
}
