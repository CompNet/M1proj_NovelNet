package implementation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Represent a book as an object
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class Book {

    protected LinkedList<Chapter> chapters; //Chapters in the book

    /**
     * Constructor
     * 
    */
    Book() {
        chapters = new LinkedList<>();
    }

    /**
     * add an empty chapter to the book
    */
    void addVoidChapter() {
        chapters.add(new Chapter());
    }

    /**
     * add a chapter to the book
     * 
     * @param chapter chapter to add to the book
    */
    void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }

    /**
     * display the content in the console
    */
    void display() {
        int cpt = 1;

        for (Chapter chapter : this.chapters) {
            System.out.println(" ");
            chapter.display(cpt);
            cpt++;
        }
    }

    /**
     * write the content in a file
     *  
     * @param fileWriter object used to write in the file (it contains the file destination and name)
    */
    void printToFile(FileWriter fileWriter) throws IOException {
        int cpt = 1;
        for (Chapter chapter : this.chapters) {
            fileWriter.write("\n");
            chapter.printToFile(fileWriter, cpt);
            cpt++;
        }
    }
}
