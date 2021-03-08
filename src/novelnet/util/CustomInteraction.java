package novelnet.util;

import java.util.LinkedList;
import java.util.List;

import novelnet.book.Book;

import edu.stanford.nlp.ie.util.RelationTriple;


public class CustomInteraction {

    protected List<RelationTriple> triples;
    protected List<CustomCorefChain> subjects;
    protected List<CustomCorefChain> objects;
    protected int sentenceIndex;
    protected String type;

    public CustomInteraction() {
        triples = new LinkedList<>();
        subjects = new LinkedList<>();
        objects = new LinkedList<>();
        sentenceIndex = 0;
        type = null;
    }

    public CustomInteraction(List<RelationTriple> triples, Book book) {
        this.triples = triples;
        subjects = new LinkedList<>();
        objects = new LinkedList<>();
        sentenceIndex = triples.get(0).relation.get(0).sentIndex();
        setObjects(book);
        setSubjects(book);
        type = null;
    }


    public List<RelationTriple> getTriples() {
        return this.triples;
    }

    public void setTriples(List<RelationTriple> triples) {
        this.triples = triples;
    }

    public List<CustomCorefChain> getSubjects() {
        return this.subjects;
    }

    public void setSubjects(List<CustomCorefChain> subjects) {
        this.subjects = subjects;
    }

    public List<CustomCorefChain> getObjects() {
        return this.objects;
    }

    public void setObjects(List<CustomCorefChain> objects) {
        this.objects = objects;
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
    
    public void setObjects(Book book){
        for(RelationTriple rt : triples){
            for (CustomCorefChain ccc : book.getCorefChain()){
                if (ccc.contains(rt.objectHead()) && !objects.contains(ccc)){
                    objects.add(ccc);
                }
            }
        }
    }

    public void setSubjects(Book book){
        for(RelationTriple rt : triples){
            for (CustomCorefChain ccc : book.getCorefChain()){
                if (ccc.contains(rt.subjectHead()) && !subjects.contains(ccc)){
                    subjects.add(ccc);
                }
            }
        }
    }

    public void display(){
        System.out.println(triples);
        System.out.print("subjects : ");
        for (CustomCorefChain ccc : subjects){
            System.out.print("{" + ccc.representativeName + "}");
        }
        System.out.print("\nobjects : ");
        for (CustomCorefChain ccc : objects){
            System.out.print("{" + ccc.representativeName + "}");
        }
        System.out.println();
    }
}
