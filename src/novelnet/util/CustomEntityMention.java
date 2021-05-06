package novelnet.util;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.util.Pair;
import performance.coref.CorefChainContainer;

public class CustomEntityMention {

    CustomCorefChain corefChain;
    List<CoreLabel> tokens;
    String bestName;
    Pair<Integer, Integer> window;
    Pair<Integer, Integer> tokenIndexs;
    int sentenceNumber;

    public CustomEntityMention(List<CoreLabel> tokens, Pair<Integer, Integer> window, String bestName) {
        this.tokens = tokens;
        this.window = window;
        this.bestName = bestName;
        this.sentenceNumber = tokens.get(0).sentIndex()+1;
    }

    public CustomEntityMention(CoreEntityMention cem){
        this.tokens = new LinkedList<>();
        for (CoreLabel token : cem.tokens()){
            tokens.add(token);
        }
		sentenceNumber = cem.tokens().get(0).sentIndex()+1;
        window = new Pair<>();
        bestName = cem.text();
	}

    public CustomEntityMention(CustomEntityMention cem){
        this.tokens = new LinkedList<>(cem.getTokens());
		sentenceNumber = cem.getSentenceNumber();
        window = cem.window;
        bestName = cem.bestName;
        tokenIndexs = cem.tokenIndexs;
	}

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
        window = new Pair<>();
	}
	
	public CustomEntityMention(String text, String bestName, int sentenceNumber, int firstTokenNumber, int lastTokenNumber) {
        this.bestName = bestName;
		tokens = new LinkedList<>();
        CoreLabel temp = new CoreLabel();
        temp.setOriginalText(text);
        temp.setIndex(firstTokenNumber);
        temp.setSentIndex(sentenceNumber-1);
        tokens.add(temp);
		this.sentenceNumber = sentenceNumber;
        window = new Pair<>();
        tokenIndexs = new Pair<>(firstTokenNumber, lastTokenNumber);
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

    public CustomCorefChain getCorefChain(){
        return corefChain;
    }

    public void setCorefChain(CustomCorefChain ccc){
        corefChain = ccc;
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

    public Pair<Integer,Integer> tokenIndexs(){
        if (tokenIndexs == null) return new Pair<>(tokens.get(0).index(), tokens.get(tokens.size()-1).index());
        else return tokenIndexs;
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

    public boolean equalTo(CustomEntityMention ce){
		return this.sentenceNumber == ce.sentenceNumber && this.tokenIndexs().first() == ce.tokenIndexs().first() && this.tokenIndexs().second() == ce.tokenIndexs().second();
	}

    /**
     * used by CustomCorefChain.precision().
     *  
    */
    public double precision(CorefChainContainer reference, CustomCorefChain originChain) {
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
     *  
    */
    public double recall(CorefChainContainer reference, CustomCorefChain originChain) {
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
