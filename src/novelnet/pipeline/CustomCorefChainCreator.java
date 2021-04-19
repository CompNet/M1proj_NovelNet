package novelnet.pipeline;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;

import novelnet.util.CustomCorefChain;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;

public class CustomCorefChainCreator {
    
    private CustomCorefChainCreator(){

    }

    public static List<CustomCorefChain> makeCustomCorefChains(CoreDocument document) throws NullDocumentException{

        List<CustomCorefChain> cccList = new LinkedList<>();
        if (document.corefChains() != null){
            for(CorefChain cc : document.corefChains().values()){
                cccList.add(new CustomCorefChain(cc));
            }
        }
        
        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            if (cem.text() != "he" && cem.text() != "he " && cem.text() != "him" && cem.text() != "his" && cem.text() != "him " && cem.text() != "his "){
                cccList.add(new CustomCorefChain(cem));
            }
            
        }
        return cccList;
    }
}
