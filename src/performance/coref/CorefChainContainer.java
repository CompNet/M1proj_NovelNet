package performance.coref;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;
import performance.ner.ComparableEntity;

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

    @Override
    public String toString() {
        return "{" +
            " corefChains='" + getCorefChains() + "'" +
            "}";
    }
    
    public void display(){
        for (ComparableCorefChain ccc : corefChains){
            System.out.print("{ ");
            for (ComparableEntity ce : ccc.entities){
                System.out.print(ce.getText() + ", ");
            }
            System.out.print(" }\n");
        }
    }

    public CorefChainContainer(CorefChainContainer corefChainContainer) {
        ComparableCorefChain tmpccc;
        corefChains = new LinkedList<>();
        for (ComparableCorefChain ccc : corefChainContainer.getCorefChains()){
            tmpccc = new ComparableCorefChain();
            for (ComparableEntity ce : ccc.getEntities()){
                tmpccc.getEntities().add(ce);
            }
            corefChains.add(tmpccc);
        }
    }

    public List<ComparableCorefChain> getCorefChains() {
        return this.corefChains;
    }

    public void setCorefChains(List<ComparableCorefChain> corefChains) {
        this.corefChains = corefChains;
    }

    public int getNumberOfEntities(){
        int tot = 0;
        for (ComparableCorefChain ccc : corefChains){
            tot += ccc.getEntities().size();
        }
        return tot;
    }

    public double precision(CorefChainContainer reference){
        double tot = 0;
        for (ComparableCorefChain ccc : corefChains){
            tot += ccc.precision(reference);
        }
        return tot/getNumberOfEntities();
    }

    public boolean contains(ComparableEntity ce){
        for (ComparableCorefChain ccc : corefChains){
            if (ccc.getEntities().contains(ce)) return true;
        }
        return false;
    }

    public void addEntityAsNewChain(ComparableEntity ce){
        corefChains.add(new ComparableCorefChain(ce));
    }

    public void delete(ComparableEntity ce) {
        for (ComparableCorefChain ccc : corefChains){
            if (ccc.entities.contains(ce)){
                if (ccc.entities.size()==1) corefChains.remove(ccc);
                else ccc.entities.remove(ce);
            }
        }
    }
}
