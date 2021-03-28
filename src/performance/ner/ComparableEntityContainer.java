package performance.ner;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.util.Properties;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class ComparableEntityContainer {

    List<ComparableEntity> entities;


    public ComparableEntityContainer() {
        entities = new LinkedList<>();
    }

    public ComparableEntityContainer(List<ComparableEntity> entities) {
        this.entities = entities;
    }

    public static ComparableEntityContainer buildFromXml(String pathToReference) throws IOException{
        ComparableEntityContainer result = new ComparableEntityContainer();
		SAXBuilder builder = new SAXBuilder();
		FileInputStream is = new FileInputStream(pathToReference);     
	    try {
	    	Document document = (Document) builder.build(is);
	        Element rootNode = document.getRootElement();
	        List<Element> list = rootNode.getChildren("mention");
	        for (int i = 0; i < list.size(); i++) {
	        	Element node = (Element) list.get(i);
				if (node.getChildText("mentionType").equals("explicit")){
					result.entities.add(new ComparableEntity(node.getChildText("text"), 
	        			Integer.parseInt(node.getChildText("sentence")), 
	        			Integer.parseInt(node.getChildText("start")),
	        			Integer.parseInt(node.getChildText("end"))));
				}
	        }
	    } catch (IOException io) {
	    	System.out.println(io.getMessage());
	    } catch (JDOMException jdomex) {
	    	System.out.println(jdomex.getMessage());
	    }
        return result;
	}

    public static ComparableEntityContainer buildFromTxt(String pathToText) throws IOException {
        ComparableEntityContainer result = new ComparableEntityContainer();
		FileInputStream is = new FileInputStream(pathToText);     
		String content = IOUtils.toString(is, "UTF-8");
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);
		boolean person;
		for (CoreEntityMention e : document.entityMentions()){
			person = false;
			if (e.entityType().equals("PERSON")) {
				for(CoreLabel token : e.tokens()){
					if (token.ner().equals("PERSON")){
						person = true;
					}
				}
				if (person) result.entities.add(new ComparableEntity(e));
			}
		}
		result.entities.sort(Comparator.comparing(ComparableEntity::getSentenceNumber).thenComparing(ComparableEntity::getTokenNumberFirst));
        return result;
    }
	

    public List<ComparableEntity> getEntities() {
        return this.entities;
    }

    public void setEntities(List<ComparableEntity> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return "{" +
            " entities='" + getEntities() + "'" +
            "}";
    }

    public void display(){
        System.out.print("{ ");
        for (ComparableEntity ce : entities){
            System.out.print(ce.getText() + ", ");
        }
        System.out.print(" }\n");
    }
}
