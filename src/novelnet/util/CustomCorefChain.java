package novelnet.util;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import performance.coref.CorefChainContainer;

public class CustomCorefChain implements Cloneable{

    List<CustomEntityMention> cEMList;
    String representativeName;
    int id;
    int clusterID;

    public CustomCorefChain(){
        cEMList = new LinkedList<>();
        representativeName = "";
        id = 0;
        clusterID = 0;
    }

    public CustomCorefChain(CorefChain cc){
        cEMList = new LinkedList<>();
        try {
            representativeName = cc.getRepresentativeMention().mentionSpan;
            for (CorefMention cm : cc.getMentionsInTextualOrder()){
                cEMList.add(new CustomEntityMention(cm, representativeName));
            }
        }
        catch(Exception e){System.out.println(e.getMessage());}
        id = 0;
        clusterID = 0;
    }

    public CustomCorefChain(CoreEntityMention cem){
        cEMList = new LinkedList<>();
        representativeName = cem.text();
        CustomEntityMention temp = new CustomEntityMention(cem);
        temp.setCorefChain(this);
        cEMList.add(temp);
        id = 0;
        clusterID = 0;
    }

    public CustomCorefChain(CustomEntityMention ce) {
        cEMList = new LinkedList<>();
        representativeName = ce.text();
        cEMList.add(ce);
        id = 0;
        clusterID = 0;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public List<CustomEntityMention> getCEMList() {
        return cEMList;
    }

    public int getClusterID() {
        return this.clusterID;
    }

    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
    }

    public void setCEMList(List<CustomEntityMention> cEMList) {
        this.cEMList = cEMList;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }


	public void setRepresentativeName(String representativeName) {
        for (CustomEntityMention cem : cEMList){
            cem.setBestName(representativeName);
        }
        this.representativeName = representativeName;
	}

    public Boolean contains(CoreLabel token){
        for(CustomEntityMention entity : cEMList){
            if ((entity.getSentenceIndex() == token.sentIndex()) &&
                (entity.tokenIndexs().first() <= token.index()) &&
                (entity.tokenIndexs().second() >= token.index()))
                {
                    return true;
                }
        }
        return false;
    }

    /**
     * used by CorefChainContainer.precision().
     *  
    */
    public double corefPrecision(CorefChainContainer reference){
        double tot = 0;
        for (CustomEntityMention ce : cEMList){
            tot += ce.precision(reference, this);
        }

        return tot;
    }

    /**
     * used by CorefChainContainer.precision().
     *  
    */
    public double corefRecall(CorefChainContainer reference) {
        double tot = 0;
        for (CustomEntityMention ce : cEMList){
            tot += ce.recall(reference, this);
        }

        return tot;
    }
    
    @Override
    public String toString() {
        String cemNames = "";
        String result = "{";
        for (int i = 0; i < cEMList.size(); i++){
            cemNames += cEMList.get(i).text();
            if (i != cEMList.size()-1) cemNames += ", ";
        }
        if (!cemNames.equals("")){
            result+= "cEMList='" + cemNames + "', ";
        }
        if (!representativeName.equals("")){
            result+= "representativeName='" + getRepresentativeName() + "', ";
        }
        if (getId() != 0) result += "chainID='" + getId() + "'";
        if (getClusterID() != 0) result += "clusterID='" + getClusterID() + "'";
        result += "}";
        return result;
    }

    @Override
    public Object clone() {
        Object o = null;
        try {
            // On récupère l'instance à renvoyer par l'appel de la 
            // méthode super.clone()
            o = super.clone();
        } catch(CloneNotSupportedException cnse) {
            // Ne devrait jamais arriver, car nous implémentons 
            // l'interface Cloneable
            cnse.printStackTrace(System.err);
        }
        // on renvoie le clone
        return o;
    }
}
