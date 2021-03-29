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

import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;

import performance.ner.ComparableEntity;

public class ComparableCorefChainContainer {

    List<ComparableCorefChain> corefChains;

    public ComparableCorefChainContainer() {
        corefChains = new LinkedList<>();
    }

    public ComparableCorefChainContainer(CoreDocument stanfordCoreDocument) throws NullDocumentException{
        corefChains = new LinkedList<>();

        for(CorefChain cc : stanfordCoreDocument.corefChains().values()){
            corefChains.add(new ComparableCorefChain(cc,0));
        }

        ImpUtils.setDocument(stanfordCoreDocument);
        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            corefChains.add(new ComparableCorefChain(cem,0));
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
            result.corefChains.add(new ComparableCorefChain(cc,0));
        }

        ImpUtils.setDocument(document);
        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            result.corefChains.add(new ComparableCorefChain(cem,0));
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
                ComparableCorefChain ccc = result.get(Integer.parseInt(node.getChildText("CorefChain")));

                if ( ccc != null){
                    ccc.getEntities().add(new ComparableEntity(node.getChildText("text"), 
                        Integer.parseInt(node.getChildText("sentence")), 
                        Integer.parseInt(node.getChildText("start")),
                        Integer.parseInt(node.getChildText("end"))));
                }
                else{
                    result.addEntityAsNewChain(new ComparableEntity(node.getChildText("text"), 
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

    private ComparableCorefChain get(int chainID) {
        if (chainID == 0) return null;
        for(ComparableCorefChain ccc : corefChains){
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
        for (ComparableCorefChain ccc : corefChains){
            System.out.print("{ ");
            for (ComparableEntity ce : ccc.getEntities()){
                System.out.print(ce.getText() + ", ");
            }
            System.out.print(" }\n");
        }
    }

    public ComparableCorefChainContainer(ComparableCorefChainContainer corefChainContainer) {
        ComparableCorefChain tmpccc;
        corefChains = new LinkedList<>();
        for (ComparableCorefChain ccc : corefChainContainer.getCorefChains()){
            tmpccc = new ComparableCorefChain();
            for (ComparableEntity ce : ccc.getEntities()){
                tmpccc.getEntities().add(ce);
            }
            corefChains.add(tmpccc);
        }
    }

    public List<ComparableCorefChain> getCorefChains() {
        return this.corefChains;
    }

    public void setCorefChains(List<ComparableCorefChain> corefChains) {
        this.corefChains = corefChains;
    }

    public int getNumberOfEntities(){
        int tot = 0;
        for (ComparableCorefChain ccc : corefChains){
            tot += ccc.getEntities().size();
        }
        return tot;
    }

    public double precision(ComparableCorefChainContainer reference){
        double tot = 0;
        for (ComparableCorefChain ccc : corefChains){
            tot += ccc.precision(reference);
        }
        return tot/getNumberOfEntities();
    }

    public boolean contains(ComparableEntity ce){
        for (ComparableCorefChain ccc : corefChains){
            if (ccc.getEntities().contains(ce)) return true;
        }
        return false;
    }

    public void addEntityAsNewChain(ComparableEntity ce, int id){
        corefChains.add(new ComparableCorefChain(ce, id));
    }

    public void addEntityAsNewChain(ComparableEntity ce) {
        corefChains.add(new ComparableCorefChain(ce, 0));
    }

    public void delete(ComparableEntity ce) {
        for (ComparableCorefChain ccc : corefChains){
            if (ccc.getEntities().contains(ce)){
                if (ccc.getEntities().size()==1) corefChains.remove(ccc);
                else ccc.getEntities().remove(ce);
            }
        }
    }
}
