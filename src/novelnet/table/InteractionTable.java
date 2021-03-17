package novelnet.table;

import java.util.LinkedList;
import java.util.List;

public abstract class InteractionTable {

    /**
	 * An Integer representing the size of the co-occurrence window.
	 */
	protected int windowSize;
	/**
	 * a List of String representing the name of Character A
	 */
	protected List<String> listCharA;
	/**
	 * a List of String representing the name of Character B
	 */
	protected List<String> listCharB;
	/**
	 * a List of Integer representing the distance between the 2 Characters in characters (letters)
	 */
	protected List<Integer> listDistanceChar;
	/**
	 * a List of Integer representing the distance between the 2 Characters in words
	 */
	protected List<Integer> listDistanceWord;
	/**
	 * a List of Integer representing the begining index of the window where the co-occurrence happened (in Sentences for TableSentence and Paragraphs for TableParagraphs)
	 */
	protected List<Integer> listBeginingWindow;
	/**
	 * a List of Integer representing the begining index of the window where the co-occurrence happened (in Sentences for TableSentence and Paragraphs for TableParagraphs)
	 */
	protected List<Integer> listEndingWindow;

    /**
	 * a List of Strings representing the type of the interaction (for example a triplet S-V-O can be typed as opposition or cooperation)
	 */
    protected List<String> listType;

    /**
	 * Class Constructor
	 *
	*/
	protected InteractionTable(){
		listCharA = new LinkedList<>();
		listCharB = new LinkedList<>();
		listDistanceChar = new LinkedList<>();
		listDistanceWord = new LinkedList<>();
		listBeginingWindow = new LinkedList<>();
		listEndingWindow = new LinkedList<>();
		listType = new LinkedList<>();
	}

	public List<String> getListCharA() {
		return this.listCharA;
	}

	public void setListCharA(List<String> listCharA) {
		this.listCharA = listCharA;
	}

	public List<String> getListCharB() {
		return this.listCharB;
	}

	public void setListCharB(List<String> listCharB) {
		this.listCharB = listCharB;
	}

	public List<Integer> getListDistanceChar() {
		return this.listDistanceChar;
	}

	public void setListDistanceChar(List<Integer> listDistanceChar) {
		this.listDistanceChar = listDistanceChar;
	}

	public List<Integer> getListDistanceWord() {
		return this.listDistanceWord;
	}

	public void setListDistanceWord(List<Integer> listDistanceWord) {
		this.listDistanceWord = listDistanceWord;
	}

	public List<Integer> getListBeginingWindow() {
		return this.listBeginingWindow;
	}

	public void setListBeginingWindow(List<Integer> listBeginingWindow) {
		this.listBeginingWindow = listBeginingWindow;
	}

	public List<Integer> getListEndingWindow() {
		return this.listEndingWindow;
	}

	public void setListEndingWindow(List<Integer> listEndingWindow) {
		this.listEndingWindow = listEndingWindow;
	}

    public List<String> getListType() {
        return this.listType;
    }

    public void setListType(List<String> type) {
        this.listType = type;
    }

	@Override
	public String toString() {
		return "{" +
			", listCharA='" + getListCharA() + "'" +
			", listCharB='" + getListCharB() + "'" +
			", listDistanceChar='" + getListDistanceChar() + "'" +
			", listDistanceWord='" + getListDistanceWord() + "'" +
			", listBeginingWindow='" + getListBeginingWindow() + "'" +
			", listEndingWindow='" + getListEndingWindow() + "'" +
			", listType='" + getListType() + "'" +
			"}";
	}
	
	/**
	 * Adds one line to the table
	 * 
	 * @param charA a String representing the name of Character A
	 * @param charB a String representing the name of Character B
	 * @param distanceChar an Integer representing the distance between the 2 Characters in characters (letters)
	 * @param distanceWord an Integer representing the distance between the 2 Characters in words
	 * @param beginingIndex an Integer representing the begining index of the window where the co-occurrence happened (in Sentences for TableSentence and Paragraphs for TableParagraphs)
	 * @param endingIndex an Integer representing the ending index of the window where the co-occurrence happened (in Sentences for TableSentence and Paragraphs for TableParagraphs)
	 * 
	 */
	public void add(String charA, String charB, int distanceChar, int distanceWord, int beginingIndex, int endingIndex, String type){
		listCharA.add(charA); // Adds to the list of characters A, the character A given
		listCharB.add(charB); // Adds to the list of characters B, the character B given
		listDistanceChar.add(distanceChar); // Adds to the list of distance in characters, the distance in characters given
		listDistanceWord.add(distanceWord); // Adds to the list of distance in words, the distance in words given
		listBeginingWindow.add(beginingIndex); // Adds to the list of Window begining sentence number, the number of the sentence begining the window where the wharacters are
		listEndingWindow.add(endingIndex); // Adds to the list of Window begining sentence number, the number of the sentence begining the window where the wharacters are
        listType.add(type);
    }

	/**
	 * Creates a sub table of the current table.
	 * 
	 * @param begin an Integer for the index of the line begining the sub table.
	 * @param end an Integer for the index of the line ending the sub table.
	 * @return The sub table from line begin (included) to line end (included).
	 */
	public InteractionTable subTable(int begin, int end){
		CooccurrenceTable result = new CooccurrenceTableSentence();
		for (int i = begin; i <= end; i++){
			if (i == listCharA.size()) return result;
			result.add(listCharA.get(i), listCharB.get(i), listDistanceChar.get(i), listDistanceWord.get(i), listBeginingWindow.get(i), listEndingWindow.get(i), listType.get(i));
		}
		return result;
	}
	
	/**
	 * Displays the table with some layouts
	 * 
	 */
	public void display(){
		for (int i = 0; i < listCharA.size(); i++){ // For the size of the list of character A
			System.out.print("Char A : "+listCharA.get(i)); // Prints the list of character A
			System.out.print("\t| Char B : "+listCharB.get(i)); // Prints the list of character B   
			System.out.print("\t| Dist letter : "+listDistanceChar.get(i)); // Prints the list of distance in characters      
			System.out.print("\t| Dist words : "+listDistanceWord.get(i)); // Prints the list of distance in words 
			System.out.print(" | window begin : "+listBeginingWindow.get(i)); 
			System.out.print(" | window end : "+listEndingWindow.get(i));
            System.out.println(" | type : "+listType.get(i));
		}
	}
    
}
