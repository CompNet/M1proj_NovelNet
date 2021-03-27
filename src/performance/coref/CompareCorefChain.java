package performance.coref;

import edu.stanford.nlp.trees.tregex.gui.DisplayMatchesPanel;
import edu.washington.cs.knowitall.logic.Expression.Paren.R;
import performance.ner.ComparableEntity;

public class CompareCorefChain {

    CorefChainContainer chainsToEvaluate;

    CorefChainContainer reference;
    
    double precision;

    double recall;

    double fMeasure;

    public double getPrecision() {
        return this.precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return this.recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getFMeasure() {
        return this.fMeasure;
    }

    public void setFMeasure(double fMeasure) {
        this.fMeasure = fMeasure;
    }

    public CompareCorefChain() {
    }


    public CompareCorefChain(CorefChainContainer chainsToEvaluate, CorefChainContainer reference) {
        this.chainsToEvaluate = chainsToEvaluate;
        this.reference = reference;
        precision = 0;
        recall = 0;
        fMeasure = 0;
    }

    public CorefChainContainer getChainsToEvaluate() {
        return this.chainsToEvaluate;
    }

    public void setChainsToEvaluate(CorefChainContainer chainsToEvaluate) {
        this.chainsToEvaluate = chainsToEvaluate;
    }

    public CorefChainContainer getReference() {
        return this.reference;
    }

    public void setReference(CorefChainContainer reference) {
        this.reference = reference;
    }
    
    public void precisionB3(){        

        CorefChainContainer tempChainsToEvaluate = new CorefChainContainer(chainsToEvaluate);
        CorefChainContainer tempReference = new CorefChainContainer(reference);

        for(ComparableCorefChain ccc : tempReference.getCorefChains()){
            for(ComparableEntity ce : ccc.getEntities()){
                if (!tempChainsToEvaluate.contains(ce)){
                    tempChainsToEvaluate.addEntityAsNewChain(ce);
                }
            }
        }

        for(ComparableCorefChain ccc : tempChainsToEvaluate.getCorefChains()){
            for(ComparableEntity ce : ccc.getEntities()){
                if (!tempReference.contains(ce)){
                    tempReference.addEntityAsNewChain(ce);
                }
            }
        }

        precision = tempChainsToEvaluate.precision(tempReference);
    }

    public void recallB3(){

        CorefChainContainer tempChainsToEvaluate = new CorefChainContainer(chainsToEvaluate);
        CorefChainContainer tempReference = new CorefChainContainer(reference);

        for(ComparableCorefChain ccc : tempChainsToEvaluate.getCorefChains()){
            int size = ccc.getEntities().size();
            for (int i = 0; i < size; i++){
                if (!tempReference.contains(ccc.getEntities().get(i))){
                    tempChainsToEvaluate.delete(ccc.getEntities().get(i));
                    i--;
                    size--;
                }
            }
        }

        for(ComparableCorefChain ccc : tempReference.getCorefChains()){
            for (ComparableEntity ce : ccc.getEntities()){
                if(!tempChainsToEvaluate.contains(ce)){
                    tempChainsToEvaluate.addEntityAsNewChain(ce);
                }
            }
        }
        recall = tempReference.precision(tempChainsToEvaluate);
    }

    public void fMeasure(){
        if (recall == 0 && precision == 0) fMeasure = 0;
        else fMeasure = 2*((precision*recall)/(precision+recall));
    }

    public void testPreTreating(){
        chainsToEvaluate = new CorefChainContainer();
        reference = new CorefChainContainer();

        ComparableEntity A = new ComparableEntity("A", 0, 2, 2);
        ComparableEntity B = new ComparableEntity("B", 1, 3, 4);
        ComparableEntity C = new ComparableEntity("C", 2, 1, 3);
        ComparableEntity D = new ComparableEntity("D", 4, 5, 5);
        ComparableEntity I = new ComparableEntity("I", 6, 6, 6);
        ComparableEntity J = new ComparableEntity("J", 7, 1, 2);

        ComparableCorefChain a = new ComparableCorefChain();
        a.getEntities().add(A);
        a.getEntities().add(B);
        a.getEntities().add(C);

        ComparableCorefChain b = new ComparableCorefChain();
        b.getEntities().add(A);
        b.getEntities().add(B);
        b.getEntities().add(D);

        ComparableCorefChain c = new ComparableCorefChain();
        c.getEntities().add(I);
        c.getEntities().add(J);

        chainsToEvaluate.getCorefChains().add(b);
        chainsToEvaluate.getCorefChains().add(c);

        reference.getCorefChains().add(a);

        precisionB3();

        recallB3();

        fMeasure();

        display();

    }

    @Override
    public String toString() {
        return "{" +
            " chainsToEvaluate='" + getChainsToEvaluate() + "'" +
            ", reference='" + getReference() + "'" +
            ", precision='" + getPrecision() + "'" +
            ", recall='" + getRecall() + "'" +
            ", fMeasure='" + getFMeasure() + "'" +
            "}";
    }

    public void display(){
        System.out.println("\nChains to evaluate : ");
        chainsToEvaluate.display();
        System.out.println("\nreference : ");
        reference.display();
        displayMeasures();
        
    }

    public void displayMeasures(){
        System.out.println("\nprecision : " + precision);
        System.out.println("recall : " + recall);
        System.out.println("fMeasure : " + fMeasure);
    }

    public static void main(String[] args){

        CompareCorefChain ccc = new CompareCorefChain();
        ccc.testPreTreating();
    }
    
}
