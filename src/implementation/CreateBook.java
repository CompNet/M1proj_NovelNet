package implementation;

import java.util.List;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;


/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
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
        boolean titleDetection = true;  //On considère que dès le début on cherche un titre.

        this.book = new Book();
        Chapter currentChapter = new Chapter();
        book.addChapter(currentChapter);

        Paragraph currentParagraph = new Paragraph();
        
        currentChapter.addTitle(sentences.get(0)); //On considère que la première ligne du livre sera toujours un titre

        for (int i = 1; i < sentences.size()-1; i++)
		{
            //Différence entre le dernier token de la sentence précédente et le premier token de la sentence actuelle
            previousLineSkip = sentences.get(i).tokens().get(0).beginPosition() - sentences.get(i-1).tokens().get(sentences.get(i-1).tokens().size()-1).endPosition();

            //Différence entre le dernier token de la sentence actuelle et le premier token de la sentence suivante
            nextLineSkip = sentences.get(i+1).tokens().get(0).beginPosition() - sentences.get(i).tokens().get(sentences.get(i).tokens().size()-1).endPosition();

            //Si les différences sont suppérieures à 4 la sentence fait partie du titre d'un chapitre
            if (previousLineSkip > 4 && nextLineSkip > 4){
                if(titleDetection){
                    //Si la détection du titre est en cours on ajoute la sentence au titre du chapitre
                    currentChapter.addTitle(sentences.get(i));
                }
                else {
                    //Sinon :
                    titleDetection = true;                      // on passe la détection à true,
                    currentChapter = new Chapter();             // on crée un nouveau chapitre, 
                    book.addChapter(currentChapter);            // on ajoute le chapitre au livre, 
                    currentChapter.addTitle(sentences.get(i));  // on ajoute la sentence au titre.
                }                
            }
            //Sinon si la différence précédente est suppérieure à 4 (signale la fin d'un titre d'un chapitre), égale à 4 (signale un changement de paragraphe).
            else if(previousLineSkip >= 4){
                if (titleDetection) titleDetection = false;     //Si la détection de titre était activée on la désactive.
                currentParagraph = new Paragraph();             //On crée un nouveau paragraphe,
                currentChapter.addParagraph(currentParagraph);  //on ajoute le paragraph au chapitre,
                currentParagraph.addSentence(sentences.get(i)); //on ajoute la sentence au paragraphe.
            }
            //Sinon on ajoute la sentence au paragraphe.
            else{
                currentParagraph.addSentence(sentences.get(i));
            }

        }
        currentParagraph.addSentence(sentences.get(sentences.size()-1)); //On considère que la dernière ligne du livre fera toujours partie du dernier paragraphe.
    }

}
