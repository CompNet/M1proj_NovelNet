package novelnet.pipeline;

import info.debatty.java.stringsimilarity.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.christopherfrantz.dbscan.DBSCANClusterer;
import org.christopherfrantz.dbscan.DBSCANClusteringException;

import novelnet.util.CustomCorefChain;
import novelnet.util.CustomEntityMention;
import novelnet.util.DistanceMetricCustomCorefChainRO;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;
import performance.coref.CorefChainContainer;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * @author Baptiste Quay, Lemaire Tewis
*/
public class CorefChainFuser {

    /**
	 * Class constructor
	*/
    public CorefChainFuser() {

    }

    /**
	 * fuse the corefChains given as an argument into one big corefChain.
     * 
     * @param cccList a List of corefChains to fuse.
     * @return a big corefChain containing all the mentions in all the corefChains given.
	*/
    public CustomCorefChain customCorefChainFusion(List<CustomCorefChain> cccList) {
        //if there is only one coref chain no need to fuse.
        if (cccList.size() == 1)
            return cccList.get(0);

        CustomCorefChain result = new CustomCorefChain();

        //regrouping all the EntityMentions in the result Chain
        for (CustomCorefChain ccc : cccList) {
            for (CustomEntityMention cem : ccc.getCEMList()) {
                result.getCEMList().add(cem);
            }
        }

        String bestName1;
        String bestName2;
        String bestNameMin = "";

        double tot;
        double min = Double.MAX_VALUE;

        RatcliffObershelp ro = new RatcliffObershelp();

        //finding the best representative name
        for (CustomCorefChain ccc1 : cccList) {
            //for each representative name we compare it to all the others.
            bestName1 = ccc1.getBestName();
            tot = 0;
            for (CustomCorefChain ccc2 : cccList) {
                if (!ccc1.equals(ccc2)) {
                    bestName2 = ccc2.getBestName();
                    tot += ro.distance(bestName1, bestName2);   //adding the current distance to the total distance
                }
            }

            if (tot < min) {
                bestNameMin = bestName1;
                min = tot;
            } else if (tot == min && bestName1.length() > bestNameMin.length()) {   //if the total is equal to the min we take the longest String.
                bestNameMin = bestName1;
            }
        }
        result.setBestName(bestNameMin);
        return result;
    }

    /**
	 * clusterize and fuse the chains given in argument.
     * 
     * @param cccList a List of corefChains to fuse.
     * @param dbScanDist the distance used for the dbscan algorithm
     * 
     * @return a list of the fused corefChains.
	*/
    public List<CustomCorefChain> corefChainsClusteringRO(List<CustomCorefChain> cccList, double dbScanDist) {
        DBSCANClusterer<CustomCorefChain> clusterer = null; //object used to clusterize

        List<CustomCorefChain> result = new LinkedList<>(); //result list

        List<ArrayList<CustomCorefChain>> clusterList = null;   //each sub list of tmp is a cluster of corefChains

        try {
            clusterer = new DBSCANClusterer<>(cccList, 2, dbScanDist, new DistanceMetricCustomCorefChainRO());
            clusterList = clusterer.performClustering();    //getting the clusters.
        } catch (DBSCANClusteringException e1) {
            System.out.println(e1.getMessage());
        }

        for (List<CustomCorefChain> cluster : clusterList){
            result.add(customCorefChainFusion(cluster));  //fusing each cluster and adding it to the result
            for (CustomCorefChain ccc : cluster){ //removing all the coref chains fused from the original list
                if(cccList.contains(ccc)){
                    cccList.remove(ccc);
                }
            }
        }
        //all the corefChains still in cccList are chains that haven't been clusterized (IE are standalone chains)
        //and we want to keep them
        for (CustomCorefChain ccc : cccList){
            result.add(ccc);
        }

        return result;
    }

