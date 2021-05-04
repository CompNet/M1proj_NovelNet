package novelnet.util;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import novelnet.book.Book;

/**
 * CustomTriple
 */
public class CustomTriple {

    protected RelationTriple triple;
    protected CustomCorefChain subject;
    protected CustomCorefChain object;
    protected CoreLabel verb;


    public CustomTriple() {
    }

    public CustomTriple(RelationTriple triple, CustomCorefChain subject, CustomCorefChain object) {
        this.triple = triple;
        this.subject = subject;
        this.object = object;
    }

    public CustomTriple(CustomCorefChain subject, CustomCorefChain object, CoreLabel verb) {
        this.subject = subject;
        this.object = object;
        this.verb = verb;
    }

    public CoreLabel getVerb() {
        return this.verb;
    }

    public void setVerb(CoreLabel verb) {
        this.verb = verb;
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

    public int characterNumber(){
        int count = 0;
        if (subject != null) count++;
        if (object != null) count++;
        return count;
    }

    @Override
    public String toString() {

        String result = "{" + "subject='" + getSubject().getRepresentativeName() + "'";
        if (getTriple() == null){
            result += " verb='" + getVerb().originalText() + "'";
        }
        else {
            result += " verb='" + getTriple().relationGloss() + "'";

        }
        if (getObject() != null) result += ", object='" + getObject().getRepresentativeName() + "'";
        result += "}";
        return result;
    }

}