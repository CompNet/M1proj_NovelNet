package performance.clustering;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import novelnet.pipeline.CorefChainFuser;
import novelnet.util.CustomCorefChain;

import performance.coref.CorefChainContainer;

public class ClusterContainer extends CorefChainContainer {

    public ClusterContainer() {
        super();
    }

    public ClusterContainer(CorefChainContainer container){
        super();
        for (CustomCorefChain ccc : container.getCorefChains()){
            getCorefChains().add(ccc);
        }
    }

    public int getLastCluster() {
        int max = 0;
        for (CustomCorefChain ccc : getCorefChains()){
            if (ccc.getClusterID() > max) max = ccc.getClusterID();
        }
        return max;
    }

    /**
	 * Build the ClusterContainer containing the evaluation from an .xml file
	 * 
	 * @param pathToXml path to the .xml file
     * @return the Container
	 */
    public static ClusterContainer buildClusterContainerFromXML(String pathToXml) throws IOException{
        return new ClusterContainer(CorefChainContainer.buildFromXml(pathToXml));       
	}

    public ClusterContainer clusterization(double dbScanDist){
        CorefChainFuser ccf = new CorefChainFuser();
        //execute and return the clustering (before the chain fusion form the clusterer)
        ClusterContainer tmp = new ClusterContainer(manualClone());
        ClusterContainer result = new ClusterContainer();

        List<CorefChainContainer> temp = ccf.corefChainsClusteringROBeforeFusion(tmp.getCorefChains(), 2, dbScanDist);

        for(int i = 0; i < temp.size(); i++){
            for (CustomCorefChain ccc : temp.get(i).getCorefChains()){
                ccc.setClusterID(i+1);
            }
            result.getCorefChains().addAll(temp.get(i).getCorefChains());
        }

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

    public static void main(String[] args) throws IOException {

        String language = "en";
        String fileName = "HarryPotter3_ShriekingShack";
        String path = "res/manualAnnotation/ner_coref_clustering/" + language + "/" + fileName + ".xml";

        //testImport(path);
        testClusterization(path);
    }   
}