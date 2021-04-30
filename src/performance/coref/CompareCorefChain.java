package performance.coref;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import novelnet.util.CustomCorefChain;
import novelnet.util.CustomEntityMention;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;

public class CompareCorefChain {

    /**
	 * the container with the chains to evaluate
	*/
    CorefChainContainer chainsToEvaluate;

    /**
	 * the container with the reference chains
	*/
    CorefChainContainer reference;
    
    double precision;

    double recall;

    double fMeasure;

    /**
	 * empty Class Constructor
	*/
    public CompareCorefChain() {
        precision = 0;
        recall = 0;
        fMeasure = 0;
    }

    /**
	 * Class Constructor specifying the path to the file from witch you want to extract the chains (files can be .xml or .txt)
	 *
     * @param evaluationFilePath evaluation file path (should be .txt)
     * @param referenceFilePath reference file path (should be .xml)
	*/
    public CompareCorefChain(String evaluationFilePath, String referenceFilePath) throws IOException, NullDocumentException{
		if(evaluationFilePath.substring(evaluationFilePath.length()-4, evaluationFilePath.length()).equals(".txt")){
			chainsToEvaluate = new CorefChainContainer(ImpUtils.processCoref(evaluationFilePath));
		}
		else if (evaluationFilePath.substring(evaluationFilePath.length()-4, evaluationFilePath.length()).equals(".xml")){
			chainsToEvaluate = CorefChainContainer.buildFromXml(evaluationFilePath);
		}
		else System.out.println("File type not recognized for argument 1");
		if(referenceFilePath.substring(referenceFilePath.length()-4, referenceFilePath.length()).equals(".txt")){
			reference = new CorefChainContainer(ImpUtils.processCoref(referenceFilePath));
		}
		else if (referenceFilePath.substring(referenceFilePath.length()-4, referenceFilePath.length()).equals(".xml")){
			reference = CorefChainContainer.buildFromXml(referenceFilePath);
		}
		else System.out.println("File type not recognized for argument 2");
        precision = 0;
        recall = 0;
        fMeasure = 0;
	}

    /**
	 * Class Constructor specifying the containers
	 *
     * @param chainsToEvaluate evaluation container
     * @param reference reference container
	*/
    public CompareCorefChain(CorefChainContainer chainsToEvaluate, CorefChainContainer reference) {
        this.chainsToEvaluate = chainsToEvaluate;
        this.reference = reference;
        precision = 0;
        recall = 0;
        fMeasure = 0;
    }

