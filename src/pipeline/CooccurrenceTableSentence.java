package pipeline;

/**
 * A class wich represent a table where all the co-occurrences are stocked.
 * Each list represent a column of the table.
 * The indexes of begining and ending of windows are in Sentences.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
 */
public class CooccurrenceTableSentence extends CooccurrenceTable {

     /**
	 * Constructor for the class WindowingCooccurrenceSentence.
	 * 
	 */
	public CooccurrenceTableSentence() {
        super();
    }

    /**
	 * Constructor for the class WindowingCooccurrenceSentence specifying the window's size.
	 * 
	 * @param windowSize
	 */
	public CooccurrenceTableSentence(int windowSize) {
        super();
		this.windowSize = windowSize;
    }
    
    @Override
    public CooccurrenceTable subTable(int begin, int end){
		CooccurrenceTable result = new CooccurrenceTableSentence();
		for (int i = begin; i <= end; i++){
			result.add(listCharA.get(i), listCharB.get(i), listDistanceChar.get(i), listDistanceWord.get(i), listBeginingWindow.get(i), listEndingWindow.get(i));
		}
		return result;
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
