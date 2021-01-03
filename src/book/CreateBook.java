package book;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


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
     * @return The book object created
    */
    public static Book createBook(CoreDocument document, boolean noTitle){
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
        boolean titleDetection = !noTitle;
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
            if (previousLineSkip >= 5){
                if (nextLineSkip >= 5){
                    if(titleDetection) {
                        //if the title detection was ON we just add the sentence to the title
                        currentChapter.addTitle(sentences.get(i));
                    }
                    else {
                        //else :
                        currentParagraph.endingSentence = i-1;
                        titleDetection = true;  // put the title detection to ON
                        chapterNumber++;
                        currentChapter = new Chapter(book, chapterNumber);  // create a new chapter 
                        book.addChapter(currentChapter);            // add the current chapter to the book 
                        currentChapter.addTitle(sentences.get(i));  // add the current sentence to the title of the chapter   
                    }
                    
                } 
                else if (titleDetection){
                    paragraphNumber++;
                    currentParagraph = new Paragraph(currentChapter, paragraphNumber);
                    currentChapter.addParagraph(currentParagraph);  //add the current paragraph to the chapter
                    currentParagraph.beginingSentence = i;
                    currentParagraph.addSentence(sentences.get(i)); //add the current sentence to the paragraph
                    titleDetection = false;
                }
                else{
                    currentParagraph.endingSentence = i-1;
                    chapterNumber++;
                    currentChapter = new Chapter(book, chapterNumber);  // create a new chapter 
                    book.addChapter(currentChapter);            // add the current chapter to the book
                    paragraphNumber++;
                    currentParagraph = new Paragraph(currentChapter, paragraphNumber);
                    currentChapter.addParagraph(currentParagraph);  //add the current paragraph to the chapter
                    currentParagraph.beginingSentence = i;
                    currentParagraph.addSentence(sentences.get(i)); //add the current sentence to the paragraph
                }  
            }
            //Else if there is a paragraph change (exactly 1 EOL char)
            else if(previousLineSkip == 2 || previousLineSkip == 4){
                currentParagraph.endingSentence = i-1;
                paragraphNumber++;
                currentParagraph = new Paragraph(currentChapter, paragraphNumber);   //create a new paragraph
                currentChapter.addParagraph(currentParagraph);  //add the current paragraph to the chapter
                currentParagraph.beginingSentence = i;
                currentParagraph.addSentence(sentences.get(i)); //add the current sentence to the paragraph
                
            }
            //else we add the sentence to the current paragraph
            else{
                currentParagraph.addSentence(sentences.get(i));
            }

        }
        currentParagraph.addSentence(sentences.get(sentences.size()-1)); //last line is in the last paragraph
        currentParagraph.endingSentence = sentences.size()-1;
        book.placeEntitites();
        return book;
    }
    

    public static void main(String[] args) { 
        // set up pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// manual book creation
        Book book = new Book(document);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphIndex(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphIndex(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphIndex(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphIndex(3);

		Chapter c1 = new Chapter();
		c1.addParagraph(p1);
		c1.addParagraph(p2);

		Chapter c2 = new Chapter();
		c2.addParagraph(p3);
		c2.addParagraph(p4);
		
		book.addChapter(c1);
        book.addChapter(c2);
        book.placeEntitites();

        book.display();
    }
}
