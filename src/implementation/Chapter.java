package implementation;

import java.util.ArrayList;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Chapter {

    protected ArrayList<Paragraph> paragraphs;

    Chapter(){
        paragraphs = new ArrayList<Paragraph>();
    }

    void addVoidParagraph(){
        paragraphs.add(new Paragraph());
    }

    void addParagraph(Paragraph paragraph){
        paragraphs.add(paragraph);
    }

    void display(){
        System.out.println("new chapter");
        for (Paragraph paragraph : this.paragraphs){
            paragraph.display();
        }
        
    }
}
