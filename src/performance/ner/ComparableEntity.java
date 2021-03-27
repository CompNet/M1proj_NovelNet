package performance.ner;

import edu.stanford.nlp.pipeline.CoreEntityMention;
import performance.coref.ComparableCorefChain;
import performance.coref.CorefChainContainer;

public class ComparableEntity{
	protected String text;
	protected int sentenceNumber;
	protected int tokenNumberFirst;
	protected int tokenNumberLast;

	public ComparableEntity(CoreEntityMention cem){
		this.text = cem.text();
		this.sentenceNumber = cem.tokens().get(0).sentIndex();
		this.tokenNumberFirst = cem.tokens().get(0).index();
		this.tokenNumberLast = cem.tokens().get(cem.tokens().size()-1).index();
	}
	
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

	public void setTokenNumberLast(int tokenNumberLast) {
		this.tokenNumberLast = tokenNumberLast;
	}

	public boolean compareTo(ComparableEntity ce){
		return this.sentenceNumber == ce.sentenceNumber && this.tokenNumberFirst == ce.tokenNumberFirst && this.tokenNumberLast == ce.tokenNumberLast;
	}

	@Override
	public String toString() {
		return "[Text = " + text + "\t | Sentence = " + sentenceNumber + "\t | Start = " + tokenNumberFirst + "\t | End = " + tokenNumberLast + "]";
	}

    public double precision(CorefChainContainer reference, ComparableCorefChain originChain) {
		double result = 0;
		for (ComparableCorefChain cccRef : reference.getCorefChains()){
			if (cccRef.getEntities().contains(this)){
				for (int i = 0; i < cccRef.getEntities().size();i++){
					if (originChain.getEntities().contains(cccRef.getEntities().get(i))){
						result++;
					}
					if (i == cccRef.getEntities().size()-1){
						result = result/originChain.getEntities().size();
					}
				}
			}
		}
		return result;
    }
}
