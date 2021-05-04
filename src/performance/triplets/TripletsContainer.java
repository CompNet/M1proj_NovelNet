package performance.triplets;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import edu.stanford.nlp.ling.CoreLabel;
import novelnet.util.CustomCorefChain;
import novelnet.util.CustomEntityMention;
import novelnet.util.CustomTriple;

public class TripletsContainer {

    private List<CustomTriple> triples;

    TripletsContainer(){
        triples = new LinkedList<>();
    }

    public List<CustomTriple> getTriples() {
        return this.triples;
    }

    public void setTriples(List<CustomTriple> triples) {
        this.triples = triples;
    }

    /**
	 * Build a TripleContainer from an xml file
	 * 
	 * @param pathToXml path to the xml file
     * @return the Container
	 */
    public static TripletsContainer buildFromXml(String pathToXml) throws IOException{
        TripletsContainer result = new TripletsContainer();
		SAXBuilder builder = new SAXBuilder();
		FileInputStream is = new FileInputStream(pathToXml);
        CustomCorefChain object;
        CustomCorefChain subject;

	    try {
	    	Document document = (Document) builder.build(is);
	        Element rootNode = document.getRootElement();
	        List<Element> list = rootNode.getChildren("Triplet");
	        for (int i = 0; i < list.size(); i++) {
	        	Element triplet = (Element) list.get(i);

                //  Subject
                Element subjectElement = triplet.getChildren("Subject").get(0);

                subject = new CustomCorefChain(
                    new CustomEntityMention(subjectElement.getChildText("text"),"",
                        Integer.parseInt(subjectElement.getChildText("sentence")),
                        Integer.parseInt(subjectElement.getChildText("start")),
                        Integer.parseInt(subjectElement.getChildText("end"))
                    )
                );


                //  Verb
                Element verbElement = triplet.getChildren("Verb").get(0);

                CoreLabel verb = new CoreLabel();
                verb.setValue(verbElement.getChildText("text"));
                verb.setOriginalText(verbElement.getChildText("text"));
                verb.setIndex(Integer.parseInt(verbElement.getChildText("start")));
                verb.setSentIndex(Integer.parseInt(verbElement.getChildText("sentence")));
                

                //  Object

                List<Element> listObject = triplet.getChildren("Object");

                if (!listObject.isEmpty()){
                    Element objectElement = listObject.get(0);
                    object = new CustomCorefChain(
                        new CustomEntityMention(objectElement.getChildText("text"),"",
                            Integer.parseInt(objectElement.getChildText("sentence")),
                            Integer.parseInt(objectElement.getChildText("start")),
                            Integer.parseInt(objectElement.getChildText("end"))
                        )
                    );
                }
                else {
                    object = null;
                }

                // Triple

                result.triples.add(new CustomTriple(subject, object, verb));
	        }
	    } catch (IOException io) {
	    	System.out.println(io.getMessage());
	    } catch (JDOMException jdomex) {
	    	System.out.println(jdomex.getMessage());
	    }
        return result;
	}

    @Override
    public String toString() {
        return "{" +
            " triples='" + getTriples() + "'" +
            "}";
    }

    public void display(){
        for (CustomTriple customTriple : triples) {
            System.out.println(customTriple);
        }
    }









    //Tests

    public static void main(String[] args){
        testImport();
	}

    private static void testImport(){
        String language = "en";
        String fileName = "HarryPotter3_TrainBoarding";
        String pathToXml = "res/manualAnnotation/Triplets/" + language + "/" + fileName + ".xml";

        TripletsContainer test = null;
        try {
            test = TripletsContainer.buildFromXml(pathToXml);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        test.display();
    }
    
}
