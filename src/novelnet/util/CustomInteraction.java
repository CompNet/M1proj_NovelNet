package novelnet.util;

import java.util.LinkedList;
import java.util.List;

import novelnet.book.Book;

import edu.stanford.nlp.ie.util.RelationTriple;


public class CustomInteraction {

    protected List<CustomTriple> triples;
    protected int sentenceIndex;
    protected String type;

    public CustomInteraction() {
        triples = new LinkedList<>();
        sentenceIndex = 0;
        type = null;
    }

    public CustomInteraction(List<RelationTriple> triples, Book book) {
        CustomTriple tmpTriple;
        this.triples = new LinkedList<>();
        for (RelationTriple relationTriple : triples) {
            tmpTriple = new CustomTriple();
            tmpTriple.setTriple(relationTriple);
            tmpTriple.setObject(book);
            tmpTriple.setSubject(book);

            this.triples.add(tmpTriple);
        }
        sentenceIndex = triples.get(0).relation.get(0).sentIndex();
        type = null;
    }

    public CustomInteraction(List<CustomTriple> triples){
        this.triples = triples;
        sentenceIndex = triples.get(0).getTriple().relation.get(0).sentIndex();
        type = null;
    }

    public List<CustomTriple> getTriples() {
        return this.triples;
    }

    public void setTriples(List<CustomTriple> triples) {
        this.triples = triples;
    }

    public int getSentenceIndex() {
        return this.sentenceIndex;
    }

    public void setSentenceIndex(int sentenceIndex) {
        this.sentenceIndex = sentenceIndex;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CustomCorefChain> getSubjects(){
        List<CustomCorefChain> result = new LinkedList<>();
        for (CustomTriple customTriple : triples) {
            if (!result.contains(customTriple.getSubject())) result.add(customTriple.getSubject());
        }
        return result;
    }

    public List<CustomCorefChain> getObjects(){
        List<CustomCorefChain> result = new LinkedList<>();
        for (CustomTriple customTriple : triples) {
            if (!result.contains(customTriple.getObject())) result.add(customTriple.getObject());
        }
        return result;
    }

    public void display(){
        for (CustomTriple customTriple : triples) {
            System.out.println(customTriple);
        }
    }

    @Override
    public String toString() {
        return "{" +
            " triples='" + getTriples() + "'" +
            ", sentenceIndex='" + getSentenceIndex() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }

	public int characterNumber() {
		int count = 0;
        for (CustomTriple customTriple : triples) {
            count += customTriple.characterNumber();
        }
        return count;
	}

}
