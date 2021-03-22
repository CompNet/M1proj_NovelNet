package performance.coref;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import performance.ner.ComparableEntity;

public class ComparableCorefChain {

    List<ComparableEntity> entities;

    public ComparableCorefChain(){
        entities = new LinkedList<>();
    }

    public ComparableCorefChain(CorefChain stanfordCorefChain){
        entities = new LinkedList<>();
        for(CorefMention cm : stanfordCorefChain.getMentionsInTextualOrder()){
            entities.add(new ComparableEntity(cm.mentionSpan, cm.sentNum, cm.startIndex , cm.endIndex-1));
        }
        sortEntities();
    }

    public ComparableCorefChain(CoreEntityMention stanfordEntityMention){
        entities = new LinkedList<>();
        entities.add(new ComparableEntity(stanfordEntityMention));
    }

    public List<ComparableEntity> getEntities() {
        return this.entities;
    }

    public void setEntities(List<ComparableEntity> entities) {
        this.entities = entities;
        sortEntities();
    }

    public void sortEntities(){
        entities.sort(Comparator.comparing(ComparableEntity::getSentenceNumber).thenComparing(ComparableEntity::getTokenNumberFirst));
    }

    
    
}
