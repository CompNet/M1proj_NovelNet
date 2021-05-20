package performance.graph;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jdk.internal.module.SystemModuleFinders;
import novelnet.graph.*;
import novelnet.pipeline.GraphCreator;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;
import smile.math.distance.EuclideanDistance;

/**
 * Used to compare our graph result with the reference.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class CompareGraph {

    Graph graphToEvaluate;

    Graph reference;
    
    double distance;

    /**
     * Class Constructor
    */
    public CompareGraph() {
    }

    /**
     * Class Constructor specifying the graph and the reference graph
    */
    public CompareGraph(Graph graphToEvaluate, Graph reference) {
        this.graphToEvaluate = graphToEvaluate;
        this.reference = reference;
        distance = -1;
    }

    public Graph getGraphToEvaluate() {
        return this.graphToEvaluate;
    }

    public void setGraphToEvaluate(Graph graphToEvaluate) {
        this.graphToEvaluate = graphToEvaluate;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Graph getReference() {
        return this.reference;
    }

    public void setReference(Graph reference) {
        this.reference = reference;
    }

    public void display(){
        System.out.println("\nChains to evaluate : ");
        System.out.println(graphToEvaluate);
        System.out.println("\nreference : ");
        System.out.println(reference);
        System.out.println("\ndistance : " + distance);
    }

    /**
     * verifying if all the nodes are presents in each graphs if not adding the node to the graph it's missing.
     * 
     * @return the preprocessed graphs (index 0 = reference, index 1 graph to evaluate)
    */
    public List<Graph> preProcessing(){
        List<Graph> result = new LinkedList<>();

        Graph tempRef = new Graph(reference);
        Graph tempEval = new Graph(graphToEvaluate);
        result.add(tempRef);
        result.add(tempEval);

        tempEval.addAllNodeFrom(tempRef);
        tempRef.addAllNodeFrom(tempEval);

        return result;
    }

    /**
     * Computing the euclidian distance between the two graphs.
     * 
     * @return the euclidian distance.
    */
    public double euclidianDistance(){

        List<Graph> tmpGraphs = preProcessing();

        EuclideanDistance ed = new EuclideanDistance();
        distance = ed.d(tmpGraphs.get(0).adjacencyVector(), tmpGraphs.get(1).adjacencyVector());
        if(Double.isInfinite(distance)){
            System.out.println("Problème dans le calcul de distance euclidienne. La distance entre les deux graphes est infinie.");
            return 0;
        }
        distance = distance/tmpGraphs.get(0).getTotalWeight();

        return ImpUtils.round(distance, 2);
    }



    //Tests

    public static void testSmileEuclidiantDistance(){

        // example from http://mathonline.wikidot.com/the-distance-between-two-vectors
        
        double[] u = new double[4];
        u[0] = 2;
        u[1] = 3;
        u[2] = 4;
        u[3] = 2;

        double[] v = new double[4];
        v[0] = 1;
        v[1] = -2;
        v[2] = 1;
        v[3] = 3;

        EuclideanDistance ed = new EuclideanDistance();

        System.out.println("Should be 6 : " + ed.d(u, v));
        
    }

    public static void testImport(String languageAndFileName, double dbScanDist, int sentNumber, int covering) throws IOException, NullDocumentException{
        System.out.println("\nBuilt from txt :\n" + GraphCreator.buildCoOcSentFromTxt("res/corpus/"+ languageAndFileName + ".txt", dbScanDist, sentNumber, covering));
        System.out.println("Built from xml :\n" + GraphCreator.buildCoOcSentFromXml("res/manualAnnotation/ner_coref_clustering/"+ languageAndFileName + ".xml", "res/corpus/"+ languageAndFileName + ".txt", sentNumber, covering));
    }

    public static void testOnRealData(String languageAndFileName, double dbScanDist, int sentNumber, int covering) throws IOException, NullDocumentException{
        Graph ref = GraphCreator.buildCoOcSentFromTxt("res/corpus/"+ languageAndFileName + ".txt", dbScanDist, sentNumber, covering);
        Graph eval = GraphCreator.buildCoOcSentFromXml("res/manualAnnotation/ner_coref_clustering/" + languageAndFileName + ".xml", "res/corpus/" + languageAndFileName +".txt", sentNumber, covering);
        CompareGraph ccc = new CompareGraph(eval, ref);
        ccc.euclidianDistance();
        ccc.display();

    }

    public static void testPreprocessing(){

        Graph testRef = new Graph();
        testRef.setName("testRef");
        Graph testEval = new Graph();
        testEval.setName("testEval");

        CompareGraph test = new CompareGraph(testEval, testRef);

        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("D");

        testRef.addNode(a);
        testRef.addNode(c);
        testRef.addNode(d);

        testEval.addNode(a);
        testEval.addNode(b);
        testEval.addNode(e);

        System.out.println(testRef);
        System.out.println(testEval);

        for (Graph g : test.preProcessing()) {
            System.out.println(g);
        }
    }

    public static void main(String[] args) throws IOException, NullDocumentException{
        double dbScanDist = 0.5;
        int sentNumber = 10;
        int covering = 1;

        String languageAndFileName = "en/TheLightningThief_chapter1";
        //testSmileEuclidiantDistance();
        //testImport(dbScanDist, sentNumber, covering);
        testOnRealData(languageAndFileName, dbScanDist, sentNumber, covering);
        //testPreprocessing();
    }
    
}
