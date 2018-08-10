package com.skplanet.nlp.nebot.resource;

import com.skplanet.hnlp.sntBreaker.trainer.SBprob;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.nebot.settings.Prop;
import org.apache.log4j.Logger;

import java.io.IOException;

import static com.skplanet.nlp.nebot.settings.Prop.*;


/**
 * Resource Management
 *
 * @create Changho Yoon
 * @refactor Donghun Shin / donghun.shin@sk.com
 */
public class Resource{
	// log
	private static final Logger LOGGER = Logger.getLogger(Resource.class.getName());

	public static Dic dicNebot;
	public static SBprob sbProb;

	/**
	 * Set Resources
	 */
	public static void setResources() {
		Configuration config = Configuration.getInstance();

		boolean doSegment = false;
		int ngramSize = 3;
		float eosThreshold = -1.0f;
		float nbsThreshold = -1.0f;
		int eosFreqThreshold = -1;
		int nbsFreqThreshold = -1;
		try {
			config.loadProperties(NEBOT_PROP);
			config.loadProperties(SEGMENT_PROP);

			// nebot setting
			if (config.readProperty(NEBOT_PROP, SENT_SEGMENT).equals("true")) {
				doSegment = true;
			}

			// segment setting
			ngramSize = Integer.parseInt(config.readProperty(SEGMENT_PROP, NGRAM_SIZE));
			eosFreqThreshold = Integer.parseInt(config.readProperty(SEGMENT_PROP, EOS_FRQ_THRESHOLD));
			nbsFreqThreshold = Integer.parseInt(config.readProperty(SEGMENT_PROP, NBS_FRQ_THRESHOLD));
			eosThreshold = Float.parseFloat(config.readProperty(SEGMENT_PROP, EOS_THRESHOLD));
			nbsThreshold = Float.parseFloat(config.readProperty(SEGMENT_PROP, NBS_THRESHOLD));

		} catch (IOException e) {
			LOGGER.error("failed to load properties : " + NEBOT_PROP, e);
		}

		String stopwordsDicpath = config.getResource(STOPWORD_GEN).getFile();
		String stopwordsSubMatchDicpath = config.getResource(STOPWORD_GEN_SUB).getFile();
		String historyNeDicpathNews = config.getResource(NE_HISTORY_NEWS).getFile();
		String historyNeDicpathBlog = config.getResource(NE_HISTORY_BLOG).getFile();
		String historyTstore = config.getResource(STOPWORD_TSTORE).getFile();
		String historySkcomz = config.getResource(STOPWORD_SKCOMZ).getFile();
		String sysDicpath = config.getResource(NLP_SYSTEM_DICT).getFile();
		String urlDownedDicpath = config.getResource(URL_DOWNLOAD).getFile();

		dicNebot = new Dic();
		dicNebot.setDic(stopwordsDicpath, dicNebot.getStopwordsDic());
		dicNebot.setDic(sysDicpath, dicNebot.getSysDic());
		dicNebot.setDic(urlDownedDicpath, dicNebot.getUrlDic());
		dicNebot.setDic(stopwordsSubMatchDicpath, dicNebot.getStopwordsSubMatchDic());
		dicNebot.setDic(historyNeDicpathNews, dicNebot.getHistoryNeDicNews());
		dicNebot.setDic(historyNeDicpathBlog, dicNebot.getHistoryNeDicBlog());
		dicNebot.setDic(historyTstore, dicNebot.getTstoreNeDic());
		dicNebot.setDic(historySkcomz, dicNebot.getSkcomzNeDic());

		if (doSegment) {
			try {
				LOGGER.info("segmentation resource loading ....");
				sbProb = new SBprob(false, ngramSize);
				sbProb.load(
						config.getResource(SEGMENT_MODEL_EOS).getFile(),
						true,
						eosFreqThreshold,
						eosThreshold);
				sbProb.load(
						config.getResource(SEGMENT_MODEL_NBS).getFile(),
						false,
						nbsFreqThreshold,
						nbsThreshold);
				LOGGER.info("segmentation resource loading done");
			} catch (Exception e) {
				LOGGER.error("failed to load segmentation resource!", e);
			}
		}
	}
}