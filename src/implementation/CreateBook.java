package implementation;

import java.util.List;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;

public class CreateBook {
    
    protected Book book;

    CreateBook(){
        this.book = new Book();
    }

    Book getBook(){
        return book;
    }

    void createBook(CoreDocument document){
        List<CoreSentence> sentences = document.sentences();
        int previousLineSkip;
        int nextLineSkip;
        boolean titleDetection = true;

        this.book = new Book();
        Chapter currentChapter = new Chapter();
        book.addChapter(currentChapter);

        Paragraph currentParagraph = new Paragraph();
        
        currentChapter.addTitle(sentences.get(0));

        for (int i = 1; i < sentences.size()-1; i++)
		{
            previousLineSkip = sentences.get(i).tokens().get(0).beginPosition() - sentences.get(i-1).tokens().get(sentences.get(i-1).tokens().size()-1).endPosition();
            nextLineSkip = sentences.get(i+1).tokens().get(0).beginPosition() - sentences.get(i).tokens().get(sentences.get(i).tokens().size()-1).endPosition();
            if (previousLineSkip > 4 && nextLineSkip > 4){
                if(titleDetection){
                    currentChapter.addTitle(sentences.get(i));
                }
                else {
                    currentChapter = new Chapter();
                    book.addChapter(currentChapter);
                    titleDetection = true;
                    currentChapter.addTitle(sentences.get(i));
                }                
            }
            else if(previousLineSkip >= 4 && nextLineSkip <= 4){
                if (titleDetection) titleDetection = false;
                currentParagraph = new Paragraph();
                currentChapter.addParagraph(currentParagraph);
                currentParagraph.addSentence(sentences.get(i));
            }
            else{
                currentParagraph.addSentence(sentences.get(i));
            }

        }
        currentParagraph.addSentence(sentences.get(sentences.size()-1));
    }

}
