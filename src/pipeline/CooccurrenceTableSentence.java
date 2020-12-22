package pipeline;

public class CooccurrenceTableSentence extends CooccurrenceTable {

     /**
	 * Constructor for the class WindowingCooccurrenceSentence
	 * 
	 */
	public CooccurrenceTableSentence() {
        super();
    }

    /**
	 * Constructor for the class WindowingCooccurrenceSentence
	 * 
	 * @param windowSize
	 */
	public CooccurrenceTableSentence(int windowSize) {
        super();
		this.windowSize = windowSize;
    }
	
	public void createCustom(){
		add("charA", "charB", 10, 3, 0, 4);
		add("charA", "charB", 15, 4, 0, 4);
		add("charC", "charB", 12, 3, 0, 4);
		add("charC", "charA", 20, 6, 4, 8);
		add("charB", "charA", 10, 3, 4, 8);
		add("charB", "charC", 15, 4, 8, 12);
		add("charB", "charC", 20, 5, 16, 20);
		add("charC", "charA", 20, 5, 20, 24);
	}
    
    /**
	 * Displays the table
	 * 
	 * 
	 */
    @Override
	public void display(){
		for (int i = 0; i < listCharA.size(); i++){ // For the size of the list of character A
			System.out.print("Character A : "+listCharA.get(i)); // Prints the list of character A
			System.out.print(" | Character B : "+listCharB.get(i)); // Prints the list of character B   
			System.out.print(" | Distance characters : "+listDistanceChar.get(i)); // Prints the list of distance in characters      
			System.out.print(" | Distance words : "+listDistanceWord.get(i)); // Prints the list of distance in words 
			System.out.print(" | Sentence begining the window : "+listBeginingWindow.get(i)); 
			System.out.println(" | Sentence ending the window : "+listEndingWindow.get(i));
		}
	}
    
}
