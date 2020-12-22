package pipeline;

import java.util.List;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;


/**
 * Used to Create a book object from a Stanford core nlp CoreDocument
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class CreateBook {
    
    private static CreateBook instance;
    /**
     * Constructor 
    */
    private CreateBook(){
    }

    public static CreateBook getInstance(){
        if (instance == null){
            instance = new CreateBook();
        }
        return instance;
    }

    /**
     * create the book from a CoreDocument
     * 
     * @param document Stanford core nlp CoreDocument representing a book
    */
    public static Book createBook(CoreDocument document){
        List<CoreSentence> sentences = document.sentences();    //list of the sentences in the document
        int previousLineSkip;   //used to store the difference between the last token of the previous sentence and 
                                //the first token of the current sentence
        int nextLineSkip;       //used to store the difference between the last token of the current sentence and 
                                //the first token of the next sentence

        boolean titleDetection = true;  //title detection is on by default at the beginning of the document

        int chapterNumber = 1;
        int paragraphNumber = 1;

        Book book = new Book();
        Chapter currentChapter = new Chapter(book, 1);
        book.addChapter(currentChapter);

        Paragraph currentParagraph = new Paragraph(currentChapter, 1);
        
        currentChapter.addTitle(sentences.get(0)); //the first sentence is considerated as a title

        //for each sentence in the document exept the last one
        for (int i = 1; i < sentences.size()-1; i++)
		{
            //difference between the last token of the previous sentence and the first token of the current sentence
            previousLineSkip = sentences.get(i).tokens().get(0).beginPosition() - sentences.get(i-1).tokens().get(sentences.get(i-1).tokens().size()-1).endPosition();

            //difference between the last token of the current sentence and the first token of the next sentence
            nextLineSkip = sentences.get(i+1).tokens().get(0).beginPosition() - sentences.get(i).tokens().get(sentences.get(i).tokens().size()-1).endPosition();

            //if there is more than 2 EOL characters the sentence is a chapter title (EOL char are considered as 2 char)
            if (previousLineSkip > 4 && nextLineSkip > 4){
                if(titleDetection){
                    //if the title detection was ON we just add the sentence to the title
                    currentChapter.addTitle(sentences.get(i));
                }
                else {
                    //else :
                    currentParagraph.endingSentence = i-1;
                    titleDetection = true;                      // put the title detection to ON
                    chapterNumber++;
                    currentChapter = new Chapter(book, chapterNumber);  // create a new chapter 
                    book.addChapter(currentChapter);            // add the current chapter to the book 
                    currentChapter.addTitle(sentences.get(i));  // add the current sentence to the title of the chapter
                }                
            }
            //Else if the previous line was a title (more than 2 EOL char) or there is a paragraph change (exactly 1 EOL char)
            else if(previousLineSkip >= 2){
                if (titleDetection) titleDetection = false;     //if the title detection is ON switch it OFF
                else currentParagraph.endingSentence = i-1;
                paragraphNumber++;
                currentParagraph = new Paragraph(currentChapter, paragraphNumber);   //create a new paragraph
                currentChapter.addParagraph(currentParagraph);  //add the current paragraph to the chapter
                currentParagraph.addSentence(sentences.get(i)); //add the current sentence to the paragraph
                currentParagraph.beginingSentence = i;
            }
            //else we add the sentence to the current paragraph
            else{
                currentParagraph.addSentence(sentences.get(i));
            }

        }
        currentParagraph.addSentence(sentences.get(sentences.size()-1)); //last line is in the last paragraph
        currentParagraph.endingSentence = sentences.size()-1;
        return book;
    }
    
}
