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

	/**
	 * @author Baptiste Quay
	 * @author Tewis Lemaire
	 *
	 */
	public static boolean sameCharacter(CoreEntityMention emA, CoreEntityMention emB, CoreDocument document){
		CorefChain charA = corefByEntityMention(document.corefChains(), emA);
		CorefChain charB = corefByEntityMention(document.corefChains(), emB);
		if (charA != null){
			if(charB != null){
				return charA.equals(charB);
			}
			return false;
		}
		else if (charB != null) return false;
		else return emA.text().equals(emB.text());
		
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
					//System.out.println("m.startindex : " + mention.startIndex + " cem.startindex : " +cem.tokens().get(0).index() + "m.endIndex : " + mention.endIndex + " cem.endIndex : " +cem.tokens().get(cem.tokens().size()).index());
					if (mention.startIndex-1 <= cem.tokens().get(0).index() && cem.tokens().get(cem.tokens().size()-1).index() <= mention.endIndex){
						return corefchain;
					}
				}
			}
		}
		return ret;
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
