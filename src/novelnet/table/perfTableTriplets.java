package novelnet.table;

import java.util.List;

import novelnet.util.CustomTriple;

public class perfTableTriplets {

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
    
}
