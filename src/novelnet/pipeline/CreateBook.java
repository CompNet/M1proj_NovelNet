package novelnet.pipeline;

import java.util.List;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import novelnet.book.Book;
import novelnet.book.Chapter;
import novelnet.book.Paragraph;
import novelnet.util.CustomCorefChain;


/**
 * Used to Create a book object from a Stanford core nlp CoreDocument
 * 
 * @author Quay Baptiste, Lemaire Tewis
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
    public static Book createBook(CoreDocument document, boolean notATitle, List<CustomCorefChain> corefChainList){
        List<CoreSentence> sentences = document.sentences();    //list of the sentences in the document
        int previousLineSkip;   //used to store the difference between the last token of the previous sentence and 
                                //the first token of the current sentence
        int nextLineSkip;       //used to store the difference between the last token of the current sentence and 
                                //the first token of the next sentence

        int chapterNumber = 0;
        int paragraphNumber = -1;

        Book book = new Book(document, corefChainList);
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
        //for each sentence in the document exept the first one.
        for (int i = 1; i < sentences.size(); i++)
		{
            //difference between the last token of the previous sentence and the first token of the current sentence
            previousLineSkip = sentences.get(i).tokens().get(0).beginPosition() - sentences.get(i-1).tokens().get(sentences.get(i-1).tokens().size()-1).endPosition();

            //difference between the last token of the current sentence and the first token of the next sentence
            if (i < sentences.size()-1) nextLineSkip = sentences.get(i+1).tokens().get(0).beginPosition() - sentences.get(i).tokens().get(sentences.get(i).tokens().size()-1).endPosition();
            else nextLineSkip = 0;

            //if there is more than 2 EOL characters the sentence is a chapter title (EOL char are considered as 2 char)
            if (previousLineSkip >= 6){
                if (nextLineSkip >= 6){
                    //if we are not already in a title
                    if (!titleDetection) {
                        currentParagraph.setEndingSentence(i-1);  //end the last paragraph
                        titleDetection = true;  // put the title detection to ON
                        //new Chapter creation
                        chapterNumber++;
                        currentChapter = new Chapter(book, chapterNumber);  // create a new chapter 
                        book.addChapter(currentChapter);            // add the current chapter to the book 
                    }
                    currentChapter.addTitle(sentences.get(i)); //add the title to the chapter
                } 
                //if previousLineSkip >= 6 but nextLineSkip < 6. IE if there is a chapter change but no title or the last sentence was the last title.
                else{
                    //if we were in a title detection turn it off
                    if (titleDetection){
                        titleDetection = false;
                    }
                    else{
                        currentParagraph.setEndingSentence(i-1);  //if there was no title we did not end the last paragraph so we do it.
                        //new Chapter creation
                        chapterNumber++;
                        currentChapter = new Chapter(book, chapterNumber);  // create a new chapter 
                        book.addChapter(currentChapter);            // add the current chapter to the book
                    }
                    //new Paragraph creation
                    paragraphNumber++;
                    currentParagraph = new Paragraph(currentChapter, paragraphNumber);
                    currentChapter.addParagraph(currentParagraph);  //add the current paragraph to the chapter
                    currentParagraph.setBeginingSentence(i);
                    currentParagraph.addSentence(sentences.get(i)); //add the current sentence to the paragraph
                }  
            }
            //if there is a paragraph change or no parargraph change
            else {
                //if there is a paragraph change (exactly 1 or 2 EOL char)
                if(previousLineSkip == 2 || previousLineSkip == 4){
                    currentParagraph.setEndingSentence(i-1);  //ending the last paragraph
                    //new Paragraph creation
                    paragraphNumber++;
                    currentParagraph = new Paragraph(currentChapter, paragraphNumber);   //create a new paragraph
                    currentChapter.addParagraph(currentParagraph);  //add the current paragraph to the chapter
                    currentParagraph.setBeginingSentence(i);
                }
                currentParagraph.addSentence(sentences.get(i)); //add the current sentence to the paragraph
                if (i == sentences.size()-1) currentParagraph.setEndingSentence(i);
            }
        }
        book.placeEntitites();
        return book;
    }
}
