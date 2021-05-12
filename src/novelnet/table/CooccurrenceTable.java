package novelnet.table;


/**
 * A generic abstract class wich represent a table where all the co-occurrences are stocked.
 * Each list represent a column of the table.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public abstract class CooccurrenceTable extends InteractionTable {
	
	/**
	 * Class Constructor
	*/
	protected CooccurrenceTable(){
		super();
	}
    
	/**
	 * Create a subtable taking the lines from begin (included) to end (included)
	 * 
	 * @param begin the first line (included)
	 * @param end the last last (included)
	*/
    @Override
    public CooccurrenceTable subTable(int begin, int end){
		CooccurrenceTable result = new CooccurrenceTableSentence();
		for (int i = begin; i <= end; i++){
			result.add(listCharA.get(i), listCharB.get(i), listDistanceChar.get(i), listDistanceWord.get(i), listBeginingWindow.get(i), listEndingWindow.get(i), null);
		}
		return result;
    }

	/**
	 * add a line to the table
	*/
	public void add(String charA, String charB, int distanceChar, int distanceWord, int beginingIndex, int endingIndex){
		add(charA, charB, distanceChar, distanceWord, beginingIndex, endingIndex, null);
	}
	
}
