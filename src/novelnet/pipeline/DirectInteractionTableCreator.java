package novelnet.pipeline;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import novelnet.util.CustomCorefChain;
import novelnet.util.CustomInteraction;
import novelnet.book.Book;
import novelnet.table.*;

import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;

public class DirectInteractionTableCreator {
    private DirectInteractionTableCreator() {

	}

    private static List<RelationTriple> findActionsWithMultiplesCharaters(Book book) {
        Annotation annotation = book.getDocument().annotation();
        boolean subject;
        boolean object;
        List<RelationTriple> result = new LinkedList<>();

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);

			for (RelationTriple triple : triples) {
                System.out.println("\n" + triple.subjectGloss() + "\t" + triple.relationGloss() +"\t" + triple.objectGloss());
                subject = false;
                object = false;
                for (CustomCorefChain ccc : book.getCorefChain()){
                    if (ccc.contains(triple.subjectHead())){
                        subject = true;
                    }
                    if (ccc.contains(triple.objectHead())){
                        object = true;
                    }
                }
                System.out.println("subj : " + triple.subjectHead()+ " " + subject + "\t obj : " + triple.objectHead()+ " " + object);
                if (subject && object) result.add(triple);
			}
		}
        return result;
    }

    private static List<List<RelationTriple>> findSameAction(List<RelationTriple> actionList){
        Boolean alreadyAnAction;
        List<List<RelationTriple>> sameActionList = new LinkedList<>(); //a list of list of the same action : the second list is a list of actions with the same relation ie the same action

        for (RelationTriple triple : actionList) {
            alreadyAnAction = false;
            for (List<RelationTriple> action : sameActionList){
                if (action.get(0).relationHead() == triple.relationHead()){
                    action.add(triple);
                    alreadyAnAction = true;
                }
            }
            if (actionList.isEmpty()){
                List<RelationTriple> tmp = new LinkedList<>();
                tmp.add(triple);
                sameActionList.add(tmp);
            }
            else if (!alreadyAnAction) {
                List<RelationTriple> tmp = new LinkedList<>();
                tmp.add(triple);
                sameActionList.add(tmp);
            }
        }

        return sameActionList;
    }

    private static List<CustomInteraction> createInteractions(List<List<RelationTriple>> actionList, Book book){
        List<CustomInteraction> listInteraction = new LinkedList<>();

        for (List<RelationTriple> listRT : actionList){
            listInteraction.add(new CustomInteraction(listRT, book));
        }

        return listInteraction;
    }

    public static DirectInteractionTable createTable(Book book){
        DirectInteractionTable it = new DirectInteractionTable();

        List<RelationTriple> actionList = findActionsWithMultiplesCharaters(book);
        System.out.println(actionList);
        List<List<RelationTriple>> sameActionList = findSameAction(actionList);
        System.out.println(sameActionList);
        List<CustomInteraction> listInteraction = createInteractions(sameActionList, book);
        System.out.println(listInteraction);

        /*for (CustomCorefChain ccc : book.getCorefChain()){
            System.out.println(ccc);
        }

        for(List<RelationTriple> rtlist : actionList){
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
