package novelnet.util;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.util.Pair;
import performance.coref.CorefChainContainer;

/**
 * A customized class to store EntityMention
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class CustomEntityMention {

    /**
     * the tokens composing the entity
    */
    List<CoreLabel> tokens;
    /**
     * the best name for this entity
    */
    String bestName;
    /**
     * the window of the entity in sentences
    */
    Pair<Integer, Integer> window;
    /**
     * the index of the first token and the last token of the entity
    */
    Pair<Integer, Integer> tokensIndexs;
    /**
     * the sentence number of the entity (index is number-1)
    */
    int sentenceNumber;

    /**
     * class constructor
    */
    public CustomEntityMention(List<CoreLabel> tokens, Pair<Integer, Integer> window, String bestName) {
        this.tokens = tokens;
        this.window = window;
        this.bestName = bestName;
        this.sentenceNumber = tokens.get(0).sentIndex()+1;
        sentenceNumber = tokens.get(0).sentIndex()+1;
        tokensIndexs = new Pair<>(tokens.get(0).index(), tokens.get(tokens.size()-1).index());
    }

    /**
     * use a CoreEntityMnetion from Stanford to build our entity
    */
    public CustomEntityMention(CoreEntityMention cem){
        tokens = new LinkedList<>();
        for (CoreLabel token : cem.tokens()){
            tokens.add(token);
        }
		sentenceNumber = cem.tokens().get(0).sentIndex()+1;
        window = new Pair<>(0,0);
        bestName = cem.text();
        tokensIndexs = new Pair<>(tokens.get(0).index(), tokens.get(tokens.size()-1).index());
	}

    /**
     * Copy a CustomEntityMention
    */
    public CustomEntityMention(CustomEntityMention cem){
        tokens = new LinkedList<>(cem.getTokens());
		sentenceNumber = cem.getSentenceNumber();
        window = cem.window;
        bestName = cem.bestName;
        tokensIndexs = cem.tokensIndexs;
	}

    /**
     * transform a corefMention into a CustomEntityMention
    */
    public CustomEntityMention(CorefMention cm, String bestName){
        this.bestName = bestName;
        tokens = new LinkedList<>();
        try{
            tokens.addAll(ImpUtils.getTokensbyCorefMention(cm));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
		sentenceNumber = cm.sentNum;
        window = new Pair<>(0,0);
        tokensIndexs = new Pair<>(tokens.get(0).index(), tokens.get(tokens.size()-1).index());
	}
	
    /**
     * class Contructor
    */
	public CustomEntityMention(String text, String bestName, int sentenceNumber, int firstTokenNumber, int lastTokenNumber) {
        this.bestName = bestName;
		tokens = new LinkedList<>();
        CoreLabel temp = new CoreLabel();
        temp.setOriginalText(text);
        temp.setIndex(firstTokenNumber);
        temp.setSentIndex(sentenceNumber-1);
        tokens.add(temp);
		this.sentenceNumber = sentenceNumber;
        window = new Pair<>(0,0);
        tokensIndexs = new Pair<>(firstTokenNumber, lastTokenNumber);
	}

    public Pair<Integer,Integer> getTokensIndexs() {
        return this.tokensIndexs;
    }

    public void setTokensIndexs(Pair<Integer,Integer> tokensIndexs) {
        this.tokensIndexs = tokensIndexs;
    }

	public int getSentenceNumber() {
		return sentenceNumber;
	}

	public void setSentenceNumber(int sentenceNumber) {
		this.sentenceNumber = sentenceNumber;
	}
    
    public List<CoreLabel> getTokens() {
        return tokens;
    }

    public void setTokens(List<CoreLabel> tokens){
        this.tokens = tokens;
    }

    public String getBestName(){
        return bestName;
    }

    public void setBestName(String bestName){
        this.bestName = bestName;
    }

    public int getWindowBegining(){
        return window.first();
    }

    public void setWindowBegining(int windowBegining){
        this.window.setFirst(windowBegining);
    }

    public int getWindowEnding(){
        return window.second();
    }

    public void setWindowEnding(int windowEnding){
        this.window.setSecond(windowEnding);
    }

    public Pair<Integer,Integer> getWindow() {
        return this.window;
    }

    public void setWindow(Pair<Integer,Integer> window) {
        this.window = window;
    }

    public int getSentenceIndex(){
        return getSentenceNumber()-1;
    }

    public Pair<Integer,Integer> charOffsets(){
        return new Pair<>(tokens.get(0).beginPosition(), tokens.get(tokens.size()-1).endPosition());
    }

    public Pair<Integer,Integer> tokensIndexs(){
        if (tokensIndexs == null) return new Pair<>(tokens.get(0).index(), tokens.get(tokens.size()-1).index());
        else return tokensIndexs;
    }

    public CoreLabel getBeginToken(){
        return tokens.get(0);
    }

    public CoreLabel getEndToken(){
        return tokens.get(tokens.size()-1);
    }

    public String text(){
        String text = "";
        boolean begin = true;
        for ( CoreLabel token : tokens){
            if (begin) {
                text += token.originalText();
                begin = false;
            }
            else text += " " + token.originalText();
        }
        return text;
    }

    /**
     * assess equals comparing sentenceNumber, and tokensIndexs
     * 
     * @param ce The CustomEntityMention to compare to.
     * @return true if sentenceNumber, tokensIndexs.first and tokensIndexs.second are equals, false otherwise
    */
    public boolean equalTo(CustomEntityMention ce){
        System.out.println(this.bestName + "\t" + this.sentenceNumber + "\t" + this.tokensIndexs().first() + "\t" + this.tokensIndexs().second());
        System.out.println(ce.bestName + "\t" + ce.sentenceNumber + "\t" + ce.tokensIndexs().first() + "\t" + ce.tokensIndexs().second());
		return this.sentenceNumber == ce.sentenceNumber && this.tokensIndexs().first() == ce.tokensIndexs().first() && this.tokensIndexs().second() == ce.tokensIndexs().second();
	}

    /**
     * used by CustomCorefChain.precision().
    */
    public double corefPrecision(CorefChainContainer reference, CustomCorefChain originChain) {
		double result = 0;
		for (CustomCorefChain cccRef : reference.getCorefChains()){
			if (cccRef.getCEMList().contains(this)){
				for (int i = 0; i < cccRef.getCEMList().size();i++){
					if (originChain.getCEMList().contains(cccRef.getCEMList().get(i))){
						result++;
					}
					if (i == cccRef.getCEMList().size()-1){
						result = result/originChain.getCEMList().size();
					}
				}
			}
		}
		return result;
    }

    /**
     * used by CustomCorefChain.recall().
    */
    public double corefRecall(CorefChainContainer reference, CustomCorefChain originChain) {
		double result = 0;
		for (CustomCorefChain cccRef : reference.getCorefChains()){
			if (cccRef.getCEMList().contains(this)){
				for (int i = 0; i < cccRef.getCEMList().size();i++){
					if (originChain.getCEMList().contains(cccRef.getCEMList().get(i))){
						result++;
					}
					if (i == cccRef.getCEMList().size()-1){
						result = result/cccRef.getCEMList().size();
					}
				}
			}
		}
		return result;
    }

    @Override
    public String toString() {
        return "{" +
            " name='" + text() + "'" +
            ", best name='" + getBestName() + "'" +
            ", window='" + getWindow() + "'" +
            "}";
    }

    public String originalText() {
        String result = "";
        for (int i = 0; i < tokens.size(); i++) {
           if (i != 0) result += " ";
           result += tokens.get(i).originalText();
        }
        return result;
    }
}
