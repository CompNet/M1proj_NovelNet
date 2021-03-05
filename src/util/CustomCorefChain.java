package util;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreEntityMention;

public class CustomCorefChain{

    List<CustomEntityMention> cEMList;
    String representativeName;

    public CustomCorefChain(){
        cEMList = new LinkedList<>();
        representativeName = "";
    }

    public CustomCorefChain(CorefChain cc){
        cEMList = new LinkedList<>();
        try {
            representativeName = cc.getRepresentativeMention().mentionSpan;
            for (CorefMention cm : cc.getMentionsInTextualOrder()){
                cEMList.add(new CustomEntityMention(ImpUtils.getTokensbyCorefMention(cm), representativeName, this));                
            }
        }
        catch(Exception e){System.out.println(e.getMessage());}
    }

    public CustomCorefChain(CoreEntityMention cem){
        cEMList = new LinkedList<>();
        representativeName = cem.text();
        cEMList.add(new CustomEntityMention(cem.tokens(), representativeName, this));
        
    }

    @Override
    public String toString() {
        String cemNames = "";
        for (int i = 0; i < cEMList.size(); i++){
            cemNames += cEMList.get(i).text();
            if (i != cEMList.size()-1) cemNames += ", ";
        } 
        return "{" +
            " cEMList='" + cemNames + "'" +
            ", representativeName='" + getRepresentativeName() + "'" +
            "}";
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public List<CustomEntityMention> getCEMList() {
        return cEMList;
    }

	public void setRepresentativeName(String representativeName) {
        for (CustomEntityMention cem : cEMList){
            cem.setBestName(representativeName);
        }
        this.representativeName = representativeName;
	}

    public Boolean contains(CoreLabel token){
        for(CustomEntityMention entity : cEMList){
            //System.out.println("original tokens : " + tokens + "\t entity tokens : " + entity.tokens + "\tif result : " + tokens.equals(entity.tokens));
            if (entity.tokens.contains(token)) return true;
        }
        return false;
    }    
}
