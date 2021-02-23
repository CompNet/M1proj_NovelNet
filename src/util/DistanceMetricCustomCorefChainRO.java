package util;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;

import info.debatty.java.stringsimilarity.*;

public class DistanceMetricCustomCorefChainRO implements DistanceMetric<CustomCorefChain> {

    @Override
    public double calculateDistance(CustomCorefChain arg0, CustomCorefChain arg1) throws DBSCANClusteringException {
        RatcliffObershelp ro = new RatcliffObershelp();
        //System.out.println("String 1 : " + arg0.getRepresentativeName() + "\t String 2 : " + arg1.getRepresentativeName() + "\t RO distance : " + ro.distance(arg0.getRepresentativeName(), arg1.getRepresentativeName()));
        return ro.distance(arg0.getRepresentativeName(), arg1.getRepresentativeName());
    }
    
}
