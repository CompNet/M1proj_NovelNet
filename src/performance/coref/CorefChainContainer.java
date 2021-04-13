package performance.coref;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import novelnet.util.CustomCorefChain;
import novelnet.util.CustomEntityMention;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;

public class CorefChainContainer {

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

    /**
	 * Clone a CorefChainContainer
     * 
     * @param corefChainContainer the CorefChainContainer to clone
	*/
    public CorefChainContainer(CorefChainContainer corefChainContainer) {
        CustomCorefChain tmpccc;
        corefChains = new LinkedList<>();
        for (CustomCorefChain ccc : corefChainContainer.getCorefChains()){
            tmpccc = new CustomCorefChain();
            for (CustomEntityMention ce : ccc.getCEMList()){
                tmpccc.getCEMList().add(ce);
            }
            corefChains.add(tmpccc);
        }
    }
    
    public List<CustomCorefChain> getCorefChains() {
        return this.corefChains;
    }

    public void setCorefChains(List<CustomCorefChain> corefChains) {
        this.corefChains = corefChains;
    }

    /**
	 * Build a CorefChainContainer from a .txt file using Stanford CoreNLP
	 * 
	 * @param pathToText path to the .txt file
     * @return the Container
	 */
    public static CorefChainContainer buildFromTxt(String pathToText) throws IOException, NullDocumentException {
        //transforming the file to a String
		FileInputStream is = new FileInputStream(pathToText);     
		String content = IOUtils.toString(is, "UTF-8");

        //building Stanford's pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);

        return new CorefChainContainer(document);
    }

    /**
	 * Build a CorefChainContainer from an .xml file
	 * 
	 * @param pathToXml path to the .xml file
     * @return the Container
	 */
    public static CorefChainContainer buildFromXml(String pathToXml) throws IOException{
        CorefChainContainer result = new CorefChainContainer();
		SAXBuilder builder = new SAXBuilder();  //there should be an error but it compile
		FileInputStream is = new FileInputStream(pathToXml);     
	    try {
	    	Document document = (Document) builder.build(is);
	        Element rootNode = document.getRootElement();
	        List<Element> list = rootNode.getChildren("mention");   //getting all the mentions

            //for each mention (in the .xml)
	        for (int i = 0; i < list.size(); i++) {
	        	Element node = (Element) list.get(i);
                
                CustomCorefChain ccc = result.get(Integer.parseInt(node.getChildText("CorefChain")));   //trying to find the corefChain for the current mention

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
                    result.addEntityAsNewChain(new CustomEntityMention(node.getChildText("text"), 
                        Integer.parseInt(node.getChildText("sentence")), 
                        Integer.parseInt(node.getChildText("start")),
                        Integer.parseInt(node.getChildText("end"))),
                        Integer.parseInt(node.getChildText("CorefChain")) );
                }
	        }
	    } catch (IOException io) {
	    	System.out.println(io.getMessage());
	    } catch (JDOMException jdomex) {
	    	System.out.println(jdomex.getMessage());
	    }
        return result;
	}

    /**
	 * find the coref chain corresponding to the id
	 * 
	 * @param chainID id of the chain
     * @return the corresponding chain (null if there is no corresponding chain)
	 */
    private CustomCorefChain get(int chainID) {
        if (chainID == 0) return null;
        for(CustomCorefChain ccc : corefChains){
            if (ccc.getId()== chainID){
                return ccc;
            }
        }
        return null;
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
            tot += ccc.precision(reference);    // total of precision of Entities in a corefChain in the container
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
            tot += ccc.recall(reference);   //total of recall of Entities in a corefChain in the container
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
	 * Add a mention as a new CorefChain in the container specifying the id of the chain
     * 
     * @param ce the mention to add
     * @param id the id of the chain
	 */
    public void addEntityAsNewChain(CustomEntityMention ce, int id){
        CustomCorefChain temp = new CustomCorefChain(ce);
        temp.setId(id);
        corefChains.add(temp);
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

    @Override
    public String toString() {
        return "{" +
            " corefChains='" + getCorefChains() + "'" +
            "}";
    }
    
    public void display(){
        for (CustomCorefChain ccc : corefChains){
            boolean begin = true;
            System.out.print("{ ");
            for (CustomEntityMention ce : ccc.getCEMList()){
                if (begin) {
                    System.out.print(ce.text());
                    begin = false;
                }
                else System.out.print(", " + ce.text());
            }
            System.out.print(" }\n");
        }
    }
}
