package implementation;


/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class TextNormalization {
    StringBuilder text;

    TextNormalization(String text){
        this.text = new StringBuilder(text);
    }

    public void addDotEndOfLine(){
        for(int i = 0; i < text.length()-1; i++){
            // Si on a un char EOL ET on est pas dans les 2 premier char ET le char précédent n'est pas un EOL ou un .
            if (text.charAt(i) == '\n' && i >1 && !(text.charAt(i-2) == '\n' || text.charAt(i-2) == '.')){
                text.insert(i-1,'.'); //on ajoute un point a la fin de la ligne. 
            }
        }
    }

    public String getText(){
        return text.toString();
    }
}