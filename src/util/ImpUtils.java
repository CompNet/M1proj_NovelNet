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
			if (corefClustId != null) return document.corefChains().get(corefClustId);
		}
		return null;
	}

	public static CoreEntityMention getCoreEntityMentionByCorefMention(CorefMention cm) throws NullDocumentException {
		if (document == null) throw new NullDocumentException("CoreDocument has not been defined");
		CoreEntityMention result = null;
		for (CoreEntityMention em : document.entityMentions()){
			//System.out.println((cm.startIndex-1) + " " + (cm.endIndex-1));
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

	public static List<CoreLabel> getTokensbyCorefMention(CorefMention cm) throws NullDocumentException {
		if (document == null) throw new NullDocumentException("CoreDocument has not been defined");
		List<CoreLabel> result = new LinkedList<>();
		for ( CoreLabel token : document.sentences().get(cm.sentNum-1).tokens()){
			//System.out.println(cm.mentionSpan + "\t" + cm.startIndex + "\t" + cm.endIndex + "\t" + token.originalText() + "\t" + token.index());
			if (cm.startIndex <= token.index() && token.index() < cm.endIndex){
				result.add(token);
			}
		}
		return result;
	}

	public static List<CoreEntityMention> getCoreEntityMentionsWithoutCorefChain() throws NullDocumentException{
		if (document == null) throw new NullDocumentException("CoreDocument has not been defined");
		List<CoreEntityMention> result = new LinkedList<>();
		for (CoreEntityMention cem : document.entityMentions()){
			if (cem.entityType().equals("PERSON")){
				CorefChain cc = ImpUtils.corefByEntityMention(cem);
				if (cc == null) {
					result.add(cem);
				}
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
	/* useless for now but it might be usefull later.
	
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
