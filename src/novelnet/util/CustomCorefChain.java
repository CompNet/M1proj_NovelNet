package novelnet.util;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import performance.coref.CorefChainContainer;

/**
 * A customized class to store CorefChains
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
 */
public class CustomCorefChain implements Cloneable{

    /**
     * the entities contained in the chain
    */
    List<CustomEntityMention> cEMList;
    /**
     * the best name for the mentions in the chain
    */
    String bestName;
    /**
     * id of the chain
    */
    int id;
    /**
     * id of the chain's Character
    */
    int clusterID;

    /**
     * Class constructor
    */
    public CustomCorefChain(){
        cEMList = new LinkedList<>();
        bestName = "";
        id = 0;
        clusterID = 0;
    }

    /**
     * Class constructor from a stanford CorefChain
     * 
     * @param cc Stanford's CorefChain
    */
    public CustomCorefChain(CorefChain cc){
        cEMList = new LinkedList<>();
        try {
            bestName = cc.getRepresentativeMention().mentionSpan;
            for (CorefMention cm : cc.getMentionsInTextualOrder()){
                cEMList.add(new CustomEntityMention(cm, bestName));
            }
        }
        catch(Exception e){System.out.println(e.getMessage());}
        id = 0;
        clusterID = 0;
    }

    /**
     * Create a CustomCorefChain with one mention.
     * 
     * @param cem Stanford's CoreEntityMention
    */
    public CustomCorefChain(CoreEntityMention cem){
        cEMList = new LinkedList<>();
        bestName = cem.text();
        CustomEntityMention temp = new CustomEntityMention(cem);
        cEMList.add(temp);
        id = 0;
        clusterID = 0;
    }

    /**
     * Create a CustomCorefChain with one mention.
     * 
     * @param ce our CustomEntityMention
    */
    public CustomCorefChain(CustomEntityMention ce) {
        cEMList = new LinkedList<>();
        bestName = ce.text();
        cEMList.add(ce);
        id = 0;
        clusterID = 0;
    }

    public String getBestName() {
        return bestName;
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

	public void setBestName(String bestName) {
        for (CustomEntityMention cem : cEMList){
            cem.setBestName(bestName);
        }
        this.bestName = bestName;
	}

    /**
     * check if the chain contains an entity with the token passes as argument
     * 
     * @param token Stanford's CoreLabel
     * @return true if the chain contains the CoreLabel, false otherwise
    */
    public Boolean contains(CoreLabel token){
        if (token == null) return false;
        for(CustomEntityMention entity : cEMList){
            if ((entity.getSentenceIndex() == token.sentIndex()) &&
                (entity.tokensIndexs().first() <= token.index()) &&
                (entity.tokensIndexs().second() >= token.index()))
                {
                    return true;
                }
        }
        return false;
    }

    /**
     * used by CorefChainContainer.precision().
    */
    public double corefPrecision(CorefChainContainer reference){
        double tot = 0;
        for (CustomEntityMention ce : cEMList){
            tot += ce.corefPrecision(reference, this);
        }

        return tot;
    }

    /**
     * used by CorefChainContainer.precision().
    */
    public double corefRecall(CorefChainContainer reference) {
        double tot = 0;
        for (CustomEntityMention ce : cEMList){
            tot += ce.corefRecall(reference, this);
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
        if (!bestName.equals("")){
            result+= "bestName='" + getBestName() + "', ";
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
            o = super.clone();
        } catch(CloneNotSupportedException cnse) {
            cnse.printStackTrace(System.err);
        }
        return o;
    }
}