    /**
	 * clusterize the chains given in argument.
     * 
     * @param cccList a List of corefChains to fuse.
     * @param dbScanDist the distance used for the dbscan algorithm
     * 
     * @return a list of the clusters.
	*/
    public List<CorefChainContainer> corefChainsClusteringROBeforeFusion(List<CustomCorefChain> cccList, double dbScanDist) {
        DBSCANClusterer<CustomCorefChain> clusterer = null; //object used to clusterize

        List<CorefChainContainer> result = new LinkedList<>(); //result list
        List<ArrayList<CustomCorefChain>> clusterList = null;   //each sub list of tmp is a cluster of corefChains

        try {
            clusterer = new DBSCANClusterer<>(cccList, 2, dbScanDist, new DistanceMetricCustomCorefChainRO());
            clusterList = clusterer.performClustering();    //getting the clusters.
        } catch (DBSCANClusteringException e1) {
            System.out.println(e1.getMessage());
        }

        for (Collection<CustomCorefChain> cluster : clusterList){
            CorefChainContainer temp = new CorefChainContainer();
            temp.setCorefChains(cluster);   //putting the corefChains from the same cluster in a corefChainContainer.
            result.add(temp);   
            for (CustomCorefChain ccc : cluster){ //removing all the coref chains fused from the original list
                if(cccList.contains(ccc)){
                    cccList.remove(ccc);
                }
            }
        }

        //all the corefChains still in cccList are chains that haven't been clusterized (IE are standalone chains)
        //and we want to keep them
        for (CustomCorefChain ccc : cccList){
            CorefChainContainer temp = new CorefChainContainer();
            temp.getCorefChains().add(ccc);
            result.add(temp);
        }

        return result;
    }

    /**
	 * fuse the coref chains given in argument by their clusterID
     * 
     * @param the list of coref chains to fuse
     * @return a list of the fused corefChains.
	*/
    public List<CustomCorefChain> corefChainsFusionByClusterID(List<CustomCorefChain> cccList){
        List<CustomCorefChain> result = new LinkedList<>();
        Boolean found;
        for (CustomCorefChain ccc : cccList){   //for each chain in the list
            found = false;
            for (CustomCorefChain cccRes : result) {    
                //if it has the same clusterID than a chain in the result list
                if (ccc.getClusterID() == cccRes.getClusterID()){
                    cccRes.getCEMList().addAll(ccc.getCEMList());//adding all the entities from the first chain to the result chain.
                    found = true;
                }
            }
            if (!found){
                //if the cluster was not found adding this chain to the result.
                result.add(ccc);
            }
        }
        for (CustomCorefChain customCorefChain : result) {
            customCorefChain.setId(customCorefChain.getClusterID());
        }
        return result;
    }








    //Tests

    public static void testStringDistance() {

        System.out.println("------------------ Levenshtein ------------------");

        Levenshtein l = new Levenshtein();

        System.out.println(l.distance("Lupin", "Remus Lupin"));
        System.out.println(l.distance("Harry", "Ron"));
        System.out.println(l.distance("Ron Weasley", "Ronald Weasley"));

        System.out.println("------------------ Normalised Levenshtein ------------------");

        NormalizedLevenshtein ln = new NormalizedLevenshtein();

        System.out.println(ln.distance("Lupin", "Remus Lupin"));
        System.out.println(ln.distance("Harry", "Ron"));
        System.out.println(ln.distance("Ron Weasley", "Ronald Weasley"));


        System.out.println("------------------ RatcliffObershelp ------------------");

        RatcliffObershelp ro = new RatcliffObershelp();
        
        System.out.println(ro.distance("Shelock Holmes", "Holmes"));
        System.out.println(ro.distance("Remus Lupin", "Lupin"));
        System.out.println(ro.distance("Professor Lupin", "Lupin"));
        System.out.println(ro.distance("Lupin", "Professor Lupin"));
        System.out.println(ro.distance("Harry", "Ron"));
        System.out.println(ro.distance("Ron Weasley", "Ronald Weasley"));
        System.out.println(ro.distance("Ronald Weasley", "Ron Weasley"));

    }

