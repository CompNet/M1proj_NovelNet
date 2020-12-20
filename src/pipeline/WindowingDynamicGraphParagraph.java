package pipeline;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * Create windows for the creation of dynamics sociograms.
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class WindowingDynamicGraphParagraph extends WindowingDynamicGraph {
    
    /**
     * Constructor
     * 
	 * @param book entry book as a Class
	*/
    public WindowingDynamicGraphParagraph(Book book){
        super(book);
    }

    /**
     * Create a list of lists of sentences to use in the WindowingCooccurrenceSentence
     * (apply the createGraph method on each List<CoreSentence>)
     * 
	 * @param size number of paragraphs in the window
     * @param covering number of paragraphs for the covering between 2 windows (the sentences will be in either windows) define to 0 to have a sequential algorithm
     * @return the list of lists of Sentences in the window
	*/
    @Override
    public List<List<CoreSentence>> toSentences(int size, int covering) {
        List<List<CoreSentence>> result = new LinkedList<>(); //list to return

        //the covering cant be greater than the size
        if(covering >= size){
            System.out.println("Erreur la taille de la fenêtre est plus petite ou égale au recouvrement.");
            return result;
        }

        int cpt; //counter for the delimitation of windows
        List<CoreSentence> list; //temporary list to put in result.
        Chapter c;  //temporary chapter
        Paragraph p;    //temporary paragraph

        //for each chapter
        for(int i = 0; i<book.chapters.size(); i++){

            list = new LinkedList<>();  //Override the temporary list (we dont use paragraph from different chapters)
            cpt = 0;    // reset counter
            c = book.chapters.get(i);   //current chapter

            //for each paragraph
            for (int j = 0; j < c.paragraphs.size(); j++){
                //Window delimitation
                if(cpt == size){    
                    result.add(list);   //add the temporary list to the result
                    list = new LinkedList<>();  //Override the temporary list
                    cpt = 0;    //reset counter
                    j -= covering;  //backtrack for the covering between windows
                }

                p = c.paragraphs.get(j);    //current paragraph
            
                //for each sentence
                for (int k = 0; k < p.sentences.size(); k++){
                    list.add(p.sentences.get(k));   //add the sentence to the list
                }
                cpt++;  //increment the counter
            }
            result.add(list);   //if the count is not perfect we add the rest to the result (for exemple size is 5 but there is only 3 paragraphs left)
        }
        
        return result;
    }

    /**
     * Create a list of lists of Paragraphs to use in the WindowingCooccurrenceParagraph
     * (apply the createGraph method on each List<Paragraph>)
     * 
	 * @param size number of paragraphs in the window
     * @param covering number of paragraphs for the covering between 2 windows (the paragraphs will be in either windows) define to 0 to have a sequential algorithm
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

        int cpt;    //counter for the delimitation of windows
        List<Paragraph> list;   //temporary list to put in result.
        Chapter c;  //temporary chapter

        //for each chapter
        for(int i = 0; i<book.chapters.size(); i++){

            list = new LinkedList<>();  //Override the temporary list
            cpt = 0;    //reset the counter
            c = book.chapters.get(i);   //temporary chapter

            //for each paragraph
            for (int j = 0; j < c.paragraphs.size(); j++){
                //Window delimitation
                if(cpt == size){    
                    result.add(list);   //add the temporary list to the result
                    list = new LinkedList<>();  //Override the temporary list
                    cpt = 0;    //reset counter
                    j -= covering;  //backtrack for the covering between windows
                }

                list.add(c.paragraphs.get(j));  //add the current paragraph to the list
                cpt++;  //increment the counter
            }
            result.add(list);   //if the count is not perfect we add the rest to the result (for exemple size is 5 but there is only 3 paragraphs left)
        }
        
        return result;
    }
    
}
