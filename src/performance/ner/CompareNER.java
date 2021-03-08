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
	List<ComparableEntity> groundTruth = new LinkedList<>();
	List<ComparableEntity> entityList = new LinkedList<>();
	Scanner sc = new Scanner(System.in);
	PrecisionRecallStats perf = new PrecisionRecallStats();
	
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
	
	public void display(List<ComparableEntity> listE){
		for (int i = 0; i < listE.size(); i++){
			System.out.println(listE.get(i).toString()); 
		}
	}
	
	public void initTxt() throws IOException {
		System.out.println("Saisir chemin du fichier txt à comparer:");
		String path = sc.nextLine();
		FileInputStream is = new FileInputStream(path);     
		String content = IOUtils.toString(is, "UTF-8");
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);
		int index = 0;
		for (CoreEntityMention e : document.entityMentions()){
			if (e.entityType().equals("PERSON")) {
				index = e.tokens().size()-1;
				entityList.add(new ComparableEntity(e.text(), e.tokens().get(0).sentIndex()+1, e.tokens().get(0).index(), e.tokens().get(index).index()));
				index = 0;
			}
		}
		entityList.sort(Comparator.comparing(ComparableEntity::getSentenceNumber).thenComparing(ComparableEntity::getTokenNumberFirst));
	}
	
	public void initXml() throws IOException{
		SAXBuilder builder = new SAXBuilder();
		System.out.println("Saisir chemin du fichier XML à convertir:");
		String path = sc.nextLine();
		FileInputStream is = new FileInputStream(path);     
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
	    sc.close();
	}
	
	public void compare() {
		//todo
		int i = 0;
		int j = 0;
		while (i < groundTruth.size() && j < entityList.size()) {
			if (groundTruth.get(i).toString().equals(entityList.get(j).toString())) {
				perf.incrementTP();
			}
		}
		System.out.println("VP : " + perf.getTP());
		System.out.println("FP : " + perf.getFP());
		System.out.println("FN : " + perf.getFN());
		System.out.println("Precision : " + perf.getPrecision());
		System.out.println("Rappel : " + perf.getRecall());
		System.out.println("F-mesures : " + perf.getFMeasure());
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		CompareNER c = new CompareNER();
		//res/corpus/reference.txt
		c.initTxt();
		c.display(c.getEntityList());
		//performance/ner/reference.xml
		c.initXml();
		c.compare();
	}
}