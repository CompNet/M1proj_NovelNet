package implementation;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
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
        CoreLabel last = null;
        CoreLabel first = null;
        this.book = new Book();
        Chapter currentChapter = new Chapter();
        Paragraph currentParagraph = new Paragraph();
        
		for (CoreSentence sentence : sentences)
		{
            List <CoreLabel> mots = sentence.tokens();
			if(last != null){
                first = mots.get(0);
                if (first.beginPosition() - last.endPosition() >= 4){
                    currentParagraph = new Paragraph();
                    if (first.beginPosition() - last.endPosition() > 4){
                        currentChapter = new Chapter();
                        book.addChapter(currentChapter);
                    }
                    currentChapter.addParagraph(currentParagraph);
                } 
                currentParagraph.addSentence(sentence);
            }
            last = mots.get(mots.size()-1);
	    }
		
    }
}
