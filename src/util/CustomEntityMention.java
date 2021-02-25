package util;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.util.Pair;

public class CustomEntityMention {

    CustomCorefChain corefChain;
    CoreEntityMention coreEntityMention;
    List<CoreLabel> tokens;
    String bestName;
    Pair<Integer, Integer> window;

    public CustomEntityMention(CoreEntityMention coreEntityMention, Pair<Integer, Integer> window, String bestName) {
        this.coreEntityMention = coreEntityMention;
        this.window = window;
        this.bestName = bestName;
    }

    /*public CustomEntityMention(CoreEntityMention coreEntityMention, String bestName) {
        this.coreEntityMention = coreEntityMention;
        this.bestName = bestName;
    }*/

    public CustomEntityMention(CoreEntityMention coreEntityMention, String bestName, CustomCorefChain customCorefChain) {
        this.coreEntityMention = coreEntityMention;
        this.bestName = bestName;
        this.corefChain = customCorefChain;
    }

    public List<CoreLabel> getTokens() {
        return tokens;
    }

    public void setTokens(List<CoreLabel> tokens){
        this.tokens = tokens;
    }

    public CoreEntityMention getCoreEntityMention(){
        return coreEntityMention;
    }

    public void setCoreEntityMention(CoreEntityMention coreEntityMention){
        this.coreEntityMention = coreEntityMention;
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
        if (coreEntityMention == null){
            return tokens.get(0).sentIndex();
        }
        return coreEntityMention.tokens().get(0).sentIndex();
    }

    public Pair<Integer,Integer> charOffsets(){
        if (coreEntityMention == null){
            return new Pair<>(tokens.get(0).beginPosition(), tokens.get(tokens.size()-1).endPosition());
        }
        return coreEntityMention.charOffsets();
    }

    public Pair<Integer,Integer> tokenIndexs(){
        if (coreEntityMention == null){
            return new Pair<>(tokens.get(0).index(), tokens.get(tokens.size()-1).index());
        }
        return new Pair<>(coreEntityMention.tokens().get(0).index(), coreEntityMention.tokens().get(tokens.size()-1).index());
    }

    public CoreLabel getBeginToken(){
        if (coreEntityMention == null){
            return tokens.get(0);
        }
        return coreEntityMention.tokens().get(0);
    }

    public CoreLabel getEndToken(){
        if (coreEntityMention == null){
            return tokens.get(tokens.size()-1);
        }
        return coreEntityMention.tokens().get(coreEntityMention.tokens().size()-1);
    }

    public String text(){
        String text = "";
        if (coreEntityMention == null){
            for ( CoreLabel token : tokens){
                text += token.value() + " ";
            }
        }
        else text = coreEntityMention.text();
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
