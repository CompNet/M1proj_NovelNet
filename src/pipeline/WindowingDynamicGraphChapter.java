package pipeline;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * Create windows for the creation of dynamics sociograms.
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class WindowingDynamicGraphChapter extends WindowingDynamicGraph {
    
    /**
     * Constructor
     * 
	 * @param book entry book as a Class
	*/
    public WindowingDynamicGraphChapter(Book book){
        super(book);
    }

    /**
     * Create a list of lists of sentences to use in the WindowingCooccurrenceSentences
     * (apply the createGraph method on each List<CoreSentence>)
     * 
	 * @param size number of chapters in the window
     * @param covering number of chapters for the covering between 2 windows (the chapters will be in either windows) define to 0 to have a sequential algorithm
     * @return the list of lists of sentences in the window
	*/
    @Override
    public List<List<CoreSentence>> toSentences(int size, int covering) {
        List<List<CoreSentence>> result = new LinkedList<>(); //list to return

        //the covering cant be greater than the size
        if(covering >= size){
            System.out.println("Erreur la taille de la fenêtre est plus petite ou égale au recouvrement.");
            return result;
        }
        
        int cpt = 0; //counter for the delimitation of windows
        List<CoreSentence> list = new LinkedList<>(); //temporary list to put in result.
        Chapter c;      //temporary chapter
        Paragraph p;    //temporary paragraph

        //for each chapter
        for(int i = 0; i<book.chapters.size(); i++){        
            
            //Window delimitation
            if(cpt == size){    
                result.add(list);   //add the temporary list to the result
                list = new LinkedList<>();  //Override the temporary list
                cpt = 0;    //reset counter
                i -= covering;  //backtrack for the covering between windows
            }

            c = book.chapters.get(i);   //current chapter
            //for each paragraph
            for (int j = 0; j < c.paragraphs.size(); j++){
                p = c.paragraphs.get(j); //current paragraph
                //for each sentence
                for (int k = 0; k < p.sentences.size(); k++){
                    list.add(p.sentences.get(k));   //add the sentence to the list
                }
            }
            cpt++;  //increment the counter
        }
        result.add(list);   //if the count is not perfect we add the rest to the result (for exemple size is 3 but there is only 2 chapters left)
        return result;
    }

    /**
     * Create a list of lists of Paragraphs to use in the WindowingCooccurrenceParagraph
     * (apply the createGraph method on each List<Paragraph>)
     * 
	 * @param size number of chapters in the window
     * @param covering number of chapters for the covering between 2 windows (the chapters will be in either windows) define to 0 to have a sequential algorithm
     * @return the list of lists of Paragraphs in the window
	*/
    @Override
    public List<List<Paragraph>> toParagraphs(int size, int covering) {
        List<List<Paragraph>> result = new LinkedList<>(); //list to return

        //the covering cant be greater than the size
        if(covering >= size){
            System.out.println("Erreur la taille de la fenêtre est plus petite ou égale au recouvrement.");
            return result;
        }

        int cpt = 0; //counter for the delimitation of windows
        List<Paragraph> list = new LinkedList<>(); //temporary list to put in result.
        Chapter c;      //temporary chapter

        //for each chapter
        for(int i = 0; i<book.chapters.size(); i++){
            c = book.chapters.get(i);   //current chapter
            
            //Window delimitation
            if(cpt == size){    
                result.add(list);   //add the temporary list to the result
                list = new LinkedList<>();  //Override the temporary list
                cpt = 0;    //reset counter
                i -= covering;  //backtrack for the covering between windows
            }
            //for each paragraph
            for (int j = 0; j < c.paragraphs.size(); j++){
                list.add(c.paragraphs.get(j));  //add current paragraph to the list
            }
            cpt++;  //increment the counter
        }
        result.add(list);   //if the count is not perfect we add the rest to the result (for exemple size is 3 but there is only 2 chapters left)
        return result;
    }
    
}
