package book;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import util.CustomCorefChain;
import util.CustomEntityMention;

/**
 * Represent a Book.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class Book {

    /**
     * A list of all the chapters in the book.
    */
    protected LinkedList<Chapter> chapters;
    protected boolean entitiesPlaced;
    protected CoreDocument document;
    protected List<CustomCorefChain> corefChain;

    /**
     * Class Constructor. specifying the document 
     * 
    */
    public Book(CoreDocument document, List<CustomCorefChain> corefChainList){
        chapters = new LinkedList<>();
        entitiesPlaced = false;
        this.document = document;
        corefChain = corefChainList;
    }


    /**
     * Class Constructor. specifying the document and the list of chapters
     * 
    */
    public Book(LinkedList<Chapter> chapters, CoreDocument document) {
        this.chapters = chapters;
        entitiesPlaced = false;
        this.document = document;
    }

    public List<CustomCorefChain> getCorefChain() {
        return corefChain;
    }

    public void setCorefChain(List<CustomCorefChain> corefChain) {
        this.corefChain = corefChain;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(LinkedList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public boolean getEntitiesPlaced() {
        return entitiesPlaced;
    }

    public void setEntitiesPlaced(boolean entitiesPlaced) {
        this.entitiesPlaced = entitiesPlaced;
    }

    public CoreDocument getDocument() {
        return document;
    }

    public void setDocument(CoreDocument document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return "{" +
            " chapters='" + getChapters() + "'" +
            "}";
    }


    /**
     * add an empty chapter to the book.
    */
    public void addVoidChapter() {
        chapters.add(new Chapter(this));
    }

    /**
     * add a chapter to the book.
     * 
     * @param chapter chapter to add to the book.
    */
    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }

    /**
     * get the Index (in the coreDocument) of the sentence begining the paragraph.
     * 
     * @param paragraphNumber An Integer representing the paragraph number in the book.
     * @return An Integer representing the Index of the sentence begining the paragraph. -1 if the paragraph is not in the book.
    */
    public int getBeginIndexOfParagraph(int paragraphNumber){
        int tmp;
        for (Chapter chapter : this.chapters) {
            tmp = chapter.getBeginIndexOfParagraph(paragraphNumber);
            if (tmp != -1) return tmp;
        }
        return -1;
    }

    /**
     * get the Index (in the coreDocument) of the sentence ending the paragraph.
     * 
     * @param paragraphNumber An Integer representing the paragraph number in the book.
     * @return An Integer representing the Index of the sentence ending the paragraph.  -1 if the paragraph is not in the book.
    */
    public int getEndIndexOfParagraph(int paragraphNumber){
        int tmp;
        for (Chapter chapter : this.chapters) {
            tmp = chapter.getEndIndexOfParagraph(paragraphNumber);
            if (tmp != -1) return tmp;
        }
        return -1;
    }

    /**
     * get the Index (in the coreDocument) of the sentence begining the chapter.
     * 
     * @param chapterNumber An Integer representing the chapter number in the book.
     * @return An Integer representing the Index of the sentence begining the chapter. -1 if the chapter is not in the book.
    */
    public int getBeginIndexOfChapter(int chapterNumber){
        for (Chapter chapter : this.chapters) {
            if (chapter.chapterNumber == chapterNumber) return chapter.getBeginingSentenceIndex();
        }
        return -1;
    }

    /**
     * get the Index (in the coreDocument) of the sentence ending the chapter.
     * 
     * @param chapterNumber An Integer representing the chapter number in the book.
     * @return An Integer representing the Index of the sentence ending the chapter. -1 if the chapter is not in the book.
    */
    public int getEndIndexOfChapter(int chapterNumber){
        for (Chapter chapter : this.chapters) {
            if (chapter.chapterNumber == chapterNumber) return chapter.getEndingSentenceIndex();
        }
        return -1;
    }

    /**
     * display the content of the book in the console
    */
    public void display() {
        for (Chapter chapter : chapters) {
            System.out.println(" ");
            chapter.display();
        }
    }

    /**
     * write the content of the book in a file
     *  
     * @param fileWriter object used to write in the file (it contains the file destination and name)
    */
    public void printToFile(FileWriter fileWriter) throws IOException {
        for (Chapter chapter : this.chapters) {
            fileWriter.write("\n");
            chapter.printToFile(fileWriter);
        }
    }

    /**
     * get all the entities in the book
     *  
     * @return a list of all the entities in the book.
    */
    public List<CustomEntityMention> getEntities(){
        List<CustomEntityMention> result = new LinkedList<>();
        for(Chapter c : chapters){
            for (CustomEntityMention cem : c.getEntities()){
                result.add(cem);
            }
        }
        return result;
    }

    public int getBeginingSentenceIndex(){
        return chapters.getFirst().getBeginingSentenceIndex();
    }

    public int getEndingSentenceIndex(){
        return chapters.getLast().getEndingSentenceIndex();
    }

    public int getBeginingParagraphNumber(){
        return chapters.getFirst().getBeginingParagraphNumber();
    }

    public int getEndingParagraphNumber(){
        return chapters.getLast().getEndingParagraphNumber();
    }

    /**
     * get a Paragraph by its number
     *  
     * @param paragraphNumber the paragraph number
     * @return the Paragraph object with the according number
    */
	public Paragraph getParagraph(int paragraphNumber) {
        for (Chapter c : chapters){
            if (c.getParagraph(paragraphNumber) != null) return c.getParagraph(paragraphNumber);
        }
		return null;
    }

    /**
     * find all the entities of the document of the book with their best name.
     *  
     * @return a list of all the entities in the document.
    */
    /*private List<CustomEntityMention> findEntity(){
		LinkedList<CustomEntityMention> result = new LinkedList<>();
		for (CustomEntityMention em : entityMentions()){
			if (em.entityType().equals("PERSON") ){
				try {
                    result.add(new CustomEntityMention(em, em.bestName));
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
			}
		}
		return result;
	}*/

    public List<CustomEntityMention> getEntitiesFromCorefChains(){
        LinkedList<CustomEntityMention> result = new LinkedList<>();
		for (CustomCorefChain ccc : corefChain){
			for (CustomEntityMention cem : ccc.getCEMList()){
                result.add(cem);
            }
		}
		return result;
    }

    /**
     * place the entities of the document in the book according to their position in the document.
     *  
    */
    public void placeEntitites(){
        if (entitiesPlaced) return;
        int tmp;
        List<CustomEntityMention> entities = getEntitiesFromCorefChains();
        for (CustomEntityMention entity : entities){
            tmp = entity.getSentenceIndex();
			for(Chapter c : chapters){
                //if the index of the entity is in the chapter
                if(tmp >= c.getBeginingSentenceIndex() && tmp <= c.getEndingSentenceIndex()){
                    for (Paragraph p : c.getParagraphs()){
                        //if the index of the entity is in the chapter
                        if(tmp >= p.getBeginingSentence() && tmp <= p.getEndingSentence()){
                            p.addEntity(entity);
                        }
                    }
                }
            }
        }
        entitiesPlaced = true;
    }    
}
