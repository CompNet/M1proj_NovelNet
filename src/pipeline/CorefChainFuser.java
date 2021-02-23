package pipeline;

import info.debatty.java.stringsimilarity.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.christopherfrantz.dbscan.DBSCANClusterer;
import org.christopherfrantz.dbscan.DBSCANClusteringException;

import util.CustomCorefChain;
import util.DistanceMetricCustomCorefChainNL;
import util.DistanceMetricCustomCorefChainRO;
import util.ImpUtils;
import util.NullDocumentException;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ie.machinereading.structure.EntityMention;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class CorefChainFuser {

    private CorefChainFuser() {

    }

    public CustomCorefChain customCorefChainFusion(List<CustomCorefChain> cccList) {
        if (cccList.size() == 1)
            return cccList.get(0);
        CustomCorefChain result = new CustomCorefChain();
        for (CustomCorefChain ccc : cccList) {
            for (CoreEntityMention cem : ccc.getCEMList()) {
                result.getCEMList().add(cem);
            }
        }

        String bestName1;
        String bestName2;
        String bestNameMin = "";

        int tot;
        int min = Integer.MAX_VALUE;

        RatcliffObershelp ro = new RatcliffObershelp();

        for (CustomCorefChain ccc1 : cccList) {
            bestName1 = ccc1.getRepresentativeName();
            tot = 0;
            for (CustomCorefChain ccc2 : cccList) {
                if (!ccc1.equals(ccc2)) {
                    bestName2 = ccc2.getRepresentativeName();
                    tot += ro.distance(bestName1, bestName2);
                }
            }

            if (tot < min) {
                bestNameMin = bestName1;
                min = tot;
            } else if (tot == min && bestName1.length() > bestNameMin.length()) {
                bestNameMin = bestName1;
            }
        }
        result.setRepresentativeName(bestNameMin);
        return result;
    }

    public List<CustomCorefChain> corefChainsClusteringNL(List<CustomCorefChain> cccList, int numberMinCluster, double maxDistance) {
        DBSCANClusterer<CustomCorefChain> clusterer = null;

        List<CustomCorefChain> result = new LinkedList<>();
        List<ArrayList<CustomCorefChain>> tmp = null;

        try {
            clusterer = new DBSCANClusterer<>(cccList, numberMinCluster, maxDistance, new DistanceMetricCustomCorefChainNL());
            tmp = clusterer.performClustering();
        } catch (DBSCANClusteringException e1) {
            System.out.println(e1.getMessage());
        }

        for (ArrayList<CustomCorefChain> cccListToFuse : tmp){
            result.add(customCorefChainFusion(cccListToFuse));
            for (CustomCorefChain ccc : cccListToFuse){
                if(cccList.contains(ccc)){
                    cccList.remove(ccc);
                }
            }
        }
        for (CustomCorefChain ccc : cccList){
            result.add(ccc);
        }

        return result;
    }

    public List<CustomCorefChain> corefChainsClusteringRO(List<CustomCorefChain> cccList, int numberMinCluster, double maxDistance) {
        DBSCANClusterer<CustomCorefChain> clusterer = null;

        List<CustomCorefChain> result = new LinkedList<>();
        List<ArrayList<CustomCorefChain>> tmp = null;

        try {
            clusterer = new DBSCANClusterer<>(cccList, numberMinCluster, maxDistance, new DistanceMetricCustomCorefChainRO());
            tmp = clusterer.performClustering();
        } catch (DBSCANClusteringException e1) {
            System.out.println(e1.getMessage());
        }

        for (ArrayList<CustomCorefChain> cccListToFuse : tmp){
            result.add(customCorefChainFusion(cccListToFuse));
            for (CustomCorefChain ccc : cccListToFuse){
                if(cccList.contains(ccc)){
                    cccList.remove(ccc);
                }
            }
        }
        for (CustomCorefChain ccc : cccList){
            result.add(ccc);
        }

        return result;
    }

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
        /*System.out.println(ro.distance("Remus Lupin", "Lupin"));
        System.out.println(ro.distance("Professor Lupin", "Lupin"));
        System.out.println(ro.distance("Lupin", "Professor Lupin"));
        System.out.println(ro.distance("Harry", "Ron"));
        System.out.println(ro.distance("Ron Weasley", "Ronald Weasley"));
        System.out.println(ro.distance("Ronald Weasley", "Ron Weasley"));*/

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

        System.out.println("\n----- Clustering NL -----\n");

        List<CustomCorefChain> cccList2 = new LinkedList<>();
        for (CustomCorefChain ccc : cccList){
            cccList2.add(ccc);
        }

        List<CustomCorefChain> testNL = ccf.corefChainsClusteringNL(cccList, 2, 0.6);

        for (CustomCorefChain cccResultList : testNL){
            System.out.println(cccResultList);
        }
        

        System.out.println("\n----- Clustering RO -----\n");

        List<CustomCorefChain> testRO = ccf.corefChainsClusteringRO(cccList2, 2, 0.4);

        for (CustomCorefChain cccResultList : testRO){
            System.out.println(cccResultList);
        }
        
    }

    public static void main(String[] args) throws IOException, NullDocumentException {
        //testStringDistance();
        //testCustomCorefChain();
        testClustering();
    }
}
