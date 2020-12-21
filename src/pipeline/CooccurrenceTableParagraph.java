package pipeline;

public class CooccurrenceTableParagraph extends CooccurrenceTable {

    protected Book book;


     /**
	 * Constructor for the class WindowingCooccurrenceSentence
	 * 
	 */
	public CooccurrenceTableParagraph() {
        super();
    }

    /**
	 * Constructor for the class WindowingCooccurrenceSentence
	 * 
	 * @param windowSize
	 * @param book
	 */
	public CooccurrenceTableParagraph(int windowSize, Book book) {
        super();
		this.windowSize = windowSize;
		this.book = book;
    }
	
	public CooccurrenceTableParagraph createCustom(){
		CooccurrenceTableParagraph ctp = new CooccurrenceTableParagraph();
		ctp.add("charA", "charB", 10, 3, 0, 4);
		ctp.add("charA", "charB", 15, 4, 0, 4);
		ctp.add("charC", "charB", 12, 3, 0, 4);
		ctp.add("charC", "charA", 20, 6, 4, 8);
		ctp.add("charB", "charA", 10, 3, 4, 8);
		ctp.add("charB", "charC", 15, 4, 8, 12);
		ctp.add("charB", "charC", 20, 5, 16, 20);
		ctp.add("charC", "charA", 20, 5, 20, 24);
		return ctp;
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
			System.out.print(" | Paragraph begining the window : "+listBeginingWindow.get(i)); 
			System.out.println(" | Paragraph ending the window : "+listEndingWindow.get(i));
		}
	}
    
}
