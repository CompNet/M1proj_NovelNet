package util;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.CoreEntityMention;

public class CustomCorefChain{

    List<CoreEntityMention> cEMList;
    String representativeName;

    public CustomCorefChain(){
        cEMList = new LinkedList<>();
        representativeName = "";
    }

    public CustomCorefChain(CorefChain cc) throws NullDocumentException {
        cEMList = new LinkedList<>();
        for (CorefMention cm : cc.getMentionsInTextualOrder()){
            cEMList.add(ImpUtils.getCoreEntityMentionByCorefMention(cm));
        }
        representativeName = cc.getRepresentativeMention().mentionSpan;
    }

    public CustomCorefChain(CoreEntityMention cem){
        cEMList = new LinkedList<>();
        cEMList.add(cem);
        representativeName = cem.text();
    }

    @Override
    public String toString() {
        return "{" +
            " cEMList='" + getCEMList() + "'" +
            ", representativeName='" + getRepresentativeName() + "'" +
            "}";
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public List<CoreEntityMention> getCEMList() {
        return cEMList;
    }

	public void setRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
	}
    
}
