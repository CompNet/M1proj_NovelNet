package pipeline;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import util.CustomCorefChain;
import util.ImpUtils;

public class CustomCorefChainMaker {
    
    private CustomCorefChainMaker(){

    }

    public static List<CustomCorefChain> makeCustomCorefChains(CoreDocument document){

        List<CustomCorefChain> cccList = new LinkedList<>();
        try {
            for(CorefChain cc : document.corefChains().values()){
                cccList.add(new CustomCorefChain(cc));
            }
    
            for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
                cccList.add(new CustomCorefChain(cem));
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        return cccList;
    }
}
