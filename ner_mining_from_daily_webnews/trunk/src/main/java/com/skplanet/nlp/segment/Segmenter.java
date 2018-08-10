/**
 * 
 */
package com.skplanet.nlp.segment;

import com.skplanet.hnlp.sntBreaker.hbreakerAPI.HbreakerAPI;
import com.skplanet.hnlp.sntBreaker.trainer.SBprob;
import org.apache.log4j.Logger;

public class Segmenter {
	private static final Logger LOGGER = Logger.getLogger(Segmenter.class.getName());

	private HbreakerAPI hbreaker = null;

	public Segmenter(SBprob sbProb){
		try{
			hbreaker = new HbreakerAPI(sbProb);
		}catch(Exception e){
			LOGGER.error("failed to initialize Hbreaker");
		}
	}
	
	public String[] segment_sentences(String content){
		return segment_sentences(content, SegmenterCodes.DO_SEGMENTING);
	}
	
	public String[] segment_sentences(String content, boolean do_segment){
		if(!do_segment)
			return new String[]{content};
		
		content = content.trim();
		
		if("".equals(content))
			return null;
		
		content	= hbreaker.processOneLine(content).toString();

		return content.split(SegmenterCodes.NEW_LINE);
	}
} 