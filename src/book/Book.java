package book;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Represent a Book.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class Book {

    /**
     * A list of all the chapters in the book.
    */
    protected LinkedList<Chapter> chapters;

    /**
     * Class Constructor.
     * 
    */
    Book() {
        chapters = new LinkedList<>();
    }


    public Book(LinkedList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public List<Chapter> getChapters() {
        return this.chapters;
    }

    public void setChapters(LinkedList<Chapter> chapters) {
        this.chapters = chapters;
    }

    @Override
    public String toString() {
        return "{" +
            " chapters='" + getChapters() + "'" +
            "}";
    }


    /**
     * add an empty chapter to the book.
    */
    void addVoidChapter() {
        chapters.add(new Chapter(this));
    }

    /**
     * add a chapter to the book.
     * 
     * @param chapter chapter to add to the book.
    */
    void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }

    /**
     * get the index (in the coreDocument) of the sentence begining the paragraph.
     * 
     * @param paragraphNumber An Integer representing the paragraph number in the book.
     * @return An Integer representing the index of the sentence begining the paragraph. -1 if the paragraph is not in the book.
    */
    public int getBeginIndexOfParagraph(int paragraphNumber){
        int tmp;
        for (Chapter chapter : this.chapters) {
            tmp = chapter.getBeginIndexOfParagraph(paragraphNumber);
            if (tmp != -1) return tmp;
        }
        return -1;
    }

    /**
     * get the index (in the coreDocument) of the sentence ending the paragraph.
     * 
     * @param paragraphNumber An Integer representing the paragraph number in the book.
     * @return An Integer representing the index of the sentence ending the paragraph.  -1 if the paragraph is not in the book.
    */
    public int getEndIndexOfParagraph(int paragraphNumber){
        int tmp;
        for (Chapter chapter : this.chapters) {
            tmp = chapter.getEndIndexOfParagraph(paragraphNumber);
            if (tmp != -1) return tmp;
        }
        return -1;
    }

    /**
     * get the index (in the coreDocument) of the sentence begining the chapter.
     * 
     * @param chapterNumber An Integer representing the chapter number in the book.
     * @return An Integer representing the index of the sentence begining the chapter. -1 if the chapter is not in the book.
    */
    public int getBeginIndexOfChapter(int chapterNumber){
        for (Chapter chapter : this.chapters) {
            if (chapter.chapterNumber == chapterNumber) return chapter.getBeginingSentence();
        }
        return -1;
    }

    /**
     * get the index (in the coreDocument) of the sentence ending the chapter.
     * 
     * @param chapterNumber An Integer representing the chapter number in the book.
     * @return An Integer representing the index of the sentence ending the chapter. -1 if the chapter is not in the book.
    */
    public int getEndIndexOfChapter(int chapterNumber){
        for (Chapter chapter : this.chapters) {
            if (chapter.chapterNumber == chapterNumber) return chapter.getEndingSentence();
        }
        return -1;
    }

    /**
     * display the content of the book in the console
    */
    public void display() {
        int cpt = 1;

        for (Chapter chapter : this.chapters) {
            System.out.println(" ");
            chapter.display(cpt);
            cpt++;
        }
    }

    /**
     * write the content of the book in a file
     *  
     * @param fileWriter object used to write in the file (it contains the file destination and name)
    */
    public void printToFile(FileWriter fileWriter) throws IOException {
        int cpt = 1;
        for (Chapter chapter : this.chapters) {
            fileWriter.write("\n");
            chapter.printToFile(fileWriter, cpt);
            cpt++;
        }
    }
}
