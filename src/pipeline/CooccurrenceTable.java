package pipeline;

import java.util.LinkedList;
import java.util.List;

public abstract class CooccurrenceTable {
	protected int windowSize;
	protected List<String> listCharA;
	protected List<String> listCharB;
	protected List<Integer> listDistanceChar;
	protected List<Integer> listDistanceWord;
	protected List<Integer> listBeginingWindow;
	protected List<Integer> listEndingWindow;

	protected CooccurrenceTable(){
		listCharA = new LinkedList<>();
		listCharB = new LinkedList<>();
		listDistanceChar = new LinkedList<>();
		listDistanceWord = new LinkedList<>();
		listBeginingWindow = new LinkedList<>();
		listEndingWindow = new LinkedList<>();
	}
	
	/**
	 * Adds one line to the table
	 * 
	 * @param String for character A
	 * @param String for character B
	 * @param int for distance in characters
	 * @param int for distance in words
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

	public CooccurrenceTable subTable(int begin, int end){
		CooccurrenceTable result = new CooccurrenceTableSentence();
		for (int i = begin; i <= end; i++){
			if (i == listCharA.size()) return result;
			result.add(listCharA.get(i), listCharB.get(i), listDistanceChar.get(i), listDistanceWord.get(i), listBeginingWindow.get(i), listEndingWindow.get(i));
		}
		return result;
	}
	
	/**
	 * Displays the table
	 * 
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
