package implementation;

import java.util.Arrays;



public class Tableau {
	protected String characterA;
	protected String characterB;
	protected int distance;
	protected int i;
	protected Object [][] tab = new Object[1000][3];
	public Tableau() {
		this.characterA = this.characterB = null;
		this.distance = this.i = 0;
	}
	
	public void setTableau(String characterA, String characterB, int distance, int i){
		tab[i][0] = characterA;
		tab[i][1] = characterB;
		tab[i][2] = distance;
	}
	
	public void display(int i)
	{
		for(int i2 = 0; i2 < i; i2++){
			System.out.println(Arrays.toString(tab[i2]));
		}	                  
	}
}