    public double getPrecision() {
        return this.precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return this.recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getFMeasure() {
        return this.fMeasure;
    }

    public void setFMeasure(double fMeasure) {
        this.fMeasure = fMeasure;
    }

    public CorefChainContainer getChainsToEvaluate() {
        return this.chainsToEvaluate;
    }

    public void setChainsToEvaluate(CorefChainContainer chainsToEvaluate) {
        this.chainsToEvaluate = chainsToEvaluate;
    }

    public CorefChainContainer getReference() {
        return this.reference;
    }

    public void setReference(CorefChainContainer reference) {
        this.reference = reference;
    }

    /**
	 * pre Processing for the precision or recall computing
	 *
     * @return a list with the pre processed chain to evaluate at index 0 and the reference at index 1
	*/
    public List<CorefChainContainer> preProcessing(){
        CorefChainContainer tempChainsToEvaluate = chainsToEvaluate.manualClone();
        CorefChainContainer tempReference = reference.manualClone();
        List<CorefChainContainer> result = new LinkedList<>();

        CustomCorefChain resChain;
        int resSize = tempChainsToEvaluate.getCorefChains().size();

        //first pre processing step (removing singleton mention from the response wich are not present in the ground truth)
        for(int i = 0; i < resSize ; i++){
            resChain = tempChainsToEvaluate.getCorefChains().get(i);
            if (resChain.getCEMList().size()==1){
                if (!tempReference.contains(resChain.getCEMList().get(0))){
                    tempChainsToEvaluate.getCorefChains().remove(resChain);
                    i--;
                    resSize--;
                }
            }
        }
        
        //second pre processing step (adding mention present in the ground truth into the response (if they are absent))
        for(CustomCorefChain ccc : tempReference.getCorefChains()){
            for(CustomEntityMention cE : ccc.getCEMList()){
                if (!tempChainsToEvaluate.contains(cE)){
                    tempChainsToEvaluate.addEntityAsNewChain(cE);
                }
            }
        }

        result.add(tempChainsToEvaluate);
        result.add(tempReference);

        return result;
    }
    
    /**
	 * Computes and sets the precision with the B^3_sys algorithm
	*/
    public void precisionB3(){
        //first two steps of pre processing
        List<CorefChainContainer> tempChains = preProcessing();

        //using temporary chains to preserve original chains
        CorefChainContainer tempChainsToEvaluate = tempChains.get(0);
        CorefChainContainer tempReference = tempChains.get(1);

        //last pre processing step for precision
        for(CustomCorefChain ccc : tempChainsToEvaluate.getCorefChains()){
            for(CustomEntityMention ce : ccc.getCEMList()){
                if (!tempReference.contains(ce)){
                    tempReference.addEntityAsNewChain(ce);
                }
            }
        }      

        //precision computing
        precision = tempChainsToEvaluate.precision(tempReference);
    }

    /**
	 * Computes and sets the recall with the B^3_sys algorithm
	*/
    public void recallB3(){
        //first two steps of pre processing
        List<CorefChainContainer> tempChains = preProcessing();

        //using temporary chains to preserve original chains
        CorefChainContainer tempChainsToEvaluate = tempChains.get(0);
        CorefChainContainer tempReference = tempChains.get(1);

        CustomCorefChain keyChain;
        int keySize = tempChainsToEvaluate.getCorefChains().size();
        int chainSize;
        CustomEntityMention ce;

        //last pre processing step for recall
        for(int i = 0; i < keySize; i++){
            keyChain = tempChainsToEvaluate.getCorefChains().get(i);
            chainSize = keyChain.getCEMList().size();
            for (int j = 0; j < chainSize; j++){
                ce = keyChain.getCEMList().get(j);
                if (!tempReference.contains(ce)){
                    if (keyChain.getCEMList().size()==1){
                        tempChainsToEvaluate.getCorefChains().remove(keyChain);
                        i--;
                        keySize--;
                    }
                    else {
                        keyChain.getCEMList().remove(ce);
                        j--;
                        chainSize--;
                    }
                }
            }
        }
        
        //recall computing
        recall = tempChainsToEvaluate.recall(tempReference);
    }

    /**
	 * Computes and sets the fMeasure
	*/
    public void fMeasure(){
        if (recall == 0 && precision == 0) fMeasure = 0;
        else fMeasure = 2*((precision*recall)/(precision+recall));
    }

    /**
	 * computing all measures
	*/
    public void compare(){
        precisionB3();
        recallB3();
        fMeasure();
    }

    @Override
    public String toString() {
        return "{" +
            " chainsToEvaluate='" + getChainsToEvaluate() + "'" +
            ", reference='" + getReference() + "'" +
            ", precision='" + getPrecision() + "'" +
            ", recall='" + getRecall() + "'" +
            ", fMeasure='" + getFMeasure() + "'" +
            "}";
    }

    public void display(){
        System.out.println("\nChains to evaluate : ");
        chainsToEvaluate.display();
        System.out.println("\nreference : ");
        reference.display();
        displayMeasures();
    }

    public void displayMeasures(){
        System.out.print("\nprecision : ");
        System.out.format("%.3f", precision);
        System.out.print("\nrecall : ");
        System.out.format("%.3f", recall);
        System.out.print("\nfMeasure : ");
        System.out.format("%.3f", fMeasure);
        System.out.println();
    }

    public void testPreTreating(){
        chainsToEvaluate = new CorefChainContainer();
        reference = new CorefChainContainer();

        CustomEntityMention A = new CustomEntityMention("A", "A", 0, 2, 2);
        CustomEntityMention B = new CustomEntityMention("B", "B", 1, 3, 4);
        CustomEntityMention C = new CustomEntityMention("C", "C", 2, 1, 3);
        CustomEntityMention D = new CustomEntityMention("D", "D", 4, 5, 5);
        CustomEntityMention I = new CustomEntityMention("I", "I", 6, 6, 6);
        CustomEntityMention J = new CustomEntityMention("J", "J", 7, 1, 2);

        CustomCorefChain a = new CustomCorefChain();
        a.getCEMList().add(A);
        a.getCEMList().add(B);
        a.getCEMList().add(C);

        CustomCorefChain b = new CustomCorefChain();
        b.getCEMList().add(A);
        b.getCEMList().add(B);
        b.getCEMList().add(D);

        CustomCorefChain c = new CustomCorefChain();
        c.getCEMList().add(I);
        c.getCEMList().add(J);

        chainsToEvaluate.getCorefChains().add(b);
        chainsToEvaluate.getCorefChains().add(c);

        reference.getCorefChains().add(a);

        compare();

        display();

    }

    public static void testOnRealData() throws IOException, NullDocumentException{
        
        CompareCorefChain ccc = new CompareCorefChain("res/corpus/en/TheLightningThief_chapter1.txt", "performance/ner_coref_clustering/en/TheLightningThief_chapter1.xml");
        ccc.compare();
        ccc.display();

    }

    public static void main(String[] args) throws IOException, NullDocumentException{

        /*CompareCorefChain ccc = new CompareCorefChain();
        ccc.testPreTreating();*/
        testOnRealData();
    }
    
}
