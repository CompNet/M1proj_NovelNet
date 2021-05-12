package novelnet.pipeline;

import java.util.LinkedList;
import java.util.List;

import novelnet.util.CustomDirectInteraction;
import novelnet.util.CustomTriple;
import novelnet.util.ImpUtils;
import novelnet.book.Book;
import novelnet.table.*;

import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;

/**
 * Create a DirectInteractionTable
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class DirectInteractionTableCreator {

    
    private DirectInteractionTableCreator() {

	}

    /**
     * get all relations triple from a Book's CoreDocument
     * 
     * @param book the book from wich you want to get the triples
     * @return a list of all the triples in the book 
    */
    private static List<RelationTriple> findAllActions(Book book){
        Annotation annotation = book.getDocument().annotation();
        List<RelationTriple> result = new LinkedList<>();

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence
			result.addAll(sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class));
        }

        return result;
    }

    /**
     * regroup the relation triple by the relation's head.
     * 
     * @param actionList a list of RelationTriple (result of findAllActions())
     * @return a list of action regrouped by relation's head
    */
    private static List<List<RelationTriple>> regroupSameAction(List<RelationTriple> actionList){
        Boolean foundActionList;
        List<List<RelationTriple>> sameActionList = new LinkedList<>(); //a list of list of the same action : the second list is a list of actions with the same relation ie the same action

        for (RelationTriple triple : actionList) {
            foundActionList = false;
            //Search if the head of the triple is already in a list in the result
            for (List<RelationTriple> action : sameActionList){
                if (action.get(0).relationHead() == triple.relationHead()){
                    //if it is add it to the list
                    action.add(triple);
                    foundActionList = true;
                }
                
            }
            //if the action was not listed add a new list in the result for that action
            if (!foundActionList) {
                List<RelationTriple> tmp = new LinkedList<>();
                tmp.add(triple);
                sameActionList.add(tmp);
            }
        }

        return sameActionList;
    }

    /**
     * Create our CustomInteraction from the regrouped actions.
     * 
     * @param actionList a list of action regrouped into lists (result of regroupSameAction()).
     * @param book the book from wich you get the CorefChains to identify subject and object in the interaction.
     * @return a list of CustomInteraction each one representing one action.
    */
    private static List<CustomDirectInteraction> createInteractions(List<List<RelationTriple>> actionList, Book book){
        List<CustomDirectInteraction> listInteraction = new LinkedList<>();
        List<CustomTriple> tmp;
        Boolean alreadyIn;

        for (List<RelationTriple> listRT : actionList){
            tmp = new LinkedList<>();
            for(RelationTriple rtToAdd : listRT){
                //Creating our CustomTriple to put into our CustomInteraction
                CustomTriple ctToAdd = new CustomTriple(rtToAdd, book);
                alreadyIn = false;
                //checking if the triple is already in or better than one that's already in
                for (CustomTriple ctToCompare : tmp) {
                    //checking if the triple is already in
                    if (ImpUtils.equals(ctToCompare, ctToAdd)){
                        //if the triple to compare is already in but the object of the triple already in is null
                        if (ctToCompare.getObject() == null){
                            //replace the old triple by the new one
                            tmp.remove(ctToCompare);
                            tmp.add(ctToAdd);
                        }
                        alreadyIn = true;
                    }
                }
                //if the triple was not found we add it to the temporary list
                if (!alreadyIn) tmp.add(ctToAdd);
            }
            //Creating our CustomInteraction from the triple list and putting it into the result.
            listInteraction.add(new CustomDirectInteraction(tmp));
            
        }

        return listInteraction;
    }

    /**
     * Use function above to find all Actions with multiple Characters in the book specified in argument.
     * 
     * @param book the book from wich you want to extract the actions.
     * @return a list of CustomInteraction each one representing one action.
    */
    public static List<CustomDirectInteraction> findActionsWithMultiplesCharaters(Book book) {
        List<CustomDirectInteraction> result = new LinkedList<>();

        //pipeline
        List<RelationTriple> actionList = findAllActions(book);
        List<List<RelationTriple>> sameActionList = regroupSameAction(actionList);
        List<CustomDirectInteraction> allInteractionList = createInteractions(sameActionList, book);

        for (CustomDirectInteraction customInteraction : allInteractionList) {
            //keeping actions with more than one character.
            if (customInteraction.characterNumber() > 1){
                result.add(customInteraction);
            }
        }

        return result;
    }

    /**
     * Use function above to get all actions with multiple characters and then build the table from the list.
     * 
     * @param book the book from wich you want to extract the actions.
     * @return the table of DirectInteraction
    */
    public static DirectInteractionTable createTable(Book book){
        DirectInteractionTable it = new DirectInteractionTable();

        List<CustomDirectInteraction> finalInteractionList = findActionsWithMultiplesCharaters(book);

        for(CustomDirectInteraction ci : finalInteractionList){
            for (int i = 0; i < ci.getSubjects().size(); i++){
                //linking each subject of the interaction.
                for (int j = 0; j < ci.getSubjects().size(); j++){
                    if (i!=j){
                        it.add(ci.getSubjects().get(i).getRepresentativeName(), ci.getSubjects().get(j).getRepresentativeName(), ci.getSentenceIndex());
                    }
                }
                //linking each subject with each object of the intereaction.
                for (int j = 0; j < ci.getObjects().size(); j++){
                    it.add(ci.getSubjects().get(i).getRepresentativeName(), ci.getObjects().get(j).getRepresentativeName(), ci.getSentenceIndex());
                }
            }
        }
        return it;
    }
}
