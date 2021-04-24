package performance.coref;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;
import java.io.IOException;

import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import novelnet.util.CustomCorefChain;
import novelnet.util.CustomEntityMention;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;

public class CorefChainContainer{

    /**
	 * the list of corefChains in the container
	*/
    List<CustomCorefChain> corefChains;

    /**
	 * Class Constructor with an empty list of corefChains
	*/
    public CorefChainContainer() {
        corefChains = new LinkedList<>();
    }

    /**
	 * Creates a CorefChainContainer from a stanford CoreDocument
	 *
     * @param stanfordCoreDocument the document to build the Container from.
	*/
    public CorefChainContainer(CoreDocument stanfordCoreDocument) throws NullDocumentException{
        corefChains = new LinkedList<>();

        //create a CustomCorefChain for each Stanford CorefChain
        for(CorefChain cc : stanfordCoreDocument.corefChains().values()){
            corefChains.add(new CustomCorefChain(cc));
        }

        ImpUtils.setDocument(stanfordCoreDocument);

        //create a CustomCorefChain for each CoreEntityMention without CorefChain in Stanford's CoreDocument
        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            corefChains.add(new CustomCorefChain(cem));
        }
    }
    
    public List<CustomCorefChain> getCorefChains() {
        return this.corefChains;
    }

    public void setCorefChains(Collection<CustomCorefChain> collection) {
        this.corefChains = new LinkedList<>(collection);
    }

    /**
	 * Build a CorefChainContainer from an .xml file
	 * 
	 * @param pathToXml path to the .xml file
     * @return the Container
	 */
    public static CorefChainContainer buildFromXml(String pathToXml) throws IOException{
        CorefChainContainer result = new CorefChainContainer();
        Map <Integer, CustomCorefChain> temp = new HashMap<>();
		SAXBuilder builder = new SAXBuilder();  //there may be an error but it compile
		FileInputStream is = new FileInputStream(pathToXml);     
	    try {
	    	Document document = (Document) builder.build(is);
	        Element rootNode = document.getRootElement();
	        List<Element> list = rootNode.getChildren("mention");   //getting all the mentions

            //for each mention (in the .xml)
	        for (int i = 0; i < list.size(); i++) {
	        	Element node = (Element) list.get(i);
                
                CustomCorefChain ccc = temp.get(Integer.parseInt(node.getChildText("CorefChain")));   //trying to find the corefChain for the current mention
                // if there is a chain for this mention
                if ( ccc != null){
                    // create the mention and add it to the chain
                    ccc.getCEMList().add(new CustomEntityMention(node.getChildText("text"), 
                        Integer.parseInt(node.getChildText("sentence")), 
                        Integer.parseInt(node.getChildText("start")),
                        Integer.parseInt(node.getChildText("end"))));
                }
                else{
                    //else we create the chain with the mention
                    ccc = new CustomCorefChain(
                        new CustomEntityMention(node.getChildText("text"), 
                            Integer.parseInt(node.getChildText("sentence")), 
                            Integer.parseInt(node.getChildText("start")),
                            Integer.parseInt(node.getChildText("end")))
                    );
                    ccc.setClusterID(Integer.parseInt(node.getChildText("ClusterID")));
                    ccc.setId(Integer.parseInt(node.getChildText("CorefChain")));
                    ccc.setRepresentativeName(node.getChildText("originalName"));
                    
                    temp.putIfAbsent( Integer.parseInt(node.getChildText("CorefChain")), ccc);
                }
	        }
	    } catch (IOException io) {
	    	System.out.println(io.getMessage());
	    } catch (JDOMException jdomex) {
	    	System.out.println(jdomex.getMessage());
	    }
        result.setCorefChains(temp.values());

        result.getCorefChains().sort(Comparator.comparing(CustomCorefChain::getId));
        return result;
	}

    /**
	 * Count the entities in the container
	 * 
     * @return number of entitites
	 */
    public int getNumberOfEntities(){
        int tot = 0;
        for (CustomCorefChain ccc : corefChains){
            tot += ccc.getCEMList().size();
        }
        return tot;
    }

    /**
	 * Calculate the precision of the container compared to the ground truth (or reference) by calculating the average of coref chain precision
     * 
     * @param reference
	 * @return the precision
	 */
    public double precision(CorefChainContainer reference){
        double tot = 0;
        for (CustomCorefChain ccc : corefChains){
            tot += ccc.corefPrecision(reference);    // total of precision of Entities in a corefChain in the container
        }
        return tot/getNumberOfEntities();   //averaging
    }

    /**
	 * Calculate the recall of the container compared to the ground truth (or reference) by calculating the average of coref chain recall
     * 
     * @param reference
	 * @return the recall
	 */
    public double recall(CorefChainContainer reference) {
        double tot = 0;
        for (CustomCorefChain ccc : corefChains){
            tot += ccc.corefRecall(reference);   //total of recall of Entities in a corefChain in the container
        }
        return tot/reference.getNumberOfEntities();     //averaging
    }

    /**
	 * Check if the Mention is in the container
     * 
     * @param ce the mention we try to find
	 * @return true if it's present, false if not.
	 */
    public boolean contains(CustomEntityMention ce){
        for (CustomCorefChain ccc : corefChains){
            if (ccc.getCEMList().contains(ce)) return true;
        }
        return false;
    }

    /**
	 * Add a mention as a new CorefChain in the container
     * 
     * @param ce the mention to add
	 */
    public void addEntityAsNewChain(CustomEntityMention ce) {
        corefChains.add(new CustomCorefChain(ce));
    }
    
    /**
	 * deletes a mention from the container, if the mention was alone in the chain delete the chain.
     * 
     * @param ce the mention to delete
	 */
    public void delete(CustomEntityMention ce) {
        int size = corefChains.size();
        CustomCorefChain ccc;
        for (int i = 0; i < size; i++){
            ccc = corefChains.get(i);
            if (ccc.getCEMList().contains(ce)){
                if (ccc.getCEMList().size()==1){
                    corefChains.remove(ccc);
                    i--;
                    size--;
                }
                else ccc.getCEMList().remove(ce);
            }
        }
    }

    public CustomCorefChain get(int id){
        for (CustomCorefChain ccc : corefChains){
            if (ccc.getId() == id) return ccc;
        }
        return null;
    }

    @Override
    public String toString() {
        return "{ corefChains='" + getCorefChains() + "' }";
    }
    
    public void display(){
        for (CustomCorefChain ccc : corefChains){
            boolean begin = true;
            System.out.print("{  Id : " + ccc.getId() + ",\tCluster : " + ccc.getClusterID() + ",\tname : " + ccc.getRepresentativeName() + " Entities : [ ");
            for (CustomEntityMention ce : ccc.getCEMList()){
                if (begin) {
                    System.out.print(ce.text());
                    begin = false;
                }
                else System.out.print(", " + ce.text());
            }
            System.out.print(" ] }\n");
        }
    }

    public CorefChainContainer manualClone() {
        CorefChainContainer o = new CorefChainContainer();
        CustomCorefChain tempccc;

        for(CustomCorefChain ccc : getCorefChains()){
            tempccc = new CustomCorefChain();
            tempccc.setClusterID(ccc.getClusterID());
            tempccc.setId(ccc.getId());
            tempccc.setRepresentativeName(ccc.getRepresentativeName());
            o.corefChains.add(tempccc);
            for (CustomEntityMention cem : ccc.getCEMList()){
                tempccc.getCEMList().add(new CustomEntityMention(cem));
            }
        }

        // on renvoie le clone
        return o;
    }

    private static void testImport(String path) throws IOException{
        CorefChainContainer test = CorefChainContainer.buildFromXml(path);
        test.display();
    }

    public static void main(String[] args) throws IOException {
        String language = "en";
        String fileName = "HarryPotter3_ShriekingShack";
        String path = "res/manualAnnotation/ner_coref_clustering/" + language + "/" + fileName + ".xml";

        testImport(path);
    }
}
