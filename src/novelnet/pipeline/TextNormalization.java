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

    public static String preProcess(String originalText, String language){
        String result = addDotEndOfLine(originalText);

        if (language.equals("fr")){
            result = replaceArticle(result);
        }

        return result;
    }

    /**
     * add a dot at the end of each line where there is no dot or some type of quotation mark
	*/
    public static String addDotEndOfLine(String originalText){
        int shift = 1;  // shift might be different we don't know why but it's either 1 or 2
                        // if you have problems of sentences with only a dot change it for the other one.
        StringBuilder text = new StringBuilder(originalText);
        for(int i = 0; i < text.length()-1; i++){
            // if the current char is EOL AND we are not in the 2 first char AND the previous char IS NOT EOL OR a dot OR some type
            // of closing quotation mark
            if (text.charAt(i) == '\n' && i >shift && !((int)text.charAt(i-shift) == 10 || (int)text.charAt(i-shift) == 13 || text.charAt(i-shift) == ' ' || text.charAt(i-shift) == '.' ||
                text.charAt(i-shift) == '?' || text.charAt(i-shift) == '!' || text.charAt(i-shift) == '"' || text.charAt(i-shift) == ':'))
            {
                text.insert(i,'.'); //we add a dot to the end of the previous line
            }
        }

        return text.toString();
    }

    public static String replaceArticle(String originalText){
        String result = originalText.replaceAll("\\s+(des)\\s+", " de les ");
        result = result.replaceAll("\\s+(Des)\\s+", " De les ");

        result = result.replaceAll("\\s+(du)\\s+", " de le ");
        result = result.replaceAll("\\s+(Du)\\s+", " De le ");

        result = result.replaceAll("\\s+(aux)\\s+", " a les ");
        result = result.replaceAll("\\s+(Aux)\\s+", " A les ");

        result = result.replaceAll("\\s+(au)\\s+", " a le ");
        result = result.replaceAll("('au)\\s+", "'a le ");
        result = result.replaceAll("\\s+(Au)\\s+", " A le ");
        
        return result;
    }
}