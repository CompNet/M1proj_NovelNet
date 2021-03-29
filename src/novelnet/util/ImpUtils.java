package novelnet.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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

	public static Properties getFrenchProperties(){
		Properties props = new Properties();

		props.setProperty("tokenize.language","fr"); 
		props.setProperty("mwt.mappingFile","edu/stanford/nlp/models/mwt/french/french-mwt.tsv");
		props.setProperty("mwt.pos.model","edu/stanford/nlp/models/mwt/french/french-mwt.tagger");
		props.setProperty("mwt.statisticalMappingFile","edu/stanford/nlp/models/mwt/french/french-mwt-statistical.tsv");
		props.setProperty("mwt.preserveCasing","false");
		props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/french-ud.tagger");
		props.setProperty("ner.model", "edu/stanford/nlp/models/ner/french-wikiner-4class.crf.ser.gz");
		props.setProperty("ner.applyNumericClassifiers","false");
		props.setProperty("ner.useSUTime","false");
		props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/frenchSR.beam.ser.gz");
		props.setProperty("depparse.model","edu/stanford/nlp/models/parser/nndep/UD_French.gz");

		return props;
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


	public static boolean isPonctuation(CoreLabel token) {
		if(token.originalText().length() == 1){
			if(token.originalText().equals("I") || token.originalText().equals("a") || token.originalText().equals("à") || token.originalText().equals("y") || token.originalText().equals("n") || token.originalText().equals("m") || token.originalText().equals("s")){
				return false;
			}
			else return true;
		}
		/*else if (token.originalText().equals("...")) return true;*/
		return false;
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
