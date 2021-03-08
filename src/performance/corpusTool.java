package performance;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;


public class corpusTool {
	List<corpusTool> infos = new LinkedList<>();
	protected String word;
	protected int sentence;
	protected int wordPosition;
	
	public corpusTool() {
		
	}
	
	public corpusTool(String word, int sentence, int wordPosition) {
		this.word = word;
		this.sentence = sentence;
		this.wordPosition = wordPosition;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getSentence() {
		return sentence;
	}

	public void setSentence(int sentence) {
		this.sentence = sentence;
	}

	public int getWordPosition() {
		return wordPosition;
	}

	public void setWordPosition(int wordPosition) {
		this.wordPosition = wordPosition;
	}

	public void createTab() throws IOException {
		String wordInfo;
		StringBuffer sb = new StringBuffer();
		String[] wordArray;
		int wordCount = 0;
		int sentCount = 1;
		Scanner sc = new Scanner(System.in);
		System.out.println("Saisir chemin du fichier:");
		String path = sc.nextLine();
		FileInputStream is = new FileInputStream(path);     
		String content = IOUtils.toString(is, "UTF-8");
		content = content.replace("?", ".");
		content = content.replace("!", ".");
		content = content.replace("\n\n", " ");
		content = content.replace("(?m)^[ \t]*\r?\n", " ");
		content += " ";
		while (!(content.isEmpty())) {
			wordArray = content.split(" " , 2);
			sb.append(wordArray[0]);
			wordInfo = sb.toString();
			content = content.replaceFirst(wordInfo + " ", "");
			sb.delete(0, wordInfo.length());
			wordCount += 1;
			infos.add(new corpusTool(wordInfo, sentCount, wordCount));
			if (wordInfo.contains(".") || wordInfo.contains(". \"")) {
				wordCount = 0;
				sentCount += 1;
			}
			if (wordInfo.contains(",") || wordInfo.contains("'")) wordCount += 1;
		}
		sc.close();
	}
	
	@Override
	public String toString() {
		return "[Text = " + word + "\t | Sentence = " + sentence + "\t | Position = " + wordPosition + "]";
	}
	
	public void display(){
		for (int i = 0; i < infos.size(); i++){
			System.out.println(infos.get(i).toString()); 
		}
	}
	

	public static void main(String[] args) throws IOException {
		corpusTool c = new corpusTool();
		c.createTab(); //res/corpus/reference.txt
		c.display();
	}
}
