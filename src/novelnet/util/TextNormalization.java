package novelnet.util;


/**
 * Normalize the text to add a dot at the end of each line where there is no dot or some type of quotation mark
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class TextNormalization {
    
    /**
     * Class Constructor
	*/
    private TextNormalization(){
    }

    /**
     * add a dot at the end of each line where there is no dot or some type of quotation mark
	*/
    public static String addDotEndOfLine(String originalText){
        StringBuilder text = new StringBuilder(originalText);
        for(int i = 0; i < text.length()-1; i++){
            // if the current char is EOL AND we are not in the 2 first char AND the previous char IS NOT EOL OR a dot OR some type
            // of closing quotation mark
            if (text.charAt(i) == '\n' && i >2 && !(text.charAt(i-2) == '\n' || text.charAt(i-2) == '.' ||
                text.charAt(i-2) == '?' || text.charAt(i-2) == '!' || text.charAt(i-2) == '"' || text.charAt(i-2) == ':'))
            {
                System.out.println(text.charAt(i-2));
                text.insert(i,'.'); //we add a dot to the end of the previous line
            }
        }
        return text.toString();
    }
}