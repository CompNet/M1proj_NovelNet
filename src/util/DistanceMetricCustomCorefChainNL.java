package util;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;

import info.debatty.java.stringsimilarity.*;

public class DistanceMetricCustomCorefChainNL implements DistanceMetric<CustomCorefChain> {

    @Override
    public double calculateDistance(CustomCorefChain arg0, CustomCorefChain arg1) throws DBSCANClusteringException {
        NormalizedLevenshtein nl = new NormalizedLevenshtein();
        //System.out.println("String 1 : " + arg0.getRepresentativeName() + "\t String 2 : " + arg1.getRepresentativeName() + "\t NL distance : " + nl.distance(arg0.getRepresentativeName(), arg1.getRepresentativeName()));
        return nl.distance(arg0.getRepresentativeName(), arg1.getRepresentativeName());
    }
    
}
