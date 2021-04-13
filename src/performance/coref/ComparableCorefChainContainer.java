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

public class ComparableCorefChainContainer {

    List<CustomCorefChain> corefChains;

    public ComparableCorefChainContainer() {
        corefChains = new LinkedList<>();
    }

    public ComparableCorefChainContainer(CoreDocument stanfordCoreDocument) throws NullDocumentException{
        corefChains = new LinkedList<>();

        for(CorefChain cc : stanfordCoreDocument.corefChains().values()){
            corefChains.add(new CustomCorefChain(cc));
        }

        ImpUtils.setDocument(stanfordCoreDocument);
        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            corefChains.add(new CustomCorefChain(cem));
        }
    }

    public static ComparableCorefChainContainer buildFromTxt(String pathToText) throws IOException, NullDocumentException {
        ComparableCorefChainContainer result = new ComparableCorefChainContainer();

		FileInputStream is = new FileInputStream(pathToText);     
		String content = IOUtils.toString(is, "UTF-8");
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);

        for (CorefChain cc : document.corefChains().values()){
            result.corefChains.add(new CustomCorefChain(cc));
        }
        System.out.println("test");
        ImpUtils.setDocument(document);
        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            result.corefChains.add(new CustomCorefChain(cem));
        }

        return result;
    }

    public static ComparableCorefChainContainer buildFromXml(String pathToXml) throws IOException{
        ComparableCorefChainContainer result = new ComparableCorefChainContainer();
		SAXBuilder builder = new SAXBuilder();
		FileInputStream is = new FileInputStream(pathToXml);     
	    try {
	    	Document document = (Document) builder.build(is);
	        Element rootNode = document.getRootElement();
	        List<Element> list = rootNode.getChildren("mention");
	        for (int i = 0; i < list.size(); i++) {
	        	Element node = (Element) list.get(i);
                CustomCorefChain ccc = result.get(Integer.parseInt(node.getChildText("CorefChain")));

                if ( ccc != null){
                    ccc.getCEMList().add(new CustomEntityMention(node.getChildText("text"), 
                        Integer.parseInt(node.getChildText("sentence")), 
                        Integer.parseInt(node.getChildText("start")),
                        Integer.parseInt(node.getChildText("end"))));
                }
                else{
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

    private CustomCorefChain get(int chainID) {
        if (chainID == 0) return null;
        for(CustomCorefChain ccc : corefChains){
            if (ccc.getId()== chainID){
                return ccc;
            }
        }
        return null;
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

    public ComparableCorefChainContainer(ComparableCorefChainContainer corefChainContainer) {
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

    public int getNumberOfEntities(){
        int tot = 0;
        for (CustomCorefChain ccc : corefChains){
            tot += ccc.getCEMList().size();
        }
        return tot;
    }

    public double precision(ComparableCorefChainContainer reference){
        double tot = 0;
        for (CustomCorefChain ccc : corefChains){
            tot += ccc.precision(reference);
        }
        return tot/getNumberOfEntities();
    }

    public double recall(ComparableCorefChainContainer reference) {
        double tot = 0;
        for (CustomCorefChain ccc : corefChains){
            tot += ccc.recall(reference);
        }
        return tot/reference.getNumberOfEntities();
    }

    public boolean contains(CustomEntityMention ce){
        for (CustomCorefChain ccc : corefChains){
            if (ccc.getCEMList().contains(ce)) return true;
        }
        return false;
    }

    public void addEntityAsNewChain(CustomEntityMention ce, int id){
        CustomCorefChain temp = new CustomCorefChain(ce);
        temp.setId(id);
        corefChains.add(temp);
    }

    public void addEntityAsNewChain(CustomEntityMention ce) {
        corefChains.add(new CustomCorefChain(ce));
    }

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
}
