package novelnet.util;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;

public class CustomEntityMention {

    CustomCorefChain corefChain;
    List<CoreLabel> tokens;
    String bestName;
    Pair<Integer, Integer> window;

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

    @Override
    public String toString() {
        return "{" +
            " coreEntityMention='" + text() + "'" +
            ", best name='" + getBestName() + "'" +
            ", window='" + getWindow() + "'" +
            "}";
    }
    

    
}
