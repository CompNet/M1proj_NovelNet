package book;

import java.util.List;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;


/**
 * Used to Create a book object from a Stanford core nlp CoreDocument
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class CreateBook {

    /**
     * Class Constructor 
    */
    private CreateBook(){
    }

    /**
     * create the book from a CoreDocument
     * 
     * @param document Stanford core nlp CoreDocument representing a text
     * @param notATitle a boolean used to determine if the first sentence is a title
     * @return The book object created
    */
    public static Book createBook(CoreDocument document, boolean notATitle){
        List<CoreSentence> sentences = document.sentences();    //list of the sentences in the document
        int previousLineSkip;   //used to store the difference between the last token of the previous sentence and 
                                //the first token of the current sentence
        int nextLineSkip;       //used to store the difference between the last token of the current sentence and 
                                //the first token of the next sentence

        int chapterNumber = 0;
        int paragraphNumber = -1;

        Book book = new Book(document);
        Chapter currentChapter = new Chapter(book, chapterNumber);
        book.addChapter(currentChapter);

        Paragraph currentParagraph = new Paragraph(currentChapter, paragraphNumber);
        boolean titleDetection = !notATitle;
        if (titleDetection){
            currentChapter.addTitle(sentences.get(0)); //if the first sentence is considerated as a title
        }
        else {
            paragraphNumber++;
            currentParagraph = new Paragraph(currentChapter, paragraphNumber);
            currentParagraph.addSentence(sentences.get(0));
            currentChapter.addParagraph(currentParagraph);
        }
        //for each sentence in the document exept the last one
        for (int i = 1; i < sentences.size()-1; i++)
		{
            //difference between the last token of the previous sentence and the first token of the current sentence
            previousLineSkip = sentences.get(i).tokens().get(0).beginPosition() - sentences.get(i-1).tokens().get(sentences.get(i-1).tokens().size()-1).endPosition();

            //difference between the last token of the current sentence and the first token of the next sentence
            nextLineSkip = sentences.get(i+1).tokens().get(0).beginPosition() - sentences.get(i).tokens().get(sentences.get(i).tokens().size()-1).endPosition();

            /*System.out.println("prev : " + previousLineSkip);
            System.out.println("next : " + nextLineSkip);*/
            //if there is more than 2 EOL characters the sentence is a chapter title (EOL char are considered as 2 char)
            if (previousLineSkip >= 6){
                if (nextLineSkip >= 6){
                    if (!titleDetection) {
                        currentParagraph.endingSentence = i-1;
                        titleDetection = true;  // put the title detection to ON
                        chapterNumber++;
                        currentChapter = new Chapter(book, chapterNumber);  // create a new chapter 
                        book.addChapter(currentChapter);            // add the current chapter to the book 
                    }
                    currentChapter.addTitle(sentences.get(i));
                    
                } 
                else{
                    if (titleDetection){
                        titleDetection = false;
                    }
                    else{
                        currentParagraph.endingSentence = i-1;
                        chapterNumber++;
                        currentChapter = new Chapter(book, chapterNumber);  // create a new chapter 
                        book.addChapter(currentChapter);            // add the current chapter to the book
                    }
                    paragraphNumber++;
                    currentParagraph = new Paragraph(currentChapter, paragraphNumber);
                    currentChapter.addParagraph(currentParagraph);  //add the current paragraph to the chapter
                    currentParagraph.beginingSentence = i;
                    currentParagraph.addSentence(sentences.get(i)); //add the current sentence to the paragraph
                }  
            }
            else {
                //if there is a paragraph change (exactly 1 or 2 EOL char)
                if(previousLineSkip == 2 || previousLineSkip == 4){
                    currentParagraph.endingSentence = i-1;
                    paragraphNumber++;
                    currentParagraph = new Paragraph(currentChapter, paragraphNumber);   //create a new paragraph
                    currentChapter.addParagraph(currentParagraph);  //add the current paragraph to the chapter
                    currentParagraph.beginingSentence = i;
                }
                currentParagraph.addSentence(sentences.get(i)); //add the current sentence to the paragraph
            }
        }
        currentParagraph.addSentence(sentences.get(sentences.size()-1)); //last line is in the last paragraph
        currentParagraph.endingSentence = sentences.size()-1;
        book.placeEntitites();
        return book;
    }
}
