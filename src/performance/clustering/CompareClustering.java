package performance.clustering;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.Comparator;

import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.clustering.evaluation.AdjustedRandIndex;
import jsat.linear.Vec;
import novelnet.util.CustomCorefChain;

public class CompareClustering {

    /**
	 * the container with the reference clusters
	*/
    ClusterContainer reference;

    /**
	 * the container with the best clusters found by the engine
	*/
    ClusterContainer bestClusters;

    /*
     * precision for the best clusters
    */
    double precision;

    /*
     * dbScan distance used to find the best clusters
    */
    double dbScanDist;

    public CompareClustering() {
    }

    public CompareClustering(ClusterContainer reference) {
        this.reference = reference;
        this.bestClusters = null;
        this.precision = -2.0;
        this.dbScanDist = 0.0;
    }

    public ClusterContainer getReference() {
        return this.reference;
    }

    public void setReference(ClusterContainer reference) {
        this.reference = reference;
    }

    public double getPrecision() {
        return this.precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public ClusterContainer getBestClusters() {
        return this.bestClusters;
    }

    public void setBestClusters(ClusterContainer bestClusters) {
        this.bestClusters = bestClusters;
    }

    public double getDbScanDist() {
        return this.dbScanDist;
    }

    public void setDbScanDist(double dbScanDist) {
        this.dbScanDist = dbScanDist;
    }

    public static CompareClustering buildFromXML(String path) throws IOException{
        CompareClustering result = new CompareClustering();
        result.setReference(ClusterContainer.buildClusterContainerFromXML(path));

        return result;
    }

    public double evaluate(double dbScanDist){

        ClusterContainer clusteredData = reference.clusterization(dbScanDist);

        reference.getCorefChains().sort(Comparator.comparing(CustomCorefChain::getId));
        clusteredData.getCorefChains().sort(Comparator.comparing(CustomCorefChain::getId));

        //Creation of the dataset From clusteredData
        ClassificationDataSet cds = new ClassificationDataSet(1,
            new CategoricalData[0],
            new CategoricalData(clusteredData.getLastCluster()+1)   //max number of clusters
        );

        //adding the coref chains into the data set
        for(CustomCorefChain ccc : clusteredData.getCorefChains()){
            cds.addDataPoint(Vec.random(1), new int[0], ccc.getClusterID());
        }

        //putting the reference into an array
        int[] d = new int[reference.getCorefChains().size()];

        //adding the coref chains into the data set
        int i = 0;
        for(CustomCorefChain ccc : reference.getCorefChains()){
            d[i] = ccc.getClusterID();
            i++;
        }

        AdjustedRandIndex ari = new AdjustedRandIndex();
        double score = ari.evaluate(d, cds);
        //conver score to ARI score
        score = 1.0-score;

        if (score > precision){
            precision = score;
            this.dbScanDist = dbScanDist;
            bestClusters = clusteredData;
        }
        
        return score;
    }

    public void findBestCluster(double begin, double end, double increment){

        for(double i = begin; i <=end; i+= increment){
            evaluate(i);
        }

    }

    public void displayResult(){

        System.out.println("\nBest clusters found by the engine : ");
        bestClusters.displayByCluster();

        System.out.println("Precision : " + getPrecision());
        System.out.println("dbscan dsit : " + getDbScanDist() + "\n");

    }






    // TESTS

    private static void testJsat(){
        //using example from http://www.otlet-institute.org/wikics/Clustering_Problems.html
        ClassificationDataSet cds = new ClassificationDataSet(1, new CategoricalData[0], new CategoricalData(3));
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                cds.addDataPoint(Vec.random(1), new int[0], i);
        int[] d = new int[9];
        d[0] = d[1] = 0;
        d[2] = d[3] = d[4] = d[5] = 1;
        d[6] = d[7] = 2;
        d[8] = 3;
        
        AdjustedRandIndex ari = new AdjustedRandIndex();
        double score = ari.evaluate(d, cds);
        //conver tot ARI
        score = 1.0-score;
        System.out.println("should be around 0.46 : " + score);

        //test 2

        cds = new ClassificationDataSet(1, new CategoricalData[0], new CategoricalData(4));
        cds.addDataPoint(Vec.random(1), new int[0], 0);
        cds.addDataPoint(Vec.random(1), new int[0], 0);
        cds.addDataPoint(Vec.random(1), new int[0], 1);
        cds.addDataPoint(Vec.random(1), new int[0], 1);
        cds.addDataPoint(Vec.random(1), new int[0], 1);
        cds.addDataPoint(Vec.random(1), new int[0], 1);
        cds.addDataPoint(Vec.random(1), new int[0], 2);
        cds.addDataPoint(Vec.random(1), new int[0], 2);
        cds.addDataPoint(Vec.random(1), new int[0], 3);


        d = new int[9];
        d[0] = d[1] = 0;
        d[2] = d[3] = d[4] = d[5] = 1;
        d[6] = d[7] = 2;
        d[8] = 3;
        
        ari = new AdjustedRandIndex();
        score = ari.evaluate(d, cds);
        //conver tot ARI
        score = 1.0-score;
        System.out.println("should be 1 : " + score);
    }

    private static void testEvaluation(String path) throws IOException{
        ClusterContainer container = ClusterContainer.buildClusterContainerFromXML(path);
        CompareClustering compare = new CompareClustering(container);
        System.out.println(compare.evaluate(0.40));

        System.out.println("reference :");
        compare.getReference().displayByCluster();
        System.out.println("\nclusterddata :");
        compare.getBestClusters().displayByCluster();

    }

    private static void testBestClusters(String path) throws IOException{
        ClusterContainer container = ClusterContainer.buildClusterContainerFromXML(path);
        CompareClustering compare = new CompareClustering(container);
        compare.findBestCluster(0.20, 0.60, 0.05);

        System.out.println("reference :");
        compare.getReference().displayByCluster();

        compare.displayResult();
    }

    public static void main(String[] args) throws IOException {
        String language = "en";
        String fileName = "HarryPotter3_ShriekingShack";
        String path = "res/manualAnnotation/ner_coref_clustering/" + language + "/" + fileName + ".xml";

        //testJsat();

        //testEvaluation(path);

        testBestClusters(path);
    }

}
