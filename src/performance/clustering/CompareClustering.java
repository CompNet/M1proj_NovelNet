package performance.clustering;

import novelnet.util.CustomCorefChain;
import performance.coref.CompareCorefChain;

public class CompareClustering {

    /**
	 * the container with the reference clusters
	*/
    ClusterContainer reference;

    /**
	 * the container with the reference clusters
	*/
    ClusterContainer bestClusters;

    double precision;

    /*
     * dbScan distance used to find the best clusters
    */
    double dbScanDist;

    public CompareClustering() {
    }

    public CompareClustering(ClusterContainer importedData, ClusterContainer reference) {
        this.importedData = importedData;
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

    public static CompareClustering buildFromXML(String path){
        CompareClustering result = new CompareClustering();
        result.setReference(ClusterContainer.buildFromXML(path));
    }

    public double evaluate(double dbScanDist){

    }

}
