package pipeline;

/**
 * A class wich represent a table where all the co-occurrences are stocked.
 * Each list represent a column of the table.
 * The indexes of begining and ending of windows are in Pargraphs.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
 */
public class CooccurrenceTableParagraph extends CooccurrenceTable {

    /**
	 * Class Constructor
	 * 
	 */
	public CooccurrenceTableParagraph() {
        super();
    }
    
    /**
	 * Displays the table with some layouts
	 * 
	 */
    @Override
	public void display(){
		for (int i = 0; i < listCharA.size(); i++){ // For the size of the list of character A
			System.out.print("Character A : "+listCharA.get(i)); // Prints the list of character A
			System.out.print(" | Character B : "+listCharB.get(i)); // Prints the list of character B   
			System.out.print(" | Distance characters : "+listDistanceChar.get(i)); // Prints the list of distance in characters      
			System.out.print(" | Distance words : "+listDistanceWord.get(i)); // Prints the list of distance in words 
			System.out.print(" | Paragraph begining the window : "+listBeginingWindow.get(i)); 
			System.out.println(" | Paragraph ending the window : "+listEndingWindow.get(i));
		}
	}
    
}
