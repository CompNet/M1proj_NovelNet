package performance.clustering;

import java.io.IOException;
import java.util.Comparator;

import smile.validation.metric.AdjustedRandIndex;

import novelnet.util.CustomCorefChain;

/**
 * Used to compare our clustering result with the reference.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class CompareClustering {

    /**
	 * the container with the reference clusters
	*/
    ClusterContainer reference;

    /**
	 * the container with the best clusters found by the clustering engine
	*/
    ClusterContainer bestClusters;

    /**
     * precision for the best clusters should be between -1.0 and 1.0 (0 = random, 1.0 = perfect, -1.0 worse than random)
    */
    double precision;

    /**
     * dbScan distance used to find the best clusters
    */
    double dbScanDist;

    /**
     * empty class constructor
    */
    public CompareClustering() {
        this.bestClusters = null;
        this.precision = -2.0;
        this.dbScanDist = 0.0;
    }

    /**
     * class constructor specifying the reference data
     * 
     * @param reference the ClusterContainer with the reference data
    */
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

    /**
     * CompareClustering builder from .xml file
     * 
     * @param path the path to the xml file containing the reference data
     * @return the ComparaClusturing object
    */
    public static CompareClustering buildFromXML(String path) throws IOException{
        CompareClustering result = new CompareClustering();
        result.setReference(ClusterContainer.buildClusterContainerFromXML(path));

        return result;
    }

    /**
     *  evaluation function :
     *      cluster with the specified distance,
     *      transform data into arrays,
     *      compare the arrays
     * 
     * <br><br> result will be stocked into bestClusters
     * 
     * @param dbScanDist the distance with wich you want to run the dbscan algorithm
     * @return the precision for the dbscan clusturing algorithm with the specified distance
    */
    public double evaluate(double dbScanDist){
        ClusterContainer clusteredData = reference.clusterization(dbScanDist);

        reference.getCorefChains().sort(Comparator.comparing(CustomCorefChain::getId)); //making sure reference and data are in the same order
        clusteredData.getCorefChains().sort(Comparator.comparing(CustomCorefChain::getId));

        //putting the data into an array
        int[] data = new int[clusteredData.getCorefChains().size()];

        //adding the coref chains into the data
        int i = 0;
        for(CustomCorefChain ccc : clusteredData.getCorefChains()){
            data[i] = ccc.getClusterID();
            i++;
        }

        //putting the reference into an array
        int[] ref = new int[reference.getCorefChains().size()];

        //adding the coref chains into the reference
        i = 0;
        for(CustomCorefChain ccc : reference.getCorefChains()){
            ref[i] = ccc.getClusterID();
            i++;
        }

        AdjustedRandIndex ari = new AdjustedRandIndex();
        
        //there is a bug in dbscan : a node can be in 2 clusters.
        //We didn't know how to fix it so when there is this bug we make sure the score is very low and not counted.
        double score = -3.0;
        if (ref.length == data.length){
            score = ari.score(ref, data);    //order of arrays shouldn't matter
        }

        if (score > precision){
            precision = score;
            this.dbScanDist = dbScanDist;
            bestClusters = clusteredData;
        }
        
        return score;
    }

    /**
     * simple function to find the best clustering for dbscan algorithm, the result will be stocked into bestClusters.
     * 
     * @param begin first distance tried (0.2 is the recomended value)
     * @param end loop will not go higher than this distance (this should be higher than the begin argument, 0.61 is the recomended value)
     * @param increment increment for the loop (0.05 is the recomended value, reduce to be more precise, augment to be faster)
    */
    public void findBestCluster(double begin, double end, double increment){

        for(double i = begin; i <=end; i+= increment){
            evaluate(i);
        }

    }

    public void displayResult(){

        System.out.println("\nBest clusters found by the engine : ");
        bestClusters.displayByCluster();

        System.out.println("Precision : " + getPrecision());
        System.out.println("dbscan distance : " + getDbScanDist() + "\n");

    }






    // TESTS

    
    public static void main(String[] args) throws IOException {
        String language = "en";
        String fileName = "HarryPotter3_ShriekingShack";
        String path = "res/manualAnnotation/ner_coref_clustering/" + language + "/" + fileName + ".xml";

        //testSmile();

        //testEvaluation(path);

        testBestClusters(path);
    }

    private static void testSmile(){
        AdjustedRandIndex ari = new AdjustedRandIndex();
        int[] ref = new int[9];
        int[] data = new int[9];

        //test1
        ref[0] = ref[1] = 0;
        ref[2] = ref[3] = ref[4] = ref[5] = 1;
        ref[6] = ref[7] = 2;
        ref[8] = 3;

        data[0] = data[1] = 0;
        data[2] = data[3] = data[4] = data[5] = 1;
        data[6] = data[7] = 2;
        data[8] = 3;

        System.out.println("should be 1 : " + ari.score(ref, data));

        //test2
        ref[0] = ref[1] = 0;
        ref[2] = ref[3] = ref[4] = ref[5] = 1;
        ref[6] = ref[7] = 2;
        ref[8] = 3;

        data[0] = data[1] = 0;
        data[2] = data[3] = data[4] = data[5] = 2;  //interchanging cluster number 1 and 2 but should still be 1
        data[6] = data[7] = 1;
        data[8] = 3;

        System.out.println("should still be 1 : " + ari.score(ref, data));


        //test3 is from http://www.otlet-institute.org/wikics/Clustering_Problems.html
        ref[0] = ref[1] = 0;
        ref[2] = ref[3] = ref[4] = ref[5] = 1;
        ref[6] = ref[7] = 2;
        ref[8] = 3;

        data[0] = data[1] = data[2] = 0;
        data[3] = data[4] = data[5] = 1;
        data[6] = data[7] = data[8] = 2;

        System.out.println("should be around 0.46 : " + ari.score(ref, data));        
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
        compare.findBestCluster(0.20, 0.61, 0.05);

        System.out.println("reference :");
        compare.getReference().displayByCluster();

        compare.displayResult();
    }
}
