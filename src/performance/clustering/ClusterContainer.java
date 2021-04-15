package performance.clustering;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.input.SAXBuilder;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import novelnet.pipeline.CorefChainFuser;
import novelnet.util.CustomCorefChain;
import performance.coref.CorefChainContainer;

public class ClusterContainer {

    List<CorefChainContainer> corefChainsContainer;

    public ClusterContainer() {
        corefChainsContainer = new LinkedList<>();
    }

    public ClusterContainer(List<CorefChainContainer> corefChainsContainer) {
        this.corefChainsContainer = corefChainsContainer;
    }

    public List<CorefChainContainer> getCorefChainsContainer() {
        return this.corefChainsContainer;
    }

    public void setCorefChainsContainer(List<CorefChainContainer> corefChainsContainer) {
        this.corefChainsContainer = corefChainsContainer;
    }    

    public CorefChainContainer get(int id){
        for (CorefChainContainer ccc : corefChainsContainer){
            if (ccc.getClusterID() == id) return ccc;
        }
        return null;
    }

    /**
	 * Build the ClusterContainer containing the evaluation from an .xml file
	 * 
	 * @param pathToXml path to the .xml file
     * @return the Container
	 */
    public static ClusterContainer buildEvaluationFromXml(String pathToXml, Double dbScanDist) throws IOException{
        CorefChainContainer tempChainContainer = new CorefChainContainer();
        CustomCorefChain tempCorefChain;
        LinkedList<CustomCorefChain> allChains = new LinkedList<>();
		SAXBuilder builder = new SAXBuilder();  //there should be an error but it compile
		FileInputStream is = new FileInputStream(pathToXml);     
	    try {
	    	Document document = builder.build(is);
	        Element rootNode = document.getRootElement();
	        List<Element> list = rootNode.getChildren("mention");   //getting all the mentions

            //for each mention (in the .xml)
	        for (int i = 0; i < list.size(); i++) {
	        	Element node = list.get(i);
                tempCorefChain = tempChainContainer.get(Integer.parseInt(node.getChildText("CorefChain")));   //trying to find the corefChain for the current mention
                // if the chain is not in the container
                if (tempCorefChain == null){
                    //we create the chain with the representative name and the id
                    tempCorefChain = new CustomCorefChain();
                    tempCorefChain.setId(Integer.parseInt(node.getChildText("CorefChain")));
                    tempCorefChain.setRepresentativeName(node.getChildText("originalName"));
                    tempChainContainer.getCorefChains().add(tempCorefChain);
                    allChains.add(tempCorefChain);
                }
	        }
	    } catch (IOException io) {
	    	System.out.println(io.getMessage());
	    } catch (JDOMException jdomex) {
	    	System.out.println(jdomex.getMessage());
	    }

        CorefChainFuser ccf = new CorefChainFuser();
        //execute and return the clustering (before the chain fusion form the clusterer)
        ClusterContainer result = new ClusterContainer(ccf.corefChainsClusteringROBeforeFusion(tempChainContainer.getCorefChains(), 2, dbScanDist));
        for (CustomCorefChain chain : allChains){
            boolean found = false;
            for(CorefChainContainer container : result.getCorefChainsContainer()){
                if (container.get(chain.getId()) != null) found = true;
            }
            if (!found){
                CorefChainContainer temp = new CorefChainContainer();
                temp.getCorefChains().add(chain);
                result.getCorefChainsContainer().add(temp);
            }
        }
        //sorting corefChains
        for (CorefChainContainer chainContainer : result.getCorefChainsContainer()){
            chainContainer.getCorefChains().sort(Comparator.comparing(CustomCorefChain::getId));
        }
        return result;
	}

    /**
	 * Build the ClusterContainer containing the reference from an .xml file
	 * 
	 * @param pathToXml path to the .xml file
     * @return the Container
	 */
    public static ClusterContainer buildReferenceFromXml(String pathToXml) throws IOException{
        ClusterContainer result = new ClusterContainer();
        CorefChainContainer tempChainContainer = new CorefChainContainer();
        CustomCorefChain tempCorefChain;
		SAXBuilder builder = new SAXBuilder();  //there should be an error but it compile
		FileInputStream is = new FileInputStream(pathToXml);     
	    try {
	    	Document document = builder.build(is);
	        Element rootNode = document.getRootElement();
	        List<Element> list = rootNode.getChildren("mention");   //getting all the mentions

            //for each mention (in the .xml)
	        for (int i = 0; i < list.size(); i++) {
	        	Element node = list.get(i);
                tempChainContainer = result.get(Integer.parseInt(node.getChildText("ClusterID")));   //trying to find the CorefChainContainer for the current mention's cluster
                // if there is no container for the cluster
                if (tempChainContainer == null){
                    //we create the chain with an id
                    tempCorefChain = new CustomCorefChain();
                    tempCorefChain.setId(Integer.parseInt(node.getChildText("CorefChain")));

                    //we create the container with the chain
                    tempChainContainer = new CorefChainContainer();
                    tempChainContainer.setClusterID(Integer.parseInt(node.getChildText("ClusterID")));
                    tempChainContainer.getCorefChains().add(tempCorefChain);

                    //we add the container to the result
                    result.getCorefChainsContainer().add(tempChainContainer);
                }
                //if there is a container for the cluster
                else{
                    tempCorefChain = tempChainContainer.get(Integer.parseInt(node.getChildText("CorefChain")));     //we try to find the chain container for the current mention's chain
                    //if there is no chain for the mention we create it and add it to the chain container
                    if (tempCorefChain == null){
                        //we create the chain with the representative name and the id
                        tempCorefChain = new CustomCorefChain();
                        tempCorefChain.setId(Integer.parseInt(node.getChildText("CorefChain")));
                        //we add the chain to the chain container
                        tempChainContainer.getCorefChains().add(tempCorefChain);
                    }
                    
                }
	        }
	    } catch (IOException io) {
	    	System.out.println(io.getMessage());
	    } catch (JDOMException jdomex) {
	    	System.out.println(jdomex.getMessage());
	    }
        
        //sorting corefChains
        for (CorefChainContainer chainContainer : result.getCorefChainsContainer()){
            chainContainer.getCorefChains().sort(Comparator.comparing(CustomCorefChain::getId));
        }
        
        return result;
    }

    public void display() {
        System.out.println("Coref Chains : ");
        for (int i = 0; i < corefChainsContainer.size(); i++){
            System.out.println(corefChainsContainer.get(i));
        }
    }

    public static void testEvaluationImport() throws IOException{
        ClusterContainer cc = ClusterContainer.buildEvaluationFromXml("manualAnnotation/ner_coref_clustering/en/HarryPotter3_TrainBoarding.xml", 0.5);
        System.out.println("\nClustering with 0.5 distance :\n");
        cc.display();

        cc = ClusterContainer.buildEvaluationFromXml("manualAnnotation/ner_coref_clustering/en/HarryPotter3_TrainBoarding.xml", 0.45);
        System.out.println("\nClustering with 0.45 distance :\n");
        cc.display();

        cc = ClusterContainer.buildEvaluationFromXml("manualAnnotation/ner_coref_clustering/en/HarryPotter3_TrainBoarding.xml", 0.4);
        System.out.println("\nClustering with 0.4 distance :\n");
        cc.display();
    }

    public static void testReferenceImport() throws IOException{
        ClusterContainer cc = ClusterContainer.buildReferenceFromXml("manualAnnotation/ner_coref_clustering/en/HarryPotter3_TrainBoarding.xml");
        cc.display();
    }

    public static void main(String[] args) throws IOException {
        testReferenceImport();
        testEvaluationImport();
    }
}
