package pipeline;

import java.util.LinkedList;
import java.util.List;

/**
 * A generic abstract class wich represent a table where all the co-occurrences are stocked.
 * Each list represent a column of the table.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
 */
public abstract class CooccurrenceTable {

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
	 * The constructor of a CooccurrenceTable
	 *
	 */
	protected CooccurrenceTable(){
		listCharA = new LinkedList<>();
		listCharB = new LinkedList<>();
		listDistanceChar = new LinkedList<>();
		listDistanceWord = new LinkedList<>();
		listBeginingWindow = new LinkedList<>();
		listEndingWindow = new LinkedList<>();
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

	@Override
	public String toString() {
		return "{" +
			", listCharA='" + getListCharA() + "'" +
			", listCharB='" + getListCharB() + "'" +
			", listDistanceChar='" + getListDistanceChar() + "'" +
			", listDistanceWord='" + getListDistanceWord() + "'" +
			", listBeginingWindow='" + getListBeginingWindow() + "'" +
			", listEndingWindow='" + getListEndingWindow() + "'" +
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
	public void add(String charA, String charB, int distanceChar, int distanceWord, int beginingIndex, int endingIndex){
		listCharA.add(charA); // Adds to the list of characters A, the character A given
		listCharB.add(charB); // Adds to the list of characters B, the character B given
		listDistanceChar.add(distanceChar); // Adds to the list of distance in characters, the distance in characters given
		listDistanceWord.add(distanceWord); // Adds to the list of distance in words, the distance in words given
		listBeginingWindow.add(beginingIndex); // Adds to the list of Window begining sentence number, the number of the sentence begining the window where the wharacters are
		listEndingWindow.add(endingIndex); // Adds to the list of Window begining sentence number, the number of the sentence begining the window where the wharacters are
	}

	/**
	 * Creates a sub table of the current table.
	 * 
	 * @param begin an Integer for the index of the line begining the sub table.
	 * @param end an Integer for the index of the line ending the sub table.
	 * @return The sub table from line begin (included) to line end (included).
	 */
	public CooccurrenceTable subTable(int begin, int end){
		CooccurrenceTable result = new CooccurrenceTableSentence();
		for (int i = begin; i <= end; i++){
			if (i == listCharA.size()) return result;
			result.add(listCharA.get(i), listCharB.get(i), listDistanceChar.get(i), listDistanceWord.get(i), listBeginingWindow.get(i), listEndingWindow.get(i));
		}
		return result;
	}
	
	/**
	 * Displays the table with some layouts
	 * 
	 */
	public void display(){
		for (int i = 0; i < listCharA.size(); i++){ // For the size of the list of character A
			System.out.print("Character A : "+listCharA.get(i)); // Prints the list of character A
			System.out.print(" | Character B : "+listCharB.get(i)); // Prints the list of character B   
			System.out.print(" | Distance characters : "+listDistanceChar.get(i)); // Prints the list of distance in characters      
			System.out.print(" | Distance words : "+listDistanceWord.get(i)); // Prints the list of distance in words 
			System.out.print(" | index begining the window : "+listBeginingWindow.get(i)); 
			System.out.println(" | index ending the window : "+listEndingWindow.get(i));
		}
	}
}
