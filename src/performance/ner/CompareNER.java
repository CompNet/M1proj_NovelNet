package performance.ner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.stats.PrecisionRecallStats;

public class CompareNER {
	List<ComparableEntity> groundTruth;
	List<ComparableEntity> entityList;
	PrecisionRecallStats perf;
	List<ComparableEntity> resultTableCE;
	List<String> resultTableString;

	public CompareNER(){
		groundTruth = new LinkedList<>();
		entityList = new LinkedList<>();
		perf = new PrecisionRecallStats();
		resultTableCE = new LinkedList<>();
		resultTableString = new LinkedList<>();
	}
	
	public List<ComparableEntity> getEntityList(){
		return this.entityList;
	}
	
	public void setEntityList(List<ComparableEntity> entityList) {
		this.entityList = entityList;
	}
	
	public List<ComparableEntity> getGroundTruth(){
		return this.groundTruth;
	}
	
	public void setGroundTruth(List<ComparableEntity> groundTruth) {
		this.groundTruth = groundTruth;
	}
	
	public void display(){
		System.out.println("Ground Truth");
		for (ComparableEntity ce : groundTruth){
			System.out.println(ce);
		}
		System.out.println("\nEntity List");
		for (ComparableEntity ce : entityList){
			System.out.println(ce);
		}
		System.out.println(perf);
		System.out.println("Precision : " + perf.getPrecision() + "\t Rappel : " + perf.getRecall()+ "\t F-mesures : " + perf.getFMeasure());
	}

	public void displayResult(){
		for (int i = 0; i < resultTableCE.size(); i++){
			System.out.println(resultTableCE.get(i) + "mesure : " + resultTableString.get(i));
		}
		System.out.println("\n" + perf);
		System.out.println("Precision : " + perf.getPrecision() + "\t Rappel : " + perf.getRecall()+ "\t F-mesures : " + perf.getFMeasure());
	}

	public void compare(String pathToText, String pathToGroundTruth) throws IOException{
		initTxt(pathToText);
		initXml(pathToGroundTruth);
		compare();
	}
	
	public void initTxt(String pathToText) throws IOException {
		FileInputStream is = new FileInputStream(pathToText);     
		String content = IOUtils.toString(is, "UTF-8");
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);
		int index;
		for (CoreEntityMention e : document.entityMentions()){
			if (e.entityType().equals("PERSON")) {
				index = e.tokens().size()-1;
				entityList.add(new ComparableEntity(e.text(), e.tokens().get(0).sentIndex()+1, e.tokens().get(0).index(), e.tokens().get(index).index()));
			}
		}
		entityList.sort(Comparator.comparing(ComparableEntity::getSentenceNumber).thenComparing(ComparableEntity::getTokenNumberFirst));
	}
	
	public void initXml(String pathToGroundTruth) throws IOException{
		SAXBuilder builder = new SAXBuilder();
		FileInputStream is = new FileInputStream(pathToGroundTruth);     
	    try {
	    	Document document = (Document) builder.build(is);
	        Element rootNode = document.getRootElement();
	        List<Element> list = rootNode.getChildren("mention");
	        for (int i = 0; i < list.size(); i++) {
	        	Element node = (Element) list.get(i);
	        	groundTruth.add(new ComparableEntity(node.getChildText("text"), 
	        			Integer.parseInt(node.getChildText("sentence")), 
	        			Integer.parseInt(node.getChildText("start")),
	        			Integer.parseInt(node.getChildText("end"))));
	        }
	    } catch (IOException io) {
	    	System.out.println(io.getMessage());
	    } catch (JDOMException jdomex) {
	    	System.out.println(jdomex.getMessage());
	    }
	}
	
	public void compare() {
		boolean found;
		for (ComparableEntity ce : entityList){
			found = false;
			for (ComparableEntity ceToCompare : groundTruth){
				if(ce.compareTo(ceToCompare)){
					resultTableCE.add(ce);
					resultTableString.add("TP");
					found = true;
					break;
				}
			}
			if (found) perf.incrementTP();
			else {
				perf.incrementFP();
				resultTableCE.add(ce);
				resultTableString.add("FP");
			}
		}
		for (ComparableEntity ce : groundTruth){
			found = false;
			for (ComparableEntity ceToCompare : entityList){
				if(ce.compareTo(ceToCompare)){
					found = true;
					break;
				}
			}
			if(!found) {
				perf.incrementFN();
				resultTableCE.add(ce);
				resultTableString.add("FN");
			}
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		CompareNER c = new CompareNER();
		Scanner sc = new Scanner(System.in);
		System.out.println("le nom du fichier de corpus à traiter:");
		String fileName = sc.nextLine();
		sc.close();
		c.compare("res/corpus/"+fileName+".txt", "performance/ner/"+fileName+".xml");
		c.display();
		c.displayResult();
	}
}