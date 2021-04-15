package performance.clustering;

import novelnet.util.CustomCorefChain;
import performance.coref.CompareCorefChain;

public class CompareClustering {

    /**
	 * the container with the clusters to evaluate
	*/
    ClusterContainer clustersToEvaluate;

    /**
	 * the container with the reference clusters
	*/
    ClusterContainer reference;

    double precision;

    public CompareClustering() {
    }

    public CompareClustering(ClusterContainer clustersToEvaluate, ClusterContainer reference) {
        this.clustersToEvaluate = clustersToEvaluate;
        this.reference = reference;
        this.precision = 0;
    }

    public ClusterContainer getClustersToEvaluate() {
        return this.clustersToEvaluate;
    }

    public void setClustersToEvaluate(ClusterContainer clustersToEvaluate) {
        this.clustersToEvaluate = clustersToEvaluate;
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

}
