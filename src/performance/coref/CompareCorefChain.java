package performance.coref;

public class CompareCorefChain {

    CorefChainContainer chainsToEvaluate;

    CorefChainContainer groundTruth;
    
    double precision;

    double recall;

    double fMeasure;


    public CompareCorefChain(CorefChainContainer chainsToEvaluate, CorefChainContainer groundTruth) {
        this.chainsToEvaluate = chainsToEvaluate;
        this.groundTruth = groundTruth;
        precision = 0;
        recall = 0;
        fMeasure = 0;
    }

    public CorefChainContainer getChainsToEvaluate() {
        return this.chainsToEvaluate;
    }

    public void setChainsToEvaluate(CorefChainContainer chainsToEvaluate) {
        this.chainsToEvaluate = chainsToEvaluate;
    }

    public CorefChainContainer getGroundTruth() {
        return this.groundTruth;
    }

    public void setGroundTruth(CorefChainContainer groundTruth) {
        this.groundTruth = groundTruth;
    }
    
    public void precisionCalculation(){
        precision = 0;
    }

    public void recallCalculation(){
        recall = 0;
    }

    public void fMeasureCalculation(){
        if (recall == 0 && precision == 0) fMeasure = 0;
        else fMeasure = 2*((precision*recall)/(precision+recall));
    }
    
}
