package performance.ner;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


import edu.stanford.nlp.stats.PrecisionRecallStats;
import novelnet.table.PerfTableNer;
import novelnet.util.CustomEntityMention;

public class CompareNER {

	/**
	 * the container with the reference entities (ground truth)
	*/
	EntityContainer reference;

	/**
	 * the container with the entities to evaluate
	*/
	EntityContainer entityList;

	/**
	 * used to compute perf stats
	*/
	PrecisionRecallStats perf;

	/**
	 * first column of result table (Entity)
	*/
	PerfTableNer perfTable;

	/**
	 * empty Class Constructor
	*/
	public CompareNER(){
		reference = new EntityContainer();
		entityList = new EntityContainer();
		perf = new PrecisionRecallStats();
		perfTable = new PerfTableNer();
	}

	/**
	 * Class Constructor specifying the path to the file from witch you want to extract the entities (files can be .xml or .txt)
	 *
     * @param evaluationFilePath evaluation file path (should be .txt)
     * @param referenceFilePath reference file path (should be .xml)
	*/
	public CompareNER(String evaluationFilePath, String referenceFilePath) throws IOException{
		if(evaluationFilePath.substring(evaluationFilePath.length()-4, evaluationFilePath.length()).equals(".txt")){
			entityList = EntityContainer.buildFromTxt(evaluationFilePath);
		}
		else if (evaluationFilePath.substring(evaluationFilePath.length()-4, evaluationFilePath.length()).equals(".xml")){
			entityList = EntityContainer.buildFromXml(evaluationFilePath);
		}
		else System.out.println("File type not recognized for argument 1");
		if(referenceFilePath.substring(referenceFilePath.length()-4, referenceFilePath.length()).equals(".txt")){
			reference = EntityContainer.buildFromTxt(referenceFilePath);
		}
		else if (referenceFilePath.substring(referenceFilePath.length()-4, referenceFilePath.length()).equals(".xml")){
			reference = EntityContainer.buildFromXml(referenceFilePath);
		}
		else System.out.println("File type not recognized for argument 2");
		perf = new PrecisionRecallStats();
		perfTable = new PerfTableNer();
	}
	
	public EntityContainer getEntityList(){
		return this.entityList;
	}
	
	public void setEntityList(EntityContainer entityList) {
		this.entityList = entityList;
	}
	
	public EntityContainer getReference(){
		return this.reference;
	}
	
	public void setReference(EntityContainer reference) {
		this.reference = reference;
	}
	
	/**
	 * Compare reference and estimation and build the result table
	*/
	public void compare() {
		boolean found;
		//comparing estimation to reference to find True Positive and False Positive
		for (CustomEntityMention ce : entityList.getEntities()){
			found = false;
			for (CustomEntityMention ceToCompare : reference.getEntities()){
				if(ce.equalTo(ceToCompare)){
					//True Positive
					perfTable.add("eval", ce, "TP");
					found = true;
					break;
				}
			}
			if (found) perf.incrementTP();
			else {
				//False Positive
				perf.incrementFP();
				perfTable.add("eval", ce, "FP");

			}
		}
		//comparing reference to estimation to find False Negative
		for (CustomEntityMention ce : reference.getEntities()){
			found = false;
			for (CustomEntityMention ceToCompare : entityList.getEntities()){
				if(ce.equalTo(ceToCompare)){
					//not a False negative
					found = true;
					break;
				}
			}
			if(!found) {
				//False Negative
				perf.incrementFN();
				perfTable.add("ref ", ce, "FN");

			}
		}
	}

	public void display(){
		System.out.println("Ground Truth");
		reference.display();
		System.out.println("\nEntity List");
		entityList.display();
		System.out.println(perf);
		System.out.println("Precision : " + perf.getPrecision() + "\t Rappel : " + perf.getRecall()+ "\t F-mesures : " + perf.getFMeasure());
	}

	public void displayResult(){
		perfTable.display();
		System.out.println("\n" + perf);
		System.out.println("Precision : " + perf.getPrecision() + "\t Rappel : " + perf.getRecall()+ "\t F-mesures : " + perf.getFMeasure());
	}
	
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println("le nom du fichier de corpus à traiter (avec en/ ou fr/) :");
		String fileName = sc.nextLine();
		sc.close();
		CompareNER c = new CompareNER("res/corpus/"+fileName+".txt", "res/manualAnnotation/ner_coref_clustering/"+fileName+".xml");
		c.compare();
		c.display();
		c.displayResult();
	}
}