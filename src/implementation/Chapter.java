package implementation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * Chapter in a Book
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class Chapter {

    protected LinkedList<Paragraph> paragraphs; //paragraphs in the chapter
    protected LinkedList<CoreSentence> titles;  //if there is a title (and maybe a number) they should be here

    /**
     * Constructor
     * 
    */
    Chapter(){
        paragraphs = new LinkedList<>();
        titles = new LinkedList<>();
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
        paragraphs.add(new Paragraph());
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

    /**
     * write the content in a file
     *  
     * @param fileWriter object used to write in the file (it contains the file destination and name)
    */
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

    public List<Paragraph> getParagraphs(){
        return this.paragraphs;
    }
}
