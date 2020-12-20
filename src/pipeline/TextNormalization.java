package pipeline;


/**
 * Normalize the text to add a dot at the end of each line where there is no dot or some type of quotation mark
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class TextNormalization {
    StringBuilder text;

    /**
     * Constructor
     * 
	 * @param text text to normalize
	*/
    TextNormalization(String text){
        this.text = new StringBuilder(text);
    }

    /**
     * add a dot add a dot at the end of each line where there is no dot or some type of quotation mark
	*/
    public void addDotEndOfLine(){
        for(int i = 0; i < text.length()-1; i++){
            // if the current char is EOL AND we are not in the 2 first char AND the previous char IS NOT EOL OR a dot OR some type of closing quotation mark
            if (text.charAt(i) == '\n' && i >1 && !(text.charAt(i-2) == '\n' || text.charAt(i-2) == '.' || text.charAt(i-2) == '?' || text.charAt(i-2) == '!' || text.charAt(i-2) == '»' || text.charAt(i-2) == '”'|| text.charAt(i-2) == '"')){
                text.insert(i-1,'.'); //we add a dot to the end of the previous line
            }
        }
    }

    public String getText(){
        return text.toString();
    }
}