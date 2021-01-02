package util;

import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.util.Pair;

public class EntityMention {

    CoreEntityMention coreEntityMention;
    String bestName;
    Pair<Integer, Integer> window;

    public EntityMention(CoreEntityMention coreEntityMention, Pair<Integer, Integer> window, String bestName) {
        this.coreEntityMention = coreEntityMention;
        this.window = window;
        this.bestName = bestName;
    }

    public EntityMention(CoreEntityMention coreEntityMention, String bestName) {
        this.coreEntityMention = coreEntityMention;
        this.bestName = bestName;
    }

    public CoreEntityMention getCoreEntityMention(){
        return coreEntityMention;
    }

    public void setCoreEntityMention(CoreEntityMention coreEntityMention){
        this.coreEntityMention = coreEntityMention;
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

    @Override
    public String toString() {
        return "{" +
            " coreEntityMention='" + getCoreEntityMention() + "'" +
            ", best name='" + getBestName() + "'" +
            ", window='" + getWindow() + "'" +
            "}";
    }

    
}
