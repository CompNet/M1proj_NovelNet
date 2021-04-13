package novelnet.util;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.util.Pair;

public class CustomEntityMention {

    CustomCorefChain corefChain;
    List<CoreLabel> tokens;
    String bestName;
    Pair<Integer, Integer> window;
    int sentenceNumber;

    public CustomEntityMention(List<CoreLabel> tokens, Pair<Integer, Integer> window, String bestName) {
        this.tokens = tokens;
        this.window = window;
        this.bestName = bestName;
    }


    public CustomEntityMention(List<CoreLabel> tokens, String bestName, CustomCorefChain customCorefChain) {
        this.tokens = tokens;
        this.bestName = bestName;
        this.corefChain = customCorefChain;
    }

    public CustomEntityMention(CoreEntityMention cem){
		this.tokens = cem.tokens();
		sentenceNumber = cem.tokens().get(0).sentIndex()+1;
        window = new Pair<>(cem.tokens().get(0).index(), cem.tokens().get(cem.tokens().size()-1).index());
	}
	
	public CustomEntityMention(String text, int sentenceNumber, int firstTokenNumber, int lastTokenNumber) {
		tokens = new LinkedList<>();
        CoreLabel temp = new CoreLabel();
        temp.setOriginalText(text);
        tokens.add(temp);
		this.sentenceNumber = sentenceNumber;
        window = new Pair<>(firstTokenNumber, lastTokenNumber);
	}

	public int getSentenceNumber() {
		return sentenceNumber;
	}

	public void setSentenceNumber(int sentenceNumber) {
		this.sentenceNumber = sentenceNumber;
	}

    public String getText() {
		String result = "";
        for (CoreLabel t : tokens){
            result += t.originalText()+" ";
        }
        return result;
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
        return tokens.get(0).sentIndex();
    }

    public Pair<Integer,Integer> charOffsets(){
        return new Pair<>(tokens.get(0).beginPosition(), tokens.get(tokens.size()-1).endPosition());
    }

    public Pair<Integer,Integer> tokenIndexs(){
        return new Pair<>(tokens.get(0).index(), tokens.get(tokens.size()-1).index());
    }

    public CoreLabel getBeginToken(){
        return tokens.get(0);
    }

    public CoreLabel getEndToken(){
        return tokens.get(tokens.size()-1);
    }

    public String text(){
        String text = "";
        for ( CoreLabel token : tokens){
            text += token.value() + " ";
        }
        return text;
    }

    public boolean compareTo(CustomEntityMention ce){
		return this.sentenceNumber == ce.sentenceNumber && this.getWindowBegining() == ce.getWindowBegining() && this.getWindowEnding() == ce.getWindowEnding();
	}

    /*public double precision(ComparableCorefChainContainer reference, ComparableCorefChain originChain) {
		double result = 0;
		for (ComparableCorefChain cccRef : reference.getCorefChains()){
			if (cccRef.getEntities().contains(this)){
				for (int i = 0; i < cccRef.getEntities().size();i++){
					if (originChain.getEntities().contains(cccRef.getEntities().get(i))){
						result++;
					}
					if (i == cccRef.getEntities().size()-1){
						result = result/originChain.getEntities().size();
					}
				}
			}
		}
		return result;
    }*/

    @Override
    public String toString() {
        return "{" +
            " coreEntityMention='" + text() + "'" +
            ", best name='" + getBestName() + "'" +
            ", window='" + getWindow() + "'" +
            "}";
    }
    

    
}
