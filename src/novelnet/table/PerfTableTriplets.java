package novelnet.table;

import java.util.LinkedList;
import java.util.List;

import novelnet.util.CustomTriple;

public class PerfTableTriplets {

    /**
	 * First column of result table representing the source of the triplet. 
     * Every row should have "ref" or "eval" as a value.
	*/
    List<String> source;
    
    /**
	 * Second column of result table (Entity)
	*/
	List<CustomTriple> triplet;

	/**
	 * Third column of result table (comparison result). 
     * Every row should have "TP", "FP" or "FN" as a value (meaning True Positive, False Positive and False Negative).
	*/
	List<String> perf;

    public PerfTableTriplets() {

        source = new LinkedList<>();
        triplet = new LinkedList<>();
        perf = new LinkedList<>();
    }

    public PerfTableTriplets(List<String> source, List<CustomTriple> triplet, List<String> perf) {
        this.source = source;
        this.triplet = triplet;
        this.perf = perf;
    }

    public List<String> getSource() {
        return this.source;
    }

    public void setSource(List<String> source) {
        this.source = source;
    }

    public List<CustomTriple> getTriplet() {
        return this.triplet;
    }

    public void setTriplet(List<CustomTriple> triplet) {
        this.triplet = triplet;
    }

    public List<String> getPerf() {
        return this.perf;
    }

    public void setPerf(List<String> perf) {
        this.perf = perf;
    }

    @Override
    public String toString() {
        return "{" +
            " source='" + getSource() + "'" +
            ", triplet='" + getTriplet() + "'" +
            ", perf='" + getPerf() + "'" +
            "}";
    }

    public void add(String src, CustomTriple cem, String eval){
        source.add(src);
        triplet.add(cem);
        perf.add(eval);
    }

    public void display() {
        for (int i = 0; i < source.size(); i++){
			System.out.println("source : " + source.get(i) + " |\t triplet : " + triplet.get(i).niceTableDisplay() + "\t| perf : " + perf.get(i));
		}
    }
    
}
