package performance.ner;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


import edu.stanford.nlp.stats.PrecisionRecallStats;

public class CompareNER {
	ComparableEntityContainer reference;
	ComparableEntityContainer entityList;
	PrecisionRecallStats perf;
	List<ComparableEntity> resultTableCE;
	List<String> resultTableString;

	public CompareNER(){
		reference = new ComparableEntityContainer();
		entityList = new ComparableEntityContainer();
		perf = new PrecisionRecallStats();
		resultTableCE = new LinkedList<>();
		resultTableString = new LinkedList<>();
	}

	public CompareNER(String evaluationFilePath, String referenceFilePath) throws IOException{
		if(evaluationFilePath.substring(evaluationFilePath.length()-4, evaluationFilePath.length()).equals(".txt")){
			entityList = ComparableEntityContainer.buildFromTxt(evaluationFilePath);
		}
		else if (evaluationFilePath.substring(evaluationFilePath.length()-4, evaluationFilePath.length()).equals(".xml")){
			entityList = ComparableEntityContainer.buildFromXml(evaluationFilePath);
		}
		else System.out.println("File type not recognized for argument 1");
		if(referenceFilePath.substring(referenceFilePath.length()-4, referenceFilePath.length()).equals(".txt")){
			reference = ComparableEntityContainer.buildFromTxt(referenceFilePath);
		}
		else if (referenceFilePath.substring(referenceFilePath.length()-4, referenceFilePath.length()).equals(".xml")){
			reference = ComparableEntityContainer.buildFromXml(referenceFilePath);
		}
		else System.out.println("File type not recognized for argument 2");
	}
	
	public ComparableEntityContainer getEntityList(){
		return this.entityList;
	}
	
	public void setEntityList(ComparableEntityContainer entityList) {
		this.entityList = entityList;
	}
	
	public ComparableEntityContainer getReference(){
		return this.reference;
	}
	
	public void setReference(ComparableEntityContainer reference) {
		this.reference = reference;
	}
	
	public void display(){
		System.out.println("Ground Truth");
		entityList.display();
		System.out.println("\nEntity List");
		entityList.display();
		System.out.println(perf);
		System.out.println("Precision : " + perf.getPrecision() + "\t Rappel : " + perf.getRecall()+ "\t F-mesures : " + perf.getFMeasure());
	}

	public void displayResult(){
		for (int i = 0; i < resultTableCE.size(); i++){
			System.out.println(resultTableCE.get(i) + "\tmesure : " + resultTableString.get(i));
		}
		System.out.println("\n" + perf);
		System.out.println("Precision : " + perf.getPrecision() + "\t Rappel : " + perf.getRecall()+ "\t F-mesures : " + perf.getFMeasure());
	}
	
	public void compare() {
		boolean found;
		for (ComparableEntity ce : entityList.getEntities()){
			found = false;
			for (ComparableEntity ceToCompare : reference.getEntities()){
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
		for (ComparableEntity ce : reference.getEntities()){
			found = false;
			for (ComparableEntity ceToCompare : entityList.getEntities()){
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
		Scanner sc = new Scanner(System.in);
		System.out.println("le nom du fichier de corpus à traiter:");
		String fileName = sc.nextLine();
		sc.close();
		CompareNER c = new CompareNER("res/corpus/"+fileName+".txt", "performance/ner/"+fileName+".xml");
		c.display();
		c.displayResult();
	}
}