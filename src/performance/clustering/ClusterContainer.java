package performance.clustering;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import novelnet.pipeline.CorefChainFuser;
import novelnet.util.CustomCorefChain;
import novelnet.util.CustomEntityMention;
import novelnet.util.ImpUtils;
import novelnet.util.TextNormalization;
import performance.coref.CorefChainContainer;

/**
 * represent a cluster of corefChains. it is mostly like a CorefChainsContainer but with some added functions
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class ClusterContainer extends CorefChainContainer {

    /**
     * Class Constructor
    */
    public ClusterContainer() {
        super();
    }

    /**
     * Copy a cluster container
    */
    public ClusterContainer(CorefChainContainer container){
        super();
        for (CustomCorefChain ccc : container.getCorefChains()){
            getCorefChains().add(ccc);
        }
    }

    /**
     * get the number of the current last cluster.
     * 
     * @return the number of the current last cluster.
    */
    public int getLastCluster() {
        int max = 0;
        for (CustomCorefChain ccc : getCorefChains()){
            if (ccc.getClusterID() > max) max = ccc.getClusterID();
        }
        return max;
    }

    /**
	 * Build the ClusterContainer from an .xml file
	 * 
	 * @param pathToXml path to the .xml file
     * @return the Container
	 */
    public static ClusterContainer buildClusterContainerFromXML(String pathToXml) throws IOException{
        return new ClusterContainer(CorefChainContainer.buildFromXml(pathToXml));       
	}

    /**
	 * Build the ClusterContainer from an .xml file (used to create a Graph)
	 * 
	 * @param pathToXml path to the .xml file
     * @return the Container
	 */
    public static ClusterContainer buildClusterContainerFromXML(String pathToXml, CoreDocument document) throws IOException{
        return new ClusterContainer(CorefChainContainer.buildFromXml(pathToXml, document));       
	}

    public ClusterContainer clusterization(double dbScanDist){
        CorefChainFuser ccf = new CorefChainFuser();
        //execute and return the clustering (before the chain fusion form the clusterer)
        ClusterContainer tmp = new ClusterContainer(manualClone());
        ClusterContainer result = new ClusterContainer();   /* our temporary cluster container to have the result but keep the original
                                                            in the current clusterContainer.    */

        List<CorefChainContainer> clusters = ccf.corefChainsClusteringROBeforeFusion(tmp.getCorefChains(), dbScanDist);
        
        //for each cluster of chains in clusters
        for(int i = 0; i < clusters.size(); i++){
            //for each chain in the cluster
            for (CustomCorefChain ccc : clusters.get(i).getCorefChains()){
                ccc.setClusterID(i+1);  // we set the cluster ID (1 -> n    IE  i+1 -> n)
            }
            //regroup all the chain in the same cluster container.
            result.getCorefChains().addAll(clusters.get(i).getCorefChains());
        }

        //adding singleton chains to the result (dscan algorithm doesn't return chains alone in a cluster)
        for (CustomCorefChain ccc : getCorefChains()){
            if (result.get(ccc.getId())==null) {
                CustomCorefChain tempCCC = (CustomCorefChain) ccc.clone();
                tempCCC.setClusterID(result.getLastCluster()+1);
                result.getCorefChains().add(tempCCC);
            }
        }

        //sorting by corefChain id
        result.getCorefChains().sort(Comparator.comparing(CustomCorefChain::getId));

        return result;
    }

    @Override
    public void display(){
        for (CustomCorefChain ccc : getCorefChains()){
            System.out.println("{ Id : " + ccc.getId() + ",\tCluster : " + ccc.getClusterID() + ",\tname : " + ccc.getRepresentativeName() + " }");
        }
    }

    public void displayByCluster(){
        getCorefChains().sort(Comparator.comparing(CustomCorefChain::getClusterID).thenComparing(Comparator.comparing(CustomCorefChain::getId)));
        for (CustomCorefChain ccc : getCorefChains()){
            System.out.println("{ Cluster : " + ccc.getClusterID() + ",\t Id : " + ccc.getId() + ",\tname : " + ccc.getRepresentativeName() + " }");
        }
        getCorefChains().sort(Comparator.comparing(CustomCorefChain::getId));
    }




    // TESTS

    private static void testImport(String path) throws IOException{
        ClusterContainer cc = ClusterContainer.buildClusterContainerFromXML(path);
        cc.display();
        System.out.println();
        cc.displayByCluster();
    }

    private static void testClusterization(String path) throws IOException {
        ClusterContainer cc = ClusterContainer.buildClusterContainerFromXML(path);
        System.out.println("\nref before cluster :");
        cc.display();
        System.out.println();

        System.out.println("\nclusterred Data :");
        cc.clusterization(0.45).display();

        System.out.println("\nref after cluster :");
        cc.display();
    }

    private static void testImportForGraph(String pathToXml, String pathToText) throws IOException{
        //Creating the document
		FileInputStream is = new FileInputStream(pathToText);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		content = TextNormalization.addDotEndOfLine(content);

		String prop="tokenize,ssplit";

		Properties props = new Properties();
		props.setProperty("annotators",prop);
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		ImpUtils.setDocument(document);
		pipeline.annotate(document);

        ClusterContainer test = buildClusterContainerFromXML(pathToXml, document);

        for (CustomCorefChain ccc : test.getCorefChains()) {
            System.out.println("\nnew chain : ");
            for (CustomEntityMention cem : ccc.getCEMList()) {
                System.out.println(cem.getBestName() + ", " + cem.originalText() + ", " + cem.getSentenceIndex());
                for (CoreLabel token : cem.getTokens()){
                    System.out.println("\ttoken : " + token.originalText()+ ", " + token.sentIndex() + ", " + token.index());
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {

        String language = "en";
        String fileName = "HarryPotter3_TrainBoarding";
        String path = "res/manualAnnotation/ner_coref_clustering/" + language + "/" + fileName + ".xml";
        String pathToText = "res/corpus/" + language + "/" + fileName + ".txt";

        //testImport(path);
        //testClusterization(path);
        testImportForGraph(path, pathToText);
    }   
}
