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

    public CustomTriple(RelationTriple triple, Book book){
        this.triple = triple;
        setSubject(book);
        setObject(book);
        verb = triple.relationHead();
    }

    public CustomTriple(RelationTriple triple, CustomCorefChain subject, CustomCorefChain object) {
        this.triple = triple;
        this.subject = subject;
        this.object = object;
        verb = triple.relationHead();
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

    public CoreLabel getObjectToken(){
        if (getObject() == null) return null;
        return getObject().getCEMList().get(0).getBeginToken();
    }

    public void setObject(CustomCorefChain object) {
        this.object = object;
    }

    public void setObject(Book book){
        for (CustomCorefChain ccc : book.getCorefChain()){
            if (ccc.contains(triple.objectHead()) && ccc != subject){
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

    public String niceTableDisplay(){
        String result = "";
        if (getTriple() != null){
            if (getSubject() != null){
                result += getTriple().subjectGloss() + " - ";
            }
            result += getTriple().relationHead().originalText();
            if (getObject() != null) result += " - " + getTriple().objectGloss();
        }

        else {
            if (getSubject() != null){
                result += getSubject().getRepresentativeName() + " - ";
            }
            result += getVerb().originalText();
            if (getObject() != null) result += " - " + getObject().getRepresentativeName();
        }

        return result;
    }

    @Override
    public String toString() {
        String result = "{";
        if (getTriple() != null){
            if (getSubject() != null){
                result += "subject='" + getTriple().subjectGloss() + "'";
            }
    
            if (getTriple() == null){
                result += " verb='" + getVerb().originalText() + "'";
            }
            else {
                result += " verb='" + getTriple().relationGloss() + "'";
    
            }
            if (getObject() != null) result += ", object='" + getTriple().objectGloss() + "'";
        }

        else {
            if (getSubject() != null){
                result += "subject='" + getSubject().getRepresentativeName() + "'";
            }
    
            if (getTriple() == null){
                result += " verb='" + getVerb().originalText() + "'";
            }
            if (getObject() != null) result += ", object='" + getObject().getRepresentativeName() + "'";
        }

        result += "}";
        return result;
    }

    public void displayTest(){
        System.out.println("\nnew Triplet :");
        System.out.println(getTriple());
        System.out.println(getSubject());
        System.out.println(getVerb());
        System.out.println(getObject());
    }

    public boolean equalTo(CustomTriple tripletToCompare) {
        //TODO try to simplify that kraken
        if (getTriple() == null){   //if this triple is from reference
            if (tripletToCompare.getObject() == null){
                if (getObject() == null){
                    if (ImpUtils.compareCoreLabel(getVerb(), tripletToCompare.getVerb()) && tripletToCompare.getSubject().contains(getSubject().getCEMList().get(0).getBeginToken())) return true;
                }
                else return false;
            }
            if(ImpUtils.compareCoreLabel(getVerb(), tripletToCompare.getVerb()) && tripletToCompare.getSubject().contains(getSubject().getCEMList().get(0).getBeginToken()) && tripletToCompare.getObject().contains(getObjectToken())){
                return true;
            }
        }
        else{   //if the triple to compare is from reference
            if (getObject() == null){
                if (tripletToCompare.getObject() == null){
                    if (ImpUtils.compareCoreLabel(getVerb(), tripletToCompare.getVerb()) && getSubject().contains(tripletToCompare.getSubject().getCEMList().get(0).getBeginToken())) return true;
                }
                else return false;
            }
            if(ImpUtils.compareCoreLabel(getVerb(), tripletToCompare.getVerb()) && getSubject().contains(tripletToCompare.getSubject().getCEMList().get(0).getBeginToken()) && getObject().contains(tripletToCompare.getObjectToken())){
                return true;
            }
        }
        return false;
    }

}