package performance.coref;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;

public class CorefChainContainer {

    List<ComparableCorefChain> corefChains;

    public CorefChainContainer() {
        corefChains = new LinkedList<>();
    }

    public CorefChainContainer(CoreDocument stanfordCoreDocument) throws NullDocumentException{
        corefChains = new LinkedList<>();

        for(CorefChain cc : stanfordCoreDocument.corefChains().values()){
            corefChains.add(new ComparableCorefChain(cc));
        }

        ImpUtils.setDocument(stanfordCoreDocument);
        for(CoreEntityMention cem : ImpUtils.getCoreEntityMentionsWithoutCorefChain()){
            corefChains.add(new ComparableCorefChain(cem));
        }
    }

    public List<ComparableCorefChain> getCorefChains() {
        return this.corefChains;
    }

    public void setCorefChains(List<ComparableCorefChain> corefChains) {
        this.corefChains = corefChains;
    }
}
