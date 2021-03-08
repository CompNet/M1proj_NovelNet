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

	public void add(String charA, String charB, int distanceChar, int distanceWord, int beginingIndex, int endingIndex){
		add(charA, charB, distanceChar, distanceWord, beginingIndex, endingIndex, null);
	}
	
}
