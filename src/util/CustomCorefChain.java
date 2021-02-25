package util;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
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
        CustomEntityMention tmp;
        try {
            representativeName = cc.getRepresentativeMention().mentionSpan;
            //System.out.println("cc : " + cc);
            for (CorefMention cm : cc.getMentionsInTextualOrder()){
                CoreEntityMention cem = ImpUtils.getCoreEntityMentionByCorefMention(cm);
                //System.out.println( " texte : " + cm.mentionSpan + "\t cm start : " + cm.startIndex + "\t cm end : " + cm.endIndex + "\t tokens : "+ cem.text());
                if (cem != null) cEMList.add(new CustomEntityMention(cem, representativeName, this));
                else {
                    tmp = new CustomEntityMention(null, representativeName, this);
                    tmp.setTokens(ImpUtils.getTokensbyCorefMention(cm));
                    cEMList.add(tmp);
                }
                
            }
        }
        catch(Exception e){System.out.println(e.getMessage());}
    }

    public CustomCorefChain(CoreEntityMention cem){
        cEMList = new LinkedList<>();
        representativeName = cem.text();
        cEMList.add(new CustomEntityMention(cem, representativeName, this));
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
    
}
