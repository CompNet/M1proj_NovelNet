package performance;

public class Perf {
	float truePositives;
	float falsePositives;
	float trueNegatives;
	float falseNegatives;
	float p;
	float r;
	float f;
	
	public Perf() {
		this.truePositives = 0;
		this.falsePositives = 0;
		this.trueNegatives = 0;
		this.falseNegatives = 0;
		this.p = 0;
		this.r = 0;
		this.f = 0;
	}
	
	public void setTruePositives(float truePositives) {
		this.truePositives += truePositives;
	}
	
	public float getTruePositives() {
		return truePositives;
	}
	
	public void setFalsePositives(float falsePositives) {
		this.falsePositives += falsePositives;
	}
	
	public float getFalsePositives() {
		return falsePositives;
	}
	
	public void setTrueNegatives(float trueNegatives) {
		this.trueNegatives += trueNegatives;
	}
	
	public float getTrueNegatives() {
		return trueNegatives;
	}
	
	public void setFalseNegatives(float falseNegatives) {
		this.falseNegatives += falseNegatives;
	}
	
	public float getFalseNegatives() {
		return falseNegatives;
	}
	
	public void setAll(float truePositives, float falsePositives, float trueNegatives, float falseNegatives){
		this.truePositives += truePositives;
		this.trueNegatives += trueNegatives;
		this.falsePositives += falsePositives;
		this.falseNegatives += falseNegatives;
	}
	
	public void setMeasures(float truePositives, float falsePositives, float falseNegatives){
		this.truePositives += truePositives;
		this.falsePositives += falsePositives;
		this.falseNegatives += falseNegatives;
	}
	
	public void setPrecision() {
		this.p = (truePositives/(truePositives + falsePositives));
	}
	
	public float getPrecision() {
		return p;
	}
	
	public void setRecall() {
		this.r = (truePositives/(truePositives + falseNegatives));
	}
	
	public float getRecall() {
		return r;
	}
	
	public void setFMeasures() {
		this.f = (2 * ((p * r)/(p + r)));
	}
	
	public float getFmeasures() {
		return f;
	}
	
	public void clear(){
		this.truePositives = 0;
		this.falsePositives = 0;
		this.falseNegatives = 0;
		this.trueNegatives = 0;
		this.p = 0;
		this.r = 0;
		this.f = 0;
	}
}
