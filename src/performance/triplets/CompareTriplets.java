package performance.triplets;

import java.io.IOException;

import edu.stanford.nlp.stats.PrecisionRecallStats;
import novelnet.table.PerfTableTriplets;
import novelnet.util.CustomTriple;
import novelnet.util.NullDocumentException;

/**
 * Used to compare our DirectInteraction result with the reference.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class CompareTriplets {
    
    private TripletsContainer reference;
    private TripletsContainer triplesToEvaluate;
    private PrecisionRecallStats perf;
    /**
     * a table wich store the result
    */
    private PerfTableTriplets perfTable;

    /**
     *  Class Constructor
    */
    public CompareTriplets() {
		perf = new PrecisionRecallStats();
        perfTable = new PerfTableTriplets();
    }

    /**
     *  Class Constructor specifying the reference and the action to evaluate.
    */
    public CompareTriplets(TripletsContainer reference, TripletsContainer triplesToEvaluate) {
        this.reference = reference;
        this.triplesToEvaluate = triplesToEvaluate;
		perf = new PrecisionRecallStats();
        perfTable = new PerfTableTriplets();
    }

    /**
	 * Class Constructor specifying the path to the file from witch you want to extract the entities (files can be .xml or .txt)
	 *
     * @param evaluationFilePath evaluation file path (should be .txt)
     * @param referenceFilePath reference file path (should be .xml)
     * @throws NullDocumentException
	*/
	public CompareTriplets(String language, String fileName) throws IOException, NullDocumentException{
		perf = new PrecisionRecallStats();
        perfTable = new PerfTableTriplets();
		
        reference = TripletsContainer.buildFromXml("res/manualAnnotation/Triplets/" + language + "/" + fileName + ".xml");
        
        triplesToEvaluate = TripletsContainer.buildFromTxt("res/corpus/" + language + "/" + fileName + ".txt", "res/manualAnnotation/ner_coref_clustering/" + language + "/" + fileName + ".xml");
	}

    public PrecisionRecallStats getPerf() {
        return this.perf;
    }

    public void setPerf(PrecisionRecallStats perf) {
        this.perf = perf;
    }

    public TripletsContainer getReference() {
        return this.reference;
    }

    public void setReference(TripletsContainer reference) {
        this.reference = reference;
    }

    public TripletsContainer getTriplesToEvaluate() {
        return this.triplesToEvaluate;
    }

    public void setTriplesToEvaluate(TripletsContainer triplesToEvaluate) {
        this.triplesToEvaluate = triplesToEvaluate;
    }

    @Override
    public String toString() {
        return "{" +
            " reference='" + getReference() + "'" +
            ", triplesToEvaluate='" + getTriplesToEvaluate() + "'" +
            "}";
    }

    public void display(){
        System.out.println("reference : ");
        reference.display();

        System.out.println("\ntriples to evaluate : ");
        triplesToEvaluate.display();
    }

    public void displayResult(){
        perfTable.display();
		System.out.println("\n" + perf);
		System.out.println("Precision : " + perf.getPrecision() + "\t Rappel : " + perf.getRecall()+ "\t F-mesures : " + perf.getFMeasure());
    }

    /**
	 * Compare reference and estimation and build the result table
	*/
	public void compare() {
		boolean found;
		//comparing estimation to reference to find True Positive and False Positive
		for (CustomTriple triplet : triplesToEvaluate.getTriples()){
			found = false;
			for (CustomTriple tripletToCompare : reference.getTriples()){
				if(triplet.compareRefToEval(tripletToCompare)){
					//True Positive
					perfTable.add("eval", triplet, "TP");
					found = true;
					break;
				}
			}
			if (found) perf.incrementTP();
			else {
				//False Positive
				perf.incrementFP();
				perfTable.add("eval", triplet, "FP");

			}
		}
		//comparing reference to estimation to find False Negative
		for (CustomTriple triplet : reference.getTriples()){
			found = false;
			for (CustomTriple tripletToCompare : triplesToEvaluate.getTriples()){
				if(triplet.compareRefToEval(tripletToCompare)){
					//not a False negative
					found = true;
					break;
				}
			}
			if(!found) {
				//False Negative
				perf.incrementFN();
				perfTable.add("ref ", triplet, "FN");

			}
		}
	}










    //Tests

    public static void main(String[] args){
        try {
            test();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    private static void test() throws IOException, NullDocumentException{
        String language = "en";
        String fileName = "HarryPotter3_TrainBoarding";

        CompareTriplets test = new CompareTriplets(language, fileName);

		test.display();

        test.compare();
        
        System.out.println("\n\nresults");
		test.displayResult();
    }
}
