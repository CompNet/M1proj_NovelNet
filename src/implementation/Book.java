package implementation;

import java.util.ArrayList;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Book {

    protected ArrayList<Chapter> chapters;

    Book(){
        chapters = new ArrayList<Chapter>();
    }

    void addVoidChapter(){
        chapters.add(new Chapter());
    }

    void addChapter(Chapter chapter){
        chapters.add(chapter);
    }

    void display(){
        for (Chapter chapter : this.chapters){
            chapter.display();
        }  
    }
}
