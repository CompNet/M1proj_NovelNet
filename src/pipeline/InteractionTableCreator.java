package pipeline;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import book.Book;

import edu.stanford.nlp.util.CoreMap;
import util.CustomInteraction;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;

public class InteractionTableCreator {
    private InteractionTableCreator() {

	}

    private static List<List<RelationTriple>> findSameAction(Book book){
        Annotation annotation = book.getDocument().annotation();
        Boolean sameAction;
        List<RelationTriple> tmpList = new LinkedList<>();

        List<List<RelationTriple>> listSameAction = new LinkedList<>();

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			// Print the triples
            /*for (RelationTriple triple : triples) {
                System.out.println(triple);
                System.out.println("subject head : " + triple.subjectHead()+ "\tsubject : " + triple.subject);
                System.out.println("object head : " + triple.objectHead()+ "\tobject : " + triple.object +"\n");
            }*/

			for (RelationTriple triple : triples) {
                sameAction = false;
                System.out.println(triple.subjectGloss() + "\t" + triple.relationGloss() +"\t" + triple.objectGloss());
                for (List<RelationTriple> list : listSameAction){
                    for (RelationTriple rt : list){
                        if (rt.relationHead() == triple.relationHead()){
                            sameAction = true;
                            tmpList = list;
                        }
                    }
                }
                
                if (listSameAction.isEmpty()){
                    List<RelationTriple> tmp = new LinkedList<>();
                    tmp.add(triple);
                    listSameAction.add(tmp);
                }
                else if (sameAction){
                    tmpList.add(triple);
                }
                else {
                    List<RelationTriple> tmp = new LinkedList<>();
                    tmp.add(triple);
                    listSameAction.add(tmp);
                }
			}
		}
        return listSameAction;
    }

    private static List<CustomInteraction> createInteractions(List<List<RelationTriple>> listSameAction, Book book){
        List<CustomInteraction> listInteraction = new LinkedList<>();

        for (List<RelationTriple> listRT : listSameAction){
            listInteraction.add(new CustomInteraction(listRT, book));
        }

        return listInteraction;
    }

    public static InteractionTable createTable(Book book){
        InteractionTable it = new InteractionTable();

        
        List<List<RelationTriple>> listSameAction = findSameAction(book);
        List<CustomInteraction> listInteraction = createInteractions(listSameAction, book);

        /*for (CustomCorefChain ccc : book.getCorefChain()){
            System.out.println(ccc);
        }

        for(List<RelationTriple> rtlist : listSameAction){
            System.out.println(rtlist);
        }*/

        for(CustomInteraction ci : listInteraction){
            //ci.display();
            for (int i = 0; i < ci.getSubjects().size(); i++){
                for (int j = 0; j < ci.getSubjects().size(); j++){
                    if (i!=j){
                        it.add(ci.getSubjects().get(i).getRepresentativeName(), ci.getSubjects().get(j).getRepresentativeName(), ci.getSentenceIndex());
                    }
                }
                for (int j = 0; j < ci.getObjects().size(); j++){
                    it.add(ci.getSubjects().get(i).getRepresentativeName(), ci.getObjects().get(j).getRepresentativeName(), ci.getSentenceIndex());
                }
            }
        }
        return it;
    }
}
