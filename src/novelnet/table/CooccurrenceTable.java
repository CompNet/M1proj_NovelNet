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
	 *
	*/
	protected CooccurrenceTable(){
		super();
	}
    
    @Override
    public CooccurrenceTable subTable(int begin, int end){
		CooccurrenceTable result = new CooccurrenceTableSentence();
		for (int i = begin; i <= end; i++){
			result.add(listCharA.get(i), listCharB.get(i), listDistanceChar.get(i), listDistanceWord.get(i), listBeginingWindow.get(i), listEndingWindow.get(i), null);
		}
		return result;
    }

	public void add(String charA, String charB, int distanceChar, int distanceWord, int beginingIndex, int endingIndex){
		add(charA, charB, distanceChar, distanceWord, beginingIndex, endingIndex, null);
	}
	
}
