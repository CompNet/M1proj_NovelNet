package performance.graph;

import java.io.IOException;

import novelnet.graph.*;
import novelnet.util.NullDocumentException;

public class CompareGraph {

    Graph graphToEvaluate;

    Graph reference;
    
    double precision;

    double recall;

    double fMeasure;

    public CompareGraph() {
    }

    public CompareGraph(String evaluationFilePath, String referenceFilePath) throws IOException, NullDocumentException{
		if(evaluationFilePath.substring(evaluationFilePath.length()-4, evaluationFilePath.length()).equals(".txt")){
			graphToEvaluate = Graph.buildFromTxt(evaluationFilePath);
		}
		else if (evaluationFilePath.substring(evaluationFilePath.length()-4, evaluationFilePath.length()).equals(".xml")){
			graphToEvaluate = Graph.buildFromXml(evaluationFilePath);
		}
		else System.out.println("File type not recognized for argument 1");
		if(referenceFilePath.substring(referenceFilePath.length()-4, referenceFilePath.length()).equals(".txt")){
			reference = Graph.buildFromTxt(referenceFilePath);
		}
		else if (referenceFilePath.substring(referenceFilePath.length()-4, referenceFilePath.length()).equals(".xml")){
			reference = Graph.buildFromXml(referenceFilePath);
		}
		else System.out.println("File type not recognized for argument 2");
	}

    public CompareGraph(Graph graphToEvaluate, Graph reference) {
        this.graphToEvaluate = graphToEvaluate;
        this.reference = reference;
        precision = 0;
        recall = 0;
        fMeasure = 0;
    }

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

    public Graph getgraphToEvaluate() {
        return this.graphToEvaluate;
    }

    public void setgraphToEvaluate(Graph graphToEvaluate) {
        this.graphToEvaluate = graphToEvaluate;
    }

    public Graph getReference() {
        return this.reference;
    }

    public void setReference(Graph reference) {
        this.reference = reference;
    }
    
    /*public void precisionB3(){        

        Graph tempgraphToEvaluate = new Graph(graphToEvaluate);
        Graph tempReference = new Graph(reference);

        for(ComparableCorefChain ccc : tempReference.getCorefChains()){
            for(ComparableEntity ce : ccc.getEntities()){
                if (!tempgraphToEvaluate.contains(ce)){
                    tempgraphToEvaluate.addEntityAsNewChain(ce);
                }
            }
        }

        for(ComparableCorefChain ccc : tempgraphToEvaluate.getCorefChains()){
            for(ComparableEntity ce : ccc.getEntities()){
                if (!tempReference.contains(ce)){
                    tempReference.addEntityAsNewChain(ce);
                }
            }
        }

        precision = tempgraphToEvaluate.precision(tempReference);
    }

    public void recallB3(){

        Graph tempgraphToEvaluate = new Graph(graphToEvaluate);
        Graph tempReference = new Graph(reference);
        ComparableCorefChain keyChain;
        int keySize = tempgraphToEvaluate.getCorefChains().size();
        int chainSize;
        ComparableEntity ce;

        for(int i = 0; i < keySize; i++){
            keyChain = tempgraphToEvaluate.getCorefChains().get(i);
            chainSize = keyChain.getEntities().size();
            for (int j = 0; j < chainSize; j++){
                ce = keyChain.getEntities().get(j);
                if (!tempReference.contains(ce)){
                    if (keyChain.getEntities().size()==1){
                        tempgraphToEvaluate.getCorefChains().remove(keyChain);
                        i--;
                        keySize--;
                    }
                    else {
                        keyChain.getEntities().remove(ce);
                        j--;
                        chainSize--;
                    }
                }
            }
        }

        for(ComparableCorefChain ccc : tempReference.getCorefChains()){
            for (ComparableEntity cE : ccc.getEntities()){
                if(!tempgraphToEvaluate.contains(cE)){
                    tempgraphToEvaluate.addEntityAsNewChain(cE);
                }
            }
        }
        recall = tempReference.precision(tempgraphToEvaluate);
    }*/

    public void fMeasure(){
        if (recall == 0 && precision == 0) fMeasure = 0;
        else fMeasure = 2*((precision*recall)/(precision+recall));
    }

    public void compare(){
        /*precisionB3();
        recallB3();*/
        fMeasure();
        displayMeasures();
    }

    @Override
    public String toString() {
        return "{" +
            " graphToEvaluate='" + getgraphToEvaluate() + "'" +
            ", reference='" + getReference() + "'" +
            ", precision='" + getPrecision() + "'" +
            ", recall='" + getRecall() + "'" +
            ", fMeasure='" + getFMeasure() + "'" +
            "}";
    }

    public void display(){
        System.out.println("\nChains to evaluate : ");
        System.out.println(graphToEvaluate);
        System.out.println("\nreference : ");
        System.out.println(reference);
        displayMeasures();
        
    }

    public void displayMeasures(){
        System.out.println("\nprecision : " + precision);
        System.out.println("recall : " + recall);
        System.out.println("fMeasure : " + fMeasure);
    }

    public static void testImport() throws IOException, NullDocumentException{
        System.out.println("Built from xml :\n" + Graph.buildFromXml("performance/ner/Joe_Smith.xml"));
        System.out.println("\nBuilt from txt :\n" + Graph.buildFromTxt("res/corpus/Joe_Smith.txt"));
    }

    /*public void testPreTreating(){
        graphToEvaluate = new Graph();
        reference = new Graph();

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

        graphToEvaluate.getCorefChains().add(b);
        graphToEvaluate.getCorefChains().add(c);

        reference.getCorefChains().add(a);

        precisionB3();

        recallB3();

        fMeasure();

        display();

    }*/

    public static void testOnRealData() throws IOException, NullDocumentException{
        
        CompareGraph ccc = new CompareGraph("res/corpus/TheLightningThief_chapter1.txt", "performance/ner/TheLightningThief_chapter1.xml");
        ccc.compare();
        ccc.display();

    }

    public static void main(String[] args) throws IOException, NullDocumentException{

        /*CompareCorefChain ccc = new CompareCorefChain();
        ccc.testPreTreating();*/
        //testImport();
        testOnRealData();
    }
    
}
