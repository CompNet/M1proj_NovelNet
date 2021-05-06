package novelnet.table;

import java.util.LinkedList;
import java.util.List;

import novelnet.util.CustomEntityMention;

public class perfTableNer {

    /**
	 * First column of result table representing the source of the entity. 
     * Every row should have "ref" or "eval" as a value.
	*/
    List<String> source;

    /**
	 * Second column of result table (Entity)
	*/
	List<CustomEntityMention> entity;

	/**
	 * Thrid column of result table (comparison result). 
     * Every row should have "TP", "FP" or "FN" as a value (meaning True Positive, False Positive and False Negative).
	*/
	List<String> perf;


    public perfTableNer() {
        source = new LinkedList<>();
        entity = new LinkedList<>();
        perf = new LinkedList<>();
    }

    public perfTableNer(List<String> source, List<CustomEntityMention> entity, List<String> perf) {
        this.source = source;
        this.entity = entity;
        this.perf = perf;
    }

    public List<String> getSource() {
        return this.source;
    }

    public void setSource(List<String> source) {
        this.source = source;
    }

    public List<CustomEntityMention> getEntity() {
        return this.entity;
    }

    public void setEntity(List<CustomEntityMention> entity) {
        this.entity = entity;
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
            ", entity='" + getEntity() + "'" +
            ", perf='" + getPerf() + "'" +
            "}";
    }

    public void add(String src, CustomEntityMention cem, String eval){
        source.add(src);
        entity.add(cem);
        perf.add(eval);
    }

    public void display() {
        for (int i = 0; i < source.size(); i++){
			System.out.println("source : " + source.get(i) + " |\t entity : " + entity.get(i).text() + "\t| perf : " + perf.get(i));
		}
    }
}
