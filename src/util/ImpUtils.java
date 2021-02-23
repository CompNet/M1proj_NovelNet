package util;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;

/**
 * @author Baptiste Quay
 * @author Tewis Lemaire
 *
 */
public class ImpUtils {

	private static CoreDocument document = null;

	private ImpUtils(){
	}
	

	public static void setDocument(CoreDocument doc){
		document = doc;
	}

	/**
	 * @author Baptiste Quay
	 * @author Tewis Lemaire
	 *
	 */
	public static CorefChain corefByEntityMention(CoreEntityMention cem) throws NullDocumentException {
		Integer corefClustId = null;

		for (CoreLabel token : cem.tokens()){
			corefClustId= token.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
			if (corefClustId != null) break;
		}
		return document.corefChains().get(corefClustId);
	}

	public static CoreEntityMention getCoreEntityMentionByCorefMention(CorefMention cm) throws NullDocumentException {
		if (document == null) throw new NullDocumentException("CoreDocument has not been defined");
		CoreEntityMention result = null;
		for (CoreEntityMention em : document.entityMentions()){
			if (em.entityType().equals("PERSON")){
				for (int i = cm.startIndex-1; i < cm.endIndex-1; i++){
					if (em.tokens().get(0).beginPosition() == document.sentences().get(cm.sentNum-1).tokens().get(i).beginPosition()){
						result = em;
					}
				}
			}
        }
		return result;
	}

	public static List<CoreEntityMention> getCoreEntityMentionsWithoutCorefChain() throws NullDocumentException{
		if (document == null) throw new NullDocumentException("CoreDocument has not been defined");
		List<CoreEntityMention> result = new LinkedList<>();
		for (CoreEntityMention cem : document.entityMentions()){
			CorefChain cc = ImpUtils.corefByEntityMention(cem);
			if ( cc == null) {
				result.add(cem);
            }
		}
		return result;
	}
	
	/**
	 * @author Baptiste Quay
	 * @author Tewis Lemaire
	 * @throws NullDocumentException
	 *
	 */
	public static String bestName(CoreEntityMention cem){
		try{
			CorefChain corefChain =  ImpUtils.corefByEntityMention(cem);
			if (corefChain == null) return cem.text();
			return corefChain.getRepresentativeMention().mentionSpan;
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
		return null;
	}

	/* useless for now but keep it until it might be usefull.
	public static List<String> getNamesInCorefChain(CorefChain cc) throws NullDocumentException{
		if (document == null) throw new NullDocumentException("CoreDocument has not been defined");
		CoreEntityMention temp;
		List<String> result = new LinkedList<>();
		for(CorefMention cm : cc.getMentionsInTextualOrder()){
			temp = getCoreEntityMentionByCorefMention(cm);
			if (temp!=null) result.add(temp.text());
		}

		return result;
	}
	
	public static List<String> getAllRepresentativeNamesOfCorefChains(List<CorefChain> ccList){
		List<String> result = new LinkedList<>();
		for(CorefChain cc : ccList){
			if (cc.getRepresentativeMention() != null){
				try {
					CoreEntityMention cem = getCoreEntityMentionByCorefMention(cc.getRepresentativeMention());
					if (cem != null){
						result.add(cem.text());
						
					}
				}
				catch (Exception e){System.out.println(e.getMessage());}	
			}
		}
		return result;
	}*/

}
