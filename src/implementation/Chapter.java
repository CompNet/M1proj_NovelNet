package implementation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Chapter {

    protected ArrayList<Paragraph> paragraphs;
    protected ArrayList<CoreSentence> titles;

    Chapter(){
        paragraphs = new ArrayList<Paragraph>();
        titles = new ArrayList<CoreSentence>();
    }

    void addTitle(CoreSentence sentence){
        titles.add(sentence);
    }

    void addVoidParagraph(){
        paragraphs.add(new Paragraph());
    }

    void addParagraph(Paragraph paragraph){
        paragraphs.add(paragraph);
    }

    void display(int i){
        System.out.println("Chapter " + i + " : ");
        for (CoreSentence sentence : this.titles){
            System.out.print(sentence.text());
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

    void printToFile(FileWriter fileWriter, int i) throws IOException {
        fileWriter.write("Chapter " + i + " : ");
        for (CoreSentence sentence : this.titles){
            fileWriter.write(sentence.text());
        }
        int cpt = 1;
        for (Paragraph paragraph : this.paragraphs) {
            fileWriter.write("\n Paragraph " + cpt + " : ");
            paragraph.printToFile(fileWriter);
            cpt++;
        }
    }
}
