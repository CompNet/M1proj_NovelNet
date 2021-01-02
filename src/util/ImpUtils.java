package util;

import java.util.Map;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;

/**
 * @author Baptiste Quay
 * @author Tewis Lemaire
 *
 */
public class ImpUtils {

	private ImpUtils(){

	}
	
	/**
	 * @author Baptiste Quay
	 * @author Tewis Lemaire
	 *
	 */
	public static CorefChain corefByEntityMention(Map<Integer,CorefChain> corefChains, CoreEntityMention cem)
	{
		CorefChain ret = null;
		
		for (CorefChain corefchain : corefChains.values()){
			for (CorefMention mention : corefchain.getMentionsInTextualOrder()){
				if (mention.mentionSpan.equals(cem.text())){
					if (mention.startIndex == cem.tokens().get(0).index() && cem.tokens().get(cem.tokens().size()-1).index() == mention.endIndex-1){
						return corefchain;
					}
				}
			}
		}
		return ret;
	}

	public static String bestName(CoreDocument document, CoreEntityMention cem){
		Map<Integer,CorefChain> corefChains = document.corefChains();
		CorefChain corefChain =  ImpUtils.corefByEntityMention(corefChains, cem);
		if (corefChain == null) return cem.text();
		return corefChain.getRepresentativeMention().mentionSpan;
	}
	
	/**
	 * 
	 * @param corefchain
	 * @return corefMention : corefMention wich contains a NER entity or null
	 * @author Schmidt Gaëtan
	 */
	protected static CorefMention valideRepresentativeMention(CorefChain corefchain, CoreDocument document)
	{

		if (corefMentionContainsNER(corefchain.getRepresentativeMention(),document))
		{
			return corefchain.getRepresentativeMention();
		}else
		{
			for (CorefMention mention : corefchain.getMentionsInTextualOrder())
			{
				if (corefMentionContainsNER(mention,document))
					return mention;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param corefMention
	 * @return true : arg contains a ner entity
	 * @author Schmidt Gaëtan
	 */
	protected static boolean corefMentionContainsNER(CorefMention corefMention, CoreDocument document)
	{
		for (int i = corefMention.startIndex-1; i < corefMention.endIndex-1; i++)
		{
			if ("PERSON".equals(document.sentences().get(corefMention.sentNum-1).tokens().get(i).ner()))
				return true;
		}
		return false;
	}

}
