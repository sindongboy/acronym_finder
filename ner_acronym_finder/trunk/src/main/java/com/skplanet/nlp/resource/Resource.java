package com.skplanet.nlp.resource;
import com.skplanet.nlp.acronym.Prop;
import com.skplanet.nlp.config.Configuration;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Resource loading?
 *  
 * TODO: totally don't understand the reason of the existence of this class at all!
 * 
 * @author changho yoon
 * @modified Donghun Shin / donghun.shin@sk.com
 * 
 */
public class Resource{
	private static final Logger LOGGER = Logger.getLogger(Resource.class.getName());

	public static Dic dicNebot;

	public static void setResources() {
		// replace 'Properties' with 'omp-config'
		Configuration config = Configuration.getInstance();
		try {
			config.loadProperties(Prop.NE_ACRONYM_PROP_NAME);
		} catch (IOException e) {
			LOGGER.error("properties loading failed: " + Prop.NE_ACRONYM_PROP_NAME, e);
		}

		String stopword = config.getResource(Prop.STOPWORD).getFile();
		String subStopword = config.getResource(Prop.STOPWORD_SUB).getFile();

		dicNebot = new Dic();
		// TODO: dictionary loading, why are these set to be static???
		dicNebot.setDic(stopword, dicNebot.getStopwordsDic());
		dicNebot.setDic(subStopword, dicNebot.getStopwordsDic());
	}
}
