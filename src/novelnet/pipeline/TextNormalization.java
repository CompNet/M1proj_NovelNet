package novelnet.pipeline;


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
        int shift = 2;  // shift might be different we don't know why but it's either 1 or 2
                        // if you have problems of sentences with only a dot change it for the other one.
        StringBuilder text = new StringBuilder(originalText);
        for(int i = 0; i < text.length()-1; i++){
            // if the current char is EOL AND we are not in the 2 first char AND the previous char IS NOT EOL OR a dot OR some type
            // of closing quotation mark
            if (text.charAt(i) == '\n' && i >shift && !((int)text.charAt(i-shift) == 10 || (int)text.charAt(i-shift) == 13 || text.charAt(i-shift) == ' ' || text.charAt(i-shift) == '.' ||
                text.charAt(i-shift) == '?' || text.charAt(i-shift) == '!' || text.charAt(i-shift) == '"' || text.charAt(i-shift) == ':'))
            {
                System.out.println("i-1 :" + text.charAt(i-1));
                System.out.println("i-2 :" + text.charAt(i-2));

                text.insert(i,'.'); //we add a dot to the end of the previous line
            }
        }
        return text.toString();
    }
}