    public static void testCustomCorefChain() throws IOException, NullDocumentException {
        String path = "res/tests/multipleTokenCorefMention.txt";

        FileInputStream is = new FileInputStream(path);
        String content = IOUtils.toString(is, StandardCharsets.UTF_8);

        String prop="tokenize,ssplit,pos,lemma,ner,parse,coref";

        Properties props = new Properties();
        props.setProperty("annotators",prop);
        props.setProperty("ner.applyFineGrained", "false");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(content);
        ImpUtils.setDocument(document);
        pipeline.annotate(document);

        List<CustomCorefChain> cccList = new LinkedList<>();

        System.out.println("---");
        System.out.println("coref chains");
        for (CorefChain cc : document.corefChains().values()) {
            cccList.add(new CustomCorefChain(cc));
            System.out.println("\t" + cc);
        }

        System.out.println("----- EntityMention -----");

        for(CoreEntityMention cem : document.entityMentions()){
            System.out.println(cem + " pos : " + cem.charOffsets());
        }


        System.out.println("----- CustomCorefChain -----");

        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            System.out.println(cem);
            cccList.add(new CustomCorefChain(cem));
        }

        for(CustomCorefChain ccc : cccList){
            System.out.println(ccc);
        }

        CorefChainFuser ccf = new CorefChainFuser();

        List<CustomCorefChain> finalList = new LinkedList<>();
        finalList.add(cccList.get(1));
        cccList.remove(1);
        finalList.add(ccf.customCorefChainFusion(cccList));

        for(CustomCorefChain ccc : finalList){
            System.out.println(ccc);
        }


    }

    public static void testClustering() throws IOException, NullDocumentException {
        String path = "res/tests/clusteringTestSample.txt";

        FileInputStream is = new FileInputStream(path);
        String content = IOUtils.toString(is, StandardCharsets.UTF_8);

        String prop="tokenize,ssplit,pos,lemma,ner,parse,coref";

        Properties props = new Properties();
        props.setProperty("annotators",prop);
        props.setProperty("ner.applyFineGrained", "false");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(content);
        ImpUtils.setDocument(document);
        pipeline.annotate(document);

        List<CustomCorefChain> cccList = new LinkedList<>();

        System.out.println("\n----- coref chains -----\n");
        for (CorefChain cc : document.corefChains().values()) {
            System.out.println("\t" + cc);
        }

        System.out.println("\n----- EntityMention -----\n");

        for(CoreEntityMention cem : document.entityMentions()){
            System.out.println(cem + " pos : " + cem.charOffsets());
        }

        System.out.println("\n----- CustomCorefChain -----\n");

        for(CorefChain cc : document.corefChains().values()){
            cccList.add(new CustomCorefChain(cc));
        }

        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            cccList.add(new CustomCorefChain(cem));
        }

        for(CustomCorefChain ccc : cccList){
            System.out.println(ccc);
        }

        ///////////////   Clustering          //////////////////////

        CorefChainFuser ccf = new CorefChainFuser();

        System.out.println("\n----- Clustering RO -----\n");

        List<CustomCorefChain> testRO = ccf.corefChainsClusteringRO(cccList, 0.4);

        for (CustomCorefChain cccResultList : testRO){
            System.out.println(cccResultList);
        }
        
    }

    public static void testClusteringOnRealData() throws IOException, NullDocumentException {
        String path = "res/corpus/Hp2.txt";

        FileInputStream is = new FileInputStream(path);
        String content = IOUtils.toString(is, StandardCharsets.UTF_8);

        String prop="tokenize,ssplit,pos,lemma,ner,parse,coref";

        Properties props = new Properties();
        props.setProperty("annotators",prop);
        props.setProperty("ner.applyFineGrained", "false");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(content);
        ImpUtils.setDocument(document);
        pipeline.annotate(document);

        List<CustomCorefChain> cccList = new LinkedList<>();

        System.out.println("\n----- CustomCorefChain -----\n");

        for(CorefChain cc : document.corefChains().values()){
            cccList.add(new CustomCorefChain(cc));
        }

        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            cccList.add(new CustomCorefChain(cem));
        }

        for(CustomCorefChain ccc : cccList){
            System.out.println(ccc);
        }

        ///////////////   Clustering          //////////////////////

        CorefChainFuser ccf = new CorefChainFuser();
     

        System.out.println("\n----- Clustering RO -----\n");

        List<CustomCorefChain> testRO = ccf.corefChainsClusteringRO(cccList, 0.45);

        for (CustomCorefChain cccResultList : testRO){
            System.out.println(cccResultList);
        }
        
    }

    public static void main(String[] args) throws IOException, NullDocumentException {
        //testStringDistance();
        //testCustomCorefChain();
        testClustering();
        //testClusteringOnRealData();
    }
}
