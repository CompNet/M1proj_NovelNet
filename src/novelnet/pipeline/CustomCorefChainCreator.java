package novelnet.pipeline;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;

import novelnet.util.CustomCorefChain;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;

/**
 * Creates CustomCorefChains from stanford's CoreDocument
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class CustomCorefChainCreator {
    
    /**
     * Class Constructor 
    */
    private CustomCorefChainCreator(){

    }

    /**
     * transform all the corefChains from a Stanford CoreDocument into our CustomCorefChains, also take all the single mentions to put them into a CustomCorefChain.
     * 
     * @param document Stanford's core Document from wich you want to make the corefChains
     * @return a list of all the corefChains
    */
    public static List<CustomCorefChain> makeCustomCorefChains(CoreDocument document) throws NullDocumentException{

        List<CustomCorefChain> result = new LinkedList<>();

        //transforming corefChains into CustomCorefChains
        if (document.corefChains() != null){
            for(CorefChain cc : document.corefChains().values()){
                result.add(new CustomCorefChain(cc));
            }
        }
        
        //puting single mentions into CustomCorefChains
        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            if (cem.text() != "he" && cem.text() != "he " && cem.text() != "him" && cem.text() != "his" && cem.text() != "him " && cem.text() != "his "){
                result.add(new CustomCorefChain(cem));
            }
            
        }
        return result;
    }
}
