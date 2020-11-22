package implementation;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Book {

    protected ArrayList<Chapter> chapters;

    Book() {
        chapters = new ArrayList<Chapter>();
    }

    void addVoidChapter() {
        chapters.add(new Chapter());
    }

    void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }

    void display() {
        int cpt = 1;

        for (Chapter chapter : this.chapters) {
            System.out.println(" ");
            chapter.display(cpt);
            cpt++;
        }
    }

    void printToFile(FileWriter fileWriter) throws IOException {
        int cpt = 1;
        for (Chapter chapter : this.chapters) {
            fileWriter.write("\n");
            chapter.printToFile(fileWriter, cpt);
            cpt++;
        }
    }
}
