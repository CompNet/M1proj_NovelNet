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
    
    @Override
    public CooccurrenceTable subTable(int begin, int end){
		CooccurrenceTable result = new CooccurrenceTableParagraph();
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
			System.out.print(" | Paragraph begining the window : "+listBeginingWindow.get(i)); 
			System.out.println(" | Paragraph ending the window : "+listEndingWindow.get(i));
		}
	}
    
}
