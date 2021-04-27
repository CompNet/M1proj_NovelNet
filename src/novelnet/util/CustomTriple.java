package novelnet.util;

import edu.stanford.nlp.ie.util.RelationTriple;
import novelnet.book.Book;

/**
 * CustomTriple
 */
public class CustomTriple {

    protected RelationTriple triple;
    protected CustomCorefChain subject;
    protected CustomCorefChain object;


    public CustomTriple() {
    }

    public CustomTriple(RelationTriple triple, CustomCorefChain subject, CustomCorefChain object) {
        this.triple = triple;
        this.subject = subject;
        this.object = object;
    }

    public RelationTriple getTriple() {
        return this.triple;
    }

    public void setTriple(RelationTriple triple) {
        this.triple = triple;
    }

    public CustomCorefChain getSubject() {
        return this.subject;
    }

    public void setSubject(CustomCorefChain subject) {
        this.subject = subject;
    }

    public CustomCorefChain getObject() {
        return this.object;
    }

    public void setObject(CustomCorefChain object) {
        this.object = object;
    }

    public void setObject(Book book){
        for (CustomCorefChain ccc : book.getCorefChain()){
            if (ccc.contains(triple.objectHead())){
                object = ccc;
            }
        }
    }

    public void setSubject(Book book){
        for (CustomCorefChain ccc : book.getCorefChain()){
            if (ccc.contains(triple.subjectHead())){
                subject = ccc;
            }
        }
    }

    @Override
    public String toString() {
        return "{" +
            ", subject='" + getSubject().getRepresentativeName() + "'" +
            " verb='" + getTriple().relationGloss() + "'" +
            ", object='" + getObject().getRepresentativeName() + "'" +
            "}";
    }

}