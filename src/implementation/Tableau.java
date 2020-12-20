package implementation;

import java.util.LinkedList;
import java.util.List;

public class Tableau {
	protected int size;
	protected Book book;
	protected List<String> listPersA;
	protected List<String> listPersB;
	protected List<Integer> listDistanceChar;
	protected List<Integer> listDistanceMot;
	
	public Tableau(int size, Book book) {
		this.size = size;
		this.book = book;
		listPersA = new LinkedList<>();
		listPersB = new LinkedList<>();
		listDistanceChar = new LinkedList<>();
		listDistanceMot = new LinkedList<>();
	}
	
	public void addLign(String charA, String charB, int distanceChar, int distanceMot){
		listPersA.add(charA);
		listPersB.add(charB);
		listDistanceChar.add(distanceChar);
		listDistanceMot.add(distanceMot);
	}
	
	public void display(){
		for (int i = 0; i < listPersA.size(); i++){
			System.out.print("Personage A : "+listPersA.get(i));
			System.out.print(" | Personage B : "+listPersB.get(i));     
			System.out.print(" | Distance Caractères : "+listDistanceChar.get(i));     
			System.out.println(" | Distance Mots : "+listDistanceMot.get(i));     
		}
	}
}
