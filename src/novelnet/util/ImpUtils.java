package novelnet.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import novelnet.pipeline.TextNormalization;

/**
 * @author Baptiste Quay
 * @author Tewis Lemaire
 *
 * some generally usefull fonction
*/
public class ImpUtils {

	/**
	 * Stanford's CoreDocument.
	*/
	private static CoreDocument document = null;

	private ImpUtils(){
	}

	public static void setDocument(CoreDocument doc){
		document = doc;
	}

	/**
	 * get the corefChain associated with the coreEntityMention in argument
	 * 
	 * @param cem Stanford's CoreEntityMention
	 * @return the corefChain containing the entity
	*/
	public static CorefChain corefByEntityMention(CoreEntityMention cem) {
		Integer corefClustId = null;

		for (CoreLabel token : cem.tokens()){
			corefClustId= token.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
			if (corefClustId != null) return document.corefChains().get(corefClustId);
		}
		return null;
	}

	/**
	 * assert if the two tokens are equal based on text, sentence index and index in that sentence.
	 * 
	 * @return true if equal, false otherwise.
	*/
	public static Boolean compareCoreLabel(CoreLabel token1, CoreLabel token2){
		return token1.originalText().equals(token2.originalText()) && token1.index() == token2.index() && token1.sentIndex() == token2.sentIndex();
	}

	/**
	 * get a Properties object with every thing to process french text with StanfordCoreNLP.
	 * 
	 * @return a Properties object with every thing to process french text with StanfordCoreNLP.
	*/
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

	/**
	 * get the CoreEntityMention associated with a CorefMention.
	 * 
	 * @param cm a CorefMention for StanfordCoreNLP.
	 * @return the corresponding CoreEntityMention.
	*/
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

	/**
	 * get the tokens for the CorefMention.
	 * 
	 * @param cm a CorefMention for StanfordCoreNLP.
	 * @return the corresponding tokens.
	*/
	public static List<CoreLabel> getTokensbyCorefMention(CorefMention cm) throws NullDocumentException {
		if (document == null) throw new NullDocumentException("CoreDocument has not been defined");
		List<CoreLabel> result = new LinkedList<>();
		for ( CoreLabel token : document.sentences().get(cm.sentNum-1).tokens()){
			if (cm.startIndex <= token.index() && token.index() < cm.endIndex){
				result.add(token);
			}
		}
		return result;
	}

	/**
	 * get the CoreEntityMentions without corefChain.
	 * 
	 * @return the CoreEntityMentions without corefChain.
	*/
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
	 * check if a token is ponctuation.
	 * 
	 * @return true if the token is ponctuation, false otherwise.
	*/
	public static Boolean isPonctuation(CoreLabel token) {
		if(token.originalText().length() == 1){
			if(token.originalText().equals("I") || token.originalText().equals("a") || token.originalText().equals("à") || token.originalText().equals("y") || token.originalText().equals("n") || token.originalText().equals("m") || token.originalText().equals("s")){
				return false;
			}
			else return true;
		}
		return false;
	}

	/**
	 * Build and process an english NER pipeline from StanfordCoreNLP.
	 * 
	 * @param pathToFile the path to the english text file to process.
	 * @return the CoreDocument associated.
	*/
	public static CoreDocument processNER(String pathToFile) throws IOException{
		//transforming the file to a String
		FileInputStream is = new FileInputStream(pathToFile);     
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		//text Pre-processing
        content = TextNormalization.preProcess(content, "en");

        //building Stanford's pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);

		ImpUtils.setDocument(document);
		
		return document;
	}

	/**
	 * Build and process a french NER pipeline from StanfordCoreNLP.
	 * 
	 * @param pathToFile the path to the french text file to process.
	 * @return the CoreDocument associated.
	*/
	public static CoreDocument processFrenchNER(String pathToFile) throws IOException{
		//transforming the file to a String
		FileInputStream is = new FileInputStream(pathToFile);     
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		//text Pre-processing
        content = TextNormalization.preProcess(content, "fr");

        //building Stanford's pipeline
		Properties props = ImpUtils.getFrenchProperties();
		props.setProperty("annotators","tokenize,ssplit,pos,lemma,ner");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);

		ImpUtils.setDocument(document);
		
		return document;
	}

	/**
	 * Build and process an english co reference pipeline from StanfordCoreNLP.
	 * 
	 * @param pathToFile the path to the english text file to process.
	 * @return the CoreDocument associated.
	*/
	public static CoreDocument processCoref(String pathToFile) throws IOException{
		//transforming the file to a String
		FileInputStream is = new FileInputStream(pathToFile);     
		String content = IOUtils.toString(is, "UTF-8");

		//text Pre-processing
        content = TextNormalization.preProcess(content, "en");

        //building Stanford's pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);

		ImpUtils.setDocument(document);
		
		return document;
	}

	/**
	 * Build and process an english OpenIE with co reference pipeline from StanfordCoreNLP.
	 * 
	 * @param pathToFile the path to the english text file to process.
	 * @return the CoreDocument associated.
	*/
	public static CoreDocument processOpenIE(String pathToFile) throws IOException{
		//transforming the file to a String
		FileInputStream is = new FileInputStream(pathToFile);     
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		//text Pre-processing
        content = TextNormalization.preProcess(content, "en");

        //building Stanford's pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref,natlog,openie");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);
		
		ImpUtils.setDocument(document);

		return document;
	}

	/**
	 * rounding double function. if value = 3.33333 and places = 2 return = 3.34
	 * 
	 * @param value the value to round
	 * @param places the number of figures after the dot/comma
	 * @return the same double but rounded up.
	*/
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
