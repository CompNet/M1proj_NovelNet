package performance.triplets;

import java.io.IOException;

import edu.stanford.nlp.stats.PrecisionRecallStats;
import novelnet.util.NullDocumentException;

public class CompareTriplets {
    
    private TripletsContainer reference;
    private TripletsContainer triplesToEvaluate;
    private PrecisionRecallStats perf;


    public CompareTriplets() {
		perf = new PrecisionRecallStats();
    }

    public CompareTriplets(TripletsContainer reference, TripletsContainer triplesToEvaluate) {
        this.reference = reference;
        this.triplesToEvaluate = triplesToEvaluate;
		perf = new PrecisionRecallStats();
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
		
        reference = TripletsContainer.buildFromXml("res/manualAnnotation/Triplets/" + language + "/" + fileName + ".xml");
        
        triplesToEvaluate = TripletsContainer.buildFromTxt(language, fileName);
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










    //Tests

    public static void main(String[] args){
        try {
            testImport();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

    private static void testImport() throws IOException, NullDocumentException{
        String language = "en";
        String fileName = "HarryPotter3_TrainBoarding";

        CompareTriplets test = new CompareTriplets(language, fileName);
        test.display();
    }
}
