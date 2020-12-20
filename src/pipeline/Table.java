package pipeline;

import java.util.LinkedList;
import java.util.List;

public class Table {
	protected int size;
	protected Book book;
	protected List<String> listPersA;
	protected List<String> listPersB;
	protected List<Integer> listDistanceChar;
	protected List<Integer> listDistanceWord;
	
	/**
	 * Constructor for the class WindowingCooccurrenceSentence
	 * 
	 * @param size
	 * @param book
	 */
	public Table(int size, Book book) {
		this.size = size;
		this.book = book;
		listPersA = new LinkedList<>();
		listPersB = new LinkedList<>();
		listDistanceChar = new LinkedList<>();
		listDistanceWord = new LinkedList<>();
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
	public void addLine(String charA, String charB, int distanceChar, int distanceWord){
		listPersA.add(charA); // Adds to the list of characters A, the character A given
		listPersB.add(charB); // Adds to the list of characters B, the character B given
		listDistanceChar.add(distanceChar); // Adds to the list of distance in characters, the distance in characters given
		listDistanceWord.add(distanceWord); // Adds to the list of distance in words, the distance in words given
	}
	
	/**
	 * Displays the table
	 * 
	 * 
	 */
	public void display(){
		for (int i = 0; i < listPersA.size(); i++){ // For the size of the list of character A
			System.out.print("Personage A : "+listPersA.get(i)); // Prints the list of character A
			System.out.print(" | Personage B : "+listPersB.get(i)); // Prints the list of character B   
			System.out.print(" | Distance Caractères : "+listDistanceChar.get(i)); // Prints the list of distance in characters      
			System.out.println(" | Distance Mots : "+listDistanceWord.get(i)); // Prints the list of distance in words      
		}
	}
}
