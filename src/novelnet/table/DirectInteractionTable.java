package novelnet.table;

import java.util.List;

public class DirectInteractionTable extends InteractionTable {

    public DirectInteractionTable() {
        super();
    }

    public List<String> getSubjects() {
        return this.listCharA;
    }

    public void setSubjects(List<String> subjects) {
        this.listCharA = subjects;
    }

    public List<String> getObjects() {
        return this.listCharB;
    }

    public void setObjects(List<String> objects) {
        this.listCharB = objects;
    }

    public List<Integer> getSentenceIndex() {
        return this.listBeginingWindow;
    }

    public void setSentenceIndex(List<Integer> sentenceIndexs) {
        this.listBeginingWindow = sentenceIndexs;
        this.listEndingWindow = sentenceIndexs;
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
        add(subject, object, 1, 1, sentenceIndex, sentenceIndex, null);
	}

    /**
	 * Adds one line to the table specifying the type of interaction
	 * 
	 * @param subject a String representing the name of the subject
	 * @param object a String representing the name of the object
	 * @param sentenceIndex an Integer representing the sentence position of the triplet in the text.
	 * 
	 */
	public void add(String subject, String object, int sentenceIndex, String type){
		add(subject, object, 1, 1, sentenceIndex, sentenceIndex, type);
	}

    /**
	 * Creates a sub table of the current table.
	 * 
	 * @param begin an Integer for the index of the line begining the sub table.
	 * @param end an Integer for the index of the line ending the sub table.
	 * @return The sub table from line begin (included) to line end (included).
	 */
    @Override
	public DirectInteractionTable subTable(int begin, int end){
		DirectInteractionTable result = new DirectInteractionTable();
		for (int i = begin; i <= end; i++){
			if (i == listCharA.size()) return result;
			result.add(listCharA.get(i), listCharB.get(i), listBeginingWindow.get(i), getListType().get(i));
		}
		return result;
	}

    @Override
    public String toString() {
        return "{" +
            " subject='" + getSubjects() + "'" +
            ", object='" + getObjects() + "'" +
            ", weight='" + getSentenceIndex() + "'" +
            ", type='" + getListType() + "'" +
            "}";
    }

    @Override
    public void display(){
        if (!getListCharA().isEmpty()){
            System.out.print("Subject");
            if (getListType().get(0) != null) System.out.print("\t| Type");
            System.out.println("\t| Object | Sentence Index");
            for (int i = 0; i < listCharA.size(); i++){
                System.out.print(listCharA.get(i));
                if (getListType().get(i) != null) System.out.print("\t| "+getListType().get(i));
                System.out.println("\t| "+listCharB.get(i) + "\t| "+listBeginingWindow.get(i));
            }
        }
    }
}
