package table;

import java.util.LinkedList;
import java.util.List;

public class DirectInteractionTable {
    
    /**
	 * a List of String representing the name of the subject
	 */
	protected List<String> subject;
	/**
	 * a List of String representing the name of the object
	 */
	protected List<String> object;
    /**
	 * Position of the sentence of the interaction in the text. 
	 */
	protected List<Integer> sentenceIndex;

    protected List<String> type;


    public DirectInteractionTable() {
        this.subject = new LinkedList<>();
        this.object = new LinkedList<>();
        this.sentenceIndex = new LinkedList<>();
        this.type = new LinkedList<>();
    }

    public DirectInteractionTable(List<String> subject, List<String> object, List<Integer> sentenceIndex, List<String> type) {
        this.subject = subject;
        this.object = object;
        this.sentenceIndex = sentenceIndex;
        this.type = type;
    }

    public List<String> getSubject() {
        return this.subject;
    }

    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    public List<String> getObject() {
        return this.object;
    }

    public void setObject(List<String> object) {
        this.object = object;
    }

    public List<Integer> getSentenceIndex() {
        return this.sentenceIndex;
    }

    public void setSentenceIndex(List<Integer> sentenceIndex) {
        this.sentenceIndex = sentenceIndex;
    }

    @Override
    public String toString() {
        return "{" +
            " subject='" + getSubject() + "'" +
            ", object='" + getObject() + "'" +
            ", weight='" + getSentenceIndex() + "'" +
            "}";
    }

    /**
	 * Adds one line to the table
	 * 
	 * @param subject a String representing the name of the subject
	 * @param object a String representing the name of the object
	 * @param sentenceIndex an Integer representing the sentence position of the triplet in the text.
	 * 
	 */
	public void add(String subject, String object, int sentenceIndex){
		this.subject.add(subject); // Adds to the list of subject A, the subject A given
		this.object.add(object); // Adds to the list of subject B, the subject B given
        this.sentenceIndex.add(sentenceIndex);
        this.type.add(null);
	}

    /**
	 * Adds one line to the table
	 * 
	 * @param subject a String representing the name of the subject
	 * @param object a String representing the name of the object
	 * @param sentenceIndex an Integer representing the sentence position of the triplet in the text.
	 * 
	 */
	public void add(String subject, String object, int sentenceIndex, String type){
		this.subject.add(subject); // Adds to the list of subject A, the subject A given
		this.object.add(object); // Adds to the list of subject B, the subject B given
        this.sentenceIndex.add(sentenceIndex);
        this.type.add(type);
	}

    /**
	 * Creates a sub table of the current table.
	 * 
	 * @param begin an Integer for the index of the line begining the sub table.
	 * @param end an Integer for the index of the line ending the sub table.
	 * @return The sub table from line begin (included) to line end (included).
	 */
	public DirectInteractionTable subTable(int begin, int end){
		DirectInteractionTable result = new DirectInteractionTable();
		for (int i = begin; i <= end; i++){
			if (i == subject.size()) return result;
			result.add(subject.get(i), object.get(i), sentenceIndex.get(i), type.get(i));
		}
		return result;
	}

    public void display(){
        System.out.print("Subject");
        if (type.get(0) != null) System.out.print("\t| Type");
        System.out.println("\t| Object | Sentence Index");
        for (int i = 0; i < subject.size(); i++){
			System.out.print(subject.get(i));
			if (type.get(i) != null) System.out.print("\t| "+type.get(i));
			System.out.println("\t| "+object.get(i) + "\t| "+sentenceIndex.get(i));
		}
    }

    public List<String> getType() {
        return this.type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }
}
