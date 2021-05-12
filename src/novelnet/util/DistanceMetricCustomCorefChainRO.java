package novelnet.util;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;

import info.debatty.java.stringsimilarity.*;

/**
 * Implementation of christopherfrantz's DistanceMetric to calculate the distance between two corefChains, using the
 * Ratcliff Obershelp Algorith to calculate the distance between the best name of the two corefChains.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class DistanceMetricCustomCorefChainRO implements DistanceMetric<CustomCorefChain> {

    /**
     * @param arg0 the first corefChain
     * @param arg1 the second corefChain
     * @return the distance between the best name of the corefChains
    */
    @Override
    public double calculateDistance(CustomCorefChain arg0, CustomCorefChain arg1) throws DBSCANClusteringException {
        RatcliffObershelp ro = new RatcliffObershelp();
        return ro.distance(arg0.getRepresentativeName().toLowerCase(), arg1.getRepresentativeName().toLowerCase());
    }
    
}
