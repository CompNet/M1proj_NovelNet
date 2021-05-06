package novelnet.pipeline;

import java.util.LinkedList;
import java.util.List;

import novelnet.util.CustomInteraction;
import novelnet.util.CustomTriple;
import novelnet.util.ImpUtils;
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

    private static List<RelationTriple> findAllActions(Book book){
        Annotation annotation = book.getDocument().annotation();
        List<RelationTriple> result = new LinkedList<>();

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			result.addAll(sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class));
        }

        return result;
    }

    private static List<List<RelationTriple>> regroupSameAction(List<RelationTriple> actionList){
        Boolean foundActionList;
        List<List<RelationTriple>> sameActionList = new LinkedList<>(); //a list of list of the same action : the second list is a list of actions with the same relation ie the same action

        for (RelationTriple triple : actionList) {
            foundActionList = false;
            for (List<RelationTriple> action : sameActionList){
                if (action.get(0).relationHead() == triple.relationHead()){
                    action.add(triple);
                    foundActionList = true;
                }
                
            }
            if (!foundActionList) {
                List<RelationTriple> tmp = new LinkedList<>();
                tmp.add(triple);
                sameActionList.add(tmp);
            }
        }

        return sameActionList;
    }

    private static List<CustomInteraction> createInteractions(List<List<RelationTriple>> actionList, Book book){
        List<CustomInteraction> listInteraction = new LinkedList<>();
        List<CustomTriple> tmp;
        Boolean alreadyIn;

        for (List<RelationTriple> listRT : actionList){
            tmp = new LinkedList<>();
            for(RelationTriple rtToAdd : listRT){
                CustomTriple ctToAdd = new CustomTriple(rtToAdd, book);
                alreadyIn = false;
                for (CustomTriple ctToCompare : tmp) {
                    if (ImpUtils.equals(ctToCompare, ctToAdd)) alreadyIn = true;
                }
                if (!alreadyIn) tmp.add(ctToAdd);
            }

            listInteraction.add(new CustomInteraction(tmp));
            
        }

        return listInteraction;
    }

    public static List<CustomInteraction> findActionsWithMultiplesCharaters(Book book) {
        List<CustomInteraction> result = new LinkedList<>();

        List<RelationTriple> actionList = findAllActions(book);
        //System.out.println(actionList);
        List<List<RelationTriple>> sameActionList = regroupSameAction(actionList);
        //System.out.println(sameActionList);
        List<CustomInteraction> allInteractionList = createInteractions(sameActionList, book);
        //System.out.println(listInteraction);

        for (CustomInteraction customInteraction : allInteractionList) {
            if (customInteraction.characterNumber() > 1){
                result.add(customInteraction);
            }
        }

        return result;
    }

    public static DirectInteractionTable createTable(Book book){
        DirectInteractionTable it = new DirectInteractionTable();

        List<CustomInteraction> finalInteractionList = findActionsWithMultiplesCharaters(book);

        /*for (CustomCorefChain ccc : book.getCorefChain()){
            System.out.println(ccc);
        }

        for(List<RelationTriple> rtlist : actionList){
            System.out.println(rtlist);
        }*/

        for(CustomInteraction ci : finalInteractionList){
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
