package performance.coref;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import performance.ner.ComparableEntity;

public class ComparableCorefChain {

    private List<ComparableEntity> entities;
    private int id;

    public ComparableCorefChain(){
        entities = new LinkedList<>();
        id = 0;
    }

    public ComparableCorefChain(CorefChain stanfordCorefChain, int id){
        entities = new LinkedList<>();
        for(CorefMention cm : stanfordCorefChain.getMentionsInTextualOrder()){
            entities.add(new ComparableEntity(cm.mentionSpan, cm.sentNum, cm.startIndex , cm.endIndex-1));
        }
        sortEntities();
        this.id = id;
    }

    public ComparableCorefChain(ComparableEntity ce, int id){
        entities = new LinkedList<>();
        entities.add(ce);
        this.id = id;
    }

    public ComparableCorefChain(CoreEntityMention stanfordEntityMention, int id){
        entities = new LinkedList<>();
        entities.add(new ComparableEntity(stanfordEntityMention));
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" +
            "Id = " + getId() + " ; " +
            " entities=" + getEntities() +
            "}";
    }

    public List<ComparableEntity> getEntities() {
        return this.entities;
    }

    public void setEntities(List<ComparableEntity> entities) {
        this.entities = entities;
        sortEntities();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void sortEntities(){
        entities.sort(Comparator.comparing(ComparableEntity::getSentenceNumber).thenComparing(ComparableEntity::getTokenNumberFirst));
    }

    public double precision(ComparableCorefChainContainer reference){
        double tot = 0;
        for (ComparableEntity ce : entities){
            tot += ce.precision(reference, this);
        }

        return tot;
    }
    
}
