package performance.ner;

public class ComparableEntity{
	protected String text;
	protected int sentenceNumber;
	protected int tokenNumberFirst;
	protected int tokenNumberLast;
	
	public ComparableEntity(String text, int sentenceNumber, int tokenNumberFirst, int tokenNumberLast) {
		this.text = text;
		this.sentenceNumber = sentenceNumber;
		this.tokenNumberFirst = tokenNumberFirst;
		this.tokenNumberLast = tokenNumberLast;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSentenceNumber() {
		return sentenceNumber;
	}

	public void setSentenceNumber(int sentenceNumber) {
		this.sentenceNumber = sentenceNumber;
	}

	public int getTokenNumberFirst() {
		return tokenNumberFirst;
	}

	public void setTokenNumberFirst(int tokenNumberFirst) {
		this.tokenNumberFirst = tokenNumberFirst;
	}

	public int getTokenNumberLast() {
		return tokenNumberLast;
	}

	public boolean compareTo(ComparableEntity ce){
		return this.sentenceNumber == ce.sentenceNumber && this.tokenNumberFirst == ce.tokenNumberFirst && this.tokenNumberLast == ce.tokenNumberLast;
	}

	public void setTag(int tokenNumberLast) {
		this.tokenNumberLast = tokenNumberLast;
	}

	@Override
	public String toString() {
		return "[Text = " + text + "\t | Sentence = " + sentenceNumber + "\t | Start = " + tokenNumberFirst + "\t | End = " + tokenNumberLast + "]";
	}
}
