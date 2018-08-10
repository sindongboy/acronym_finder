package com.skplanet.nlp.nebot;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.nebot.settings.Prop;
import com.skplanet.nlp.nebot.resource.Resource;
import com.skplanet.nlp.segment.Segmenter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.SocketTimeoutException;
import java.net.URLDecoder;

import java.util.*;

import java.text.SimpleDateFormat;
import java.text.DateFormat;

import java.net.URL;
import java.nio.charset.Charset;


import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.HttpStatusException;


/**
 * NeBot Core (crawler)
 *
 * @author Changho Yoon
 * @refactor Donghun Shin / donghun.shin@sk.com
 */
public class Nebot {
	// logger
	private static final Logger LOGGER = Logger.getLogger(Nebot.class.getName());

	public static final int THOUSAND = 1000;

	private List<SeedUrlPattern> urlseeds = new ArrayList<SeedUrlPattern>();
	private Segmenter segmenter = null;
	private int timeOut = -1;
	private int termMaxLength = -1;
	private boolean doSegment = false;
	private boolean printSysdicDup = false;
	private boolean printAsciiOnly = false;
	private boolean printPrv = false;
	private boolean printTstore = false;
	private boolean printSkcomz = false;
	private boolean singleQuotation = false;
	private boolean parentheses = false;
	private boolean angleBracket = false;
	private boolean squareBracket = false;
	private String outputBlog = null;
	private String outputNews = null;
	private String historyBlog = null;
	private String historyNews = null;
	private String downloadedUrl = null;
	private String seed = null;

	/**
	 * NeBot Constructor
	 * initialize segmentation instance if required
	 */
	public Nebot() {
		Configuration config = Configuration.getInstance();
		try {
			config.loadProperties(Prop.SEGMENT_PROP);
			config.loadProperties(Prop.NEBOT_PROP);
			// nebot option
			this.timeOut = Integer.parseInt(config.readProperty(Prop.NEBOT_PROP, Prop.TIME_OUT_SECONDS));

			// segment option
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.SENT_SEGMENT))) {
				doSegment = true;
			}

			// print sysdic dup
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.PRINT_SYSDIC_DUP))) {
				printSysdicDup = true;
			}

			// print ascii only
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.PRINT_ASCII_ONLY_WORD))) {
				printAsciiOnly = true;
			}

			// print prv
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.PRINT_PRV_DUP))) {
				printPrv = true;
			}

			// print tstore
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.PRINT_TSTORE_DUP))) {
				printTstore = true;
			}

			// print skcomz
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.PRINT_SKCOMZ_DUP))) {
				printSkcomz = true;
			}

			// single quotation
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.SINGLE_QUOTATION))) {
				singleQuotation = true;
			}

			// parentheses
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.PARENTHESES))) {
				parentheses = true;
			}

			// angle bracket
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.ANGLE_BRACKET))) {
				angleBracket = true;
			}

			// square bracket
			if ("true".equals(config.readProperty(Prop.NEBOT_PROP, Prop.SQUARE_BRACKET))) {
				squareBracket = true;
			}

			// output blog
			outputBlog = config.readProperty(Prop.NEBOT_PROP, Prop.NE_FILE_OUT_BLOG);
			outputNews = config.readProperty(Prop.NEBOT_PROP, Prop.NE_FILE_OUT_NEWS);
			historyBlog = config.getResource(Prop.NE_HISTORY_BLOG).getFile();
			historyNews = config.getResource(Prop.NE_HISTORY_NEWS).getFile();
			downloadedUrl = config.getResource(Prop.URL_DOWNLOAD).getFile();

			// seed
			seed = config.getResource(Prop.SEED).getFile();

			// term max length
			termMaxLength = Integer.parseInt(config.readProperty(Prop.NEBOT_PROP, Prop.TERM_MAX_LENGTH));


		} catch (IOException e) {
			LOGGER.error("failed to load segmentation properties: " + Prop.SENT_SEGMENT, e);
		}
		if (doSegment) {
			this.segmenter = new Segmenter(Resource.sbProb);
		}
	}

	/**
	 * crawl web document from URL
	 * @param link url
	 */
	public void harvestFromUrl(String link){
		try {
			Document docUrl = Jsoup.connect(link).timeout(this.timeOut * THOUSAND).get();

			SingleUrl url = new SingleUrl();
			url.url = link;
			url.channel = "news";
			url.siteInfo = "xx_yy_zz";

			String body = docUrl.text();
			body = cleanText(body);
			String[] arrSents;
			List<String> sents = new ArrayList<String>();
			List<NeEntry> entrys = new ArrayList<NeEntry>();
			if (body.length() > 0) {
				if (this.doSegment && this.segmenter != null) {
					arrSents = segmenter.segment_sentences(body, doSegment);
				} else {
					arrSents = new String[1];
					arrSents[0] = body;
				}
				for (String sent : arrSents) {
					sent = trimRight(sent);
					int ret = isProperText(sent);
					if (ret > 0) {
						sents.add(sent);
					}
				}

				extractNe(url, sents, entrys);
				printNe(url, entrys);
			}
		}catch(SocketTimeoutException e){
			LOGGER.error("socket error", e);
		}catch(IOException e){
			LOGGER.error("i/o error", e);
		}
	}
	public void harvestFromText(String body){
		SingleUrl url = new SingleUrl();
		url.url = "http://default";
		url.channel = "news";
		url.siteInfo = "xx_yy_zz";

		String cleanBody = cleanText(body);
		String[] arrSents;
		List<String> sents = new ArrayList<String>();
		List<NeEntry> entrys = new ArrayList<NeEntry>();
		if(cleanBody.length() > 0){
			if(this.doSegment &&  this.segmenter != null) {
				arrSents = segmenter.segment_sentences(cleanBody, doSegment);
			}else{
				arrSents = new String[1];
				arrSents[0] = cleanBody;
			}
			for(String sent : arrSents){
				sent = trimRight(sent);
				int ret =isProperText(sent);
				if(ret >0){
					sents.add(sent);
				}
			}

			extractNe(url, sents, entrys);
			printNe(url,entrys);
		}
	}

	public void harvest(){
		parseSeedPattern();
		harvestNE();
	}
	private void harvestNE(){

		List<SingleUrl> urlPool = new ArrayList<SingleUrl>();
		for(SeedUrlPattern seed : this.urlseeds){
			urlPool.clear();
			if(seed.run){
				String siteInfo = seed.seedInfo;
				extractUrl(siteInfo, seed, urlPool);
				extractNeMain(urlPool);
			}
		}
	}

	private void extractNeMain(List<SingleUrl> urlPool) {

		List<NeEntry> entrys = new ArrayList<NeEntry>();
		List<String> sents = new ArrayList<String>();
		for (SingleUrl url : urlPool) {
			if (url.channel.equalsIgnoreCase(NeBotCodes.CHANNEL_BLOG)) {
				extractNe(url, entrys);
				saveNe(url, entrys);
			} else {
				extractText(url, sents);
				extractNe(url, sents, entrys);
				saveNe(url, entrys);
			}
		}
	}
	private void extractNe(SingleUrl url, List<NeEntry> entrys ){
		BlogUrlPattern blogUrlPattern = parseBlogUrl(url);
		url.url = blogUrlPattern.getFullUrlPost().toString();
		LOGGER.debug("Step 4 : full tag url... " + blogUrlPattern.getFullUrlTag().toString());
		LOGGER.debug("Step 4 : full post url... " + blogUrlPattern.getFullUrlPost().toString());
		LOGGER.debug("Step 4 : titlel... " + url.title);

		try{
			String jsonStr = readJsonUrl(blogUrlPattern.getFullUrlTag().toString());
			JSONParser jparser1 = new JSONParser();
			JSONObject jobj1 = (JSONObject)jparser1.parse(jsonStr);

			Iterator iter =jobj1.keySet().iterator();
			while(iter.hasNext()){
				String k1 = (String)iter.next();
				Object o1 = jobj1.get(k1);

				if(o1 instanceof ArrayList){
					JSONArray jarr = (JSONArray)o1;
					for(Object o2 : jarr){
						JSONParser jparser2 = new JSONParser();

						JSONObject jobj2 = (JSONObject)jparser2.parse(o2.toString());
						Iterator iter2 =jobj2.keySet().iterator();
						while(iter2.hasNext()){
							String k2 = (String) iter2.next();
							String o3 = jobj2.get(k2).toString();
							if(k2.equals(NeBotCodes.BLOG_NAVER_TAGNAME_KEY_JSON)){
								String deStr = URLDecoder.decode(o3, "UTF-8");
								String delims = "[" + NeBotCodes.COMMA +"]";
								String[] tags = deStr.split(delims);
								for(String tag : tags){
									if(under_length_limit(tag)){
										setEntry(tag, NeBotCodes.NULLSTR, NeBotCodes.NE_TYPE_BLOG_TAG, url, entrys, blogUrlPattern.getTitle());
									}
								}

							}
						}
					}
				}
			}
		}catch(ParseException e){
			LOGGER.error("parsing error", e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("file encoding error", e);
		}
	}
	private BlogUrlPattern parseBlogUrl(SingleUrl singleUrl){
		String url = singleUrl.url;
		BlogUrlPattern blogUrlPattern  = new BlogUrlPattern();
		String blogIdVal="";
		String blogPostNoVal="";

		if(url.contains(NeBotCodes.BLOG_NAVER_COM)){
			if(url.contains(NeBotCodes.BLOG_NAVER_COM) && url.contains(NeBotCodes.QUEST)){
				blogIdVal = url.substring(url.indexOf(NeBotCodes.BLOG_NAVER_COM)+NeBotCodes.BLOG_NAVER_COM.length(),url.indexOf(NeBotCodes.QUEST));
			}
			if(url.contains(NeBotCodes.BLOG_NAVER_COM_POSTNO) && url.contains(NeBotCodes.AMPERSAND)){
				blogPostNoVal = url.substring(url.indexOf(NeBotCodes.BLOG_NAVER_COM_POSTNO)+NeBotCodes.BLOG_NAVER_COM_POSTNO.length(),url.lastIndexOf(NeBotCodes.AMPERSAND));
			}
			//BLOG.NAVER.COM : http://blog.naver.com/danyob?Redirect=Log&logNo=30171924606&from=section
			blogUrlPattern.getFullUrlPost().append(NeBotCodes.BLOG_NAVER_COM);
			blogUrlPattern.getFullUrlPost().append(NeBotCodes.SLASH);
			blogUrlPattern.getFullUrlPost().append(blogIdVal);
			blogUrlPattern.getFullUrlPost().append(NeBotCodes.BLOG_NAVER_POST_URL_PREFIX);
			blogUrlPattern.getFullUrlPost().append(blogPostNoVal);
		}else if(url.contains(NeBotCodes.BLOG_ME)){
			if(url.contains(NeBotCodes.SLASH)){
				blogPostNoVal = url.substring(url.lastIndexOf(NeBotCodes.SLASH)+1);
			}
			if(url.contains(NeBotCodes.HTTP_HEAD) && url.contains(NeBotCodes.BLOG_ME)){
				blogIdVal = url.substring(url.indexOf(NeBotCodes.HTTP_HEAD)+NeBotCodes.HTTP_HEAD.length(),url.indexOf(NeBotCodes.BLOG_ME));
			}
			//BLOG.ME : http://dahamida.blog.me/10172311918
			blogUrlPattern.getFullUrlPost().append(NeBotCodes.HTTP_HEAD);
			blogUrlPattern.getFullUrlPost().append(blogIdVal);
			blogUrlPattern.getFullUrlPost().append(NeBotCodes.BLOG_ME);
			blogUrlPattern.getFullUrlPost().append(NeBotCodes.SLASH);
			blogUrlPattern.getFullUrlPost().append(blogPostNoVal);


		}
		blogUrlPattern.setBlogIdVal(blogIdVal);
		blogUrlPattern.setBlogPostNoVal(blogPostNoVal);
		blogUrlPattern.setBaseUrlTag(singleUrl.baseUrl);

		blogUrlPattern.appendFullUrlTag(singleUrl.baseUrl);

		blogUrlPattern.appendFullUrlTag(singleUrl.blogId);
		blogUrlPattern.appendFullUrlTag(NeBotCodes.EQUAL);
		blogUrlPattern.appendFullUrlTag(blogIdVal);
		blogUrlPattern.appendFullUrlTag(NeBotCodes.AMPERSAND);

		blogUrlPattern.appendFullUrlTag(singleUrl.blogPostNo);
		blogUrlPattern.appendFullUrlTag(NeBotCodes.EQUAL);
		blogUrlPattern.appendFullUrlTag(blogPostNoVal);

		blogUrlPattern.setTitle(singleUrl.title);
		return blogUrlPattern;
	}

	private StringBuilder mergeNePrintInfo(String filterType, NeEntry ne) {

		String pairA = ne.getPairA();
		String pairB = ne.getPairB();

		if (pairA.length() > 0) {
			while (pairA.contains(NeBotCodes.SPACE)) {
				pairA = pairA.replace(NeBotCodes.SPACE, NeBotCodes.UNDERBAR);
			}
		} else {
			pairA = NeBotCodes.NULLSTR;
		}

		if (pairB.length() < 1) {
			pairB = NeBotCodes.NULLSTR;
		}

		String siteInfo = ne.getSiteInfo();
		while (siteInfo.contains(NeBotCodes.UNDERBAR)) {
			siteInfo = siteInfo.replace(NeBotCodes.UNDERBAR, NeBotCodes.TAB);
		}

		StringBuilder sbNe = new StringBuilder();
		sbNe.append(filterType);
		sbNe.append(NeBotCodes.TAB);

		sbNe.append(siteInfo);
		sbNe.append(NeBotCodes.TAB);

		sbNe.append(ne.getCrwalDate());
		sbNe.append(NeBotCodes.TAB);

		sbNe.append(ne.getNeType());
		sbNe.append(NeBotCodes.TAB);

		sbNe.append(pairA);
		sbNe.append(NeBotCodes.TAB);

		sbNe.append(NeBotCodes.PAREN_LEFT);
		sbNe.append(pairB);
		sbNe.append(NeBotCodes.PAREN_RIGHT);
		sbNe.append(NeBotCodes.TAB);

		sbNe.append(ne.getSentence());
		sbNe.append(NeBotCodes.TAB);

		sbNe.append(ne.getUrl());
		sbNe.append(NeBotCodes.TAB);

		sbNe.append(ne.getWriteDate());
		sbNe.append(NeBotCodes.TAB);
		sbNe.append(NeBotCodes.NEWLINE);
		return sbNe;
	}
	private void printNe(SingleUrl url, List<NeEntry> entrys) {

		for(NeEntry ne : entrys){
			String pairA = ne.getPairA();
			pairA = pairA.trim();
			String pairB = ne.getPairB();
			pairB = pairB.trim();


			StringBuilder log;
			StringBuilder sbNe = new StringBuilder();
			if(is_stopwords(pairA)) {
				log = mergeNePrintInfo(NeBotCodes.STPWD_STOPWD, ne);
				LOGGER.debug(log);
			}else if(!under_length_limit(pairA)) {
				log = mergeNePrintInfo(NeBotCodes.STPWD_LENGTH, ne);
				LOGGER.debug(log);
			}else{
				if(duplicateWithSysdic(pairA)){
					if(printSysdicDup) {
						sbNe = mergeNePrintInfo(NeBotCodes.STPWD_SYSDUP, ne);
					}
					LOGGER.debug(sbNe);
				}else if(is_asciiOnlyWord(pairA)){
					if(printAsciiOnly) {
						sbNe = mergeNePrintInfo(NeBotCodes.NE_ASCII, ne);
					}
					LOGGER.debug(sbNe);
				}else if(duplicate_with_prev(pairA,url.channel)){
					if(printPrv) {
						sbNe = mergeNePrintInfo(NeBotCodes.NE_PRVDUP, ne);
					}
					LOGGER.debug(sbNe);
				}else if(duplicate_with_tstore(pairA)){
					if(printTstore) {
						sbNe = mergeNePrintInfo(NeBotCodes.NE_TSTDUP, ne);
					}
					LOGGER.debug(sbNe);
				}else if(duplicate_with_skcomz(pairA)){
					if(printSkcomz) {
						sbNe = mergeNePrintInfo(NeBotCodes.NE_COMZDUP, ne);
					}
					LOGGER.debug(sbNe);
				}else {
					sbNe = mergeNePrintInfo(NeBotCodes.NE_NEW, ne);
					LOGGER.debug(sbNe);
				}
			}
		}
		entrys.clear();
	}
	private void saveNe(SingleUrl url, List<NeEntry> entrys) {
		try{
			DateFormat  sdFormat = new SimpleDateFormat("yyyyMMdd");
			Date nowDate = new Date();
			String strDate = sdFormat.format(nowDate);

			if(!url.crawlDate.isEmpty()){
				strDate = url.crawlDate;
			}
			BufferedWriter bw;
			BufferedWriter bwHis;
			if (url.channel.toUpperCase().contains(NeBotCodes.CHANNEL_BLOG)) {
				bw = new BufferedWriter(new FileWriter(outputBlog + "_" + strDate + ".tab", true));
				bwHis = new BufferedWriter(new FileWriter(historyBlog, true));
			} else {
				bw = new BufferedWriter(new FileWriter(outputNews + "_" + strDate + ".tab", true));
				bwHis = new BufferedWriter(new FileWriter(historyNews, true));
			}

			for(NeEntry ne : entrys){
				String pairA = ne.getPairA();
				pairA = pairA.trim();
				//String pairB = ne.pairB;

				ne.setCrwalDate(strDate);

				StringBuilder sbNe;
				if(is_stopwords(pairA)) {
					LOGGER.debug(mergeNePrintInfo(NeBotCodes.STPWD_STOPWD, ne));
				}else if(!under_length_limit(pairA)) {
					LOGGER.debug(mergeNePrintInfo(NeBotCodes.STPWD_LENGTH, ne));
				}else{
					if(duplicateWithSysdic(pairA)){
						if(printSysdicDup) {
							sbNe = mergeNePrintInfo(NeBotCodes.STPWD_SYSDUP, ne);
							bw.write(sbNe.toString());
							LOGGER.debug(sbNe);
						}
					}else if(is_asciiOnlyWord(pairA)){
						if(printAsciiOnly) {
							sbNe = mergeNePrintInfo(NeBotCodes.NE_ASCII, ne);
							bw.write(sbNe.toString());
							LOGGER.debug(sbNe);
						}
					}else if(duplicate_with_prev(pairA,url.channel)){
						if(printPrv) {
							sbNe = mergeNePrintInfo(NeBotCodes.NE_PRVDUP, ne);
							bw.write(sbNe.toString());
							LOGGER.debug(sbNe);
						}
					}else if(duplicate_with_tstore(pairA)){
						if(printTstore) {
							sbNe = mergeNePrintInfo(NeBotCodes.NE_TSTDUP, ne);
							bw.write(sbNe.toString());
							LOGGER.debug(sbNe);
						}
					}else if (duplicate_with_skcomz(pairA)) {
						if (printSkcomz) {
							sbNe = mergeNePrintInfo(NeBotCodes.NE_COMZDUP, ne);
							bw.write(sbNe.toString());
							LOGGER.debug(sbNe);
						}
					} else {

						sbNe = mergeNePrintInfo(NeBotCodes.NE_NEW, ne);
						bw.write(sbNe.toString());
						LOGGER.debug(sbNe);

						bwHis.write(pairA);
						bwHis.write(NeBotCodes.NEWLINE);
					}
				}
			}
			bw.close();
			bwHis.close();
			entrys.clear();
		}catch(FileNotFoundException e){
			LOGGER.error("file not found", e);
		}catch(IOException e){
			LOGGER.error("i/o error", e);
		}
	}

	private int extractSqureBracketNe(int oldI, String sent, SingleUrl url, List<NeEntry> entrys) {

		int i = oldI;
		char[] chArr = sent.toCharArray();
		int size = chArr.length;

		StringBuilder pairA = new StringBuilder();
		StringBuilder pairB = new StringBuilder();

		i++;
		while (i < size && chArr[i] != NeBotCodes.SQURE_BRACKET_RIGHT.toCharArray()[0]) {
			pairA.append(chArr[i]);
			i++;
		}
		i++;
		setEntry(pairA.toString(), pairB.toString(), NeBotCodes.NE_TYPE_SQBRKT_A, url, entrys, sent);
		return i;

	}

	private int extractAngleBracketNe(int oldI, String sent, SingleUrl url, List<NeEntry> entrys) {

		int i = oldI;
		char[] chArr = sent.toCharArray();
		int size = chArr.length;

		StringBuilder pairA = new StringBuilder();
		StringBuilder pairB = new StringBuilder();

		i++;
		while (i < size && chArr[i] != NeBotCodes.ANGLE_BRACKET_RIGHT.toCharArray()[0]) {
			pairA.append(chArr[i]);
			i++;
		}
		i++;
		if (i < size && chArr[i] == NeBotCodes.PAREN_LEFT.toCharArray()[0]) {
			i++;
			while (i < size && chArr[i] != NeBotCodes.PAREN_RIGHT.toCharArray()[0]) {
				pairB.append(chArr[i]);
				i++;
			}
			i++;

			setEntry(pairA.toString(), pairB.toString(), NeBotCodes.NE_TYPE_ANBRKT_A_AND_B, url, entrys, sent);
		} else {
			setEntry(pairA.toString(), pairB.toString(), NeBotCodes.NE_TYPE_ANBRKT_A, url, entrys, sent);
		}
		return i;
	}

	private int extractParenthesesNe(int i, String sent, SingleUrl url, List<NeEntry> entries) {

		char[] chArr = sent.toCharArray();
		int size = chArr.length;

		StringBuilder pairA = new StringBuilder();
		StringBuilder pairB = new StringBuilder();

		int k = i - 1;
		while (k >= 0 && chArr[k] != ' ') {
			k--;
		}
		for (int m = k + 1; m < i && m >= 0 && m < size; m++) {
			pairA.append(chArr[m]);
		}
		int j = i + 1;
		while (j < size && chArr[j] != NeBotCodes.PAREN_RIGHT.toCharArray()[0]) {
			pairB.append(chArr[j]);
			j++;
		}
		j++;
		i = j;
		setEntry(pairA.toString(), pairB.toString(), NeBotCodes.NE_TYPE_PAREN_A_AND_B, url, entries, sent);
		return i;
	}

	private int extractSingleQuotNe(int oldI, String sent, SingleUrl url, List<NeEntry> entrys) {

		int i = oldI;
		char[] chArr = sent.toCharArray();
		int size = chArr.length;

		StringBuilder pairA = new StringBuilder();
		StringBuilder pairB = new StringBuilder();
		i++;
		while (i < size && chArr[i] != NeBotCodes.SINGLE_QUOT.toCharArray()[0]) {
			pairA.append(chArr[i]);
			i++;
		}
		if (!" ".equals(pairA.toString())) {
			i++;
		}
		if (i < size && chArr[i] == NeBotCodes.PAREN_LEFT.toCharArray()[0]) {
			i++;
			while (i < size && chArr[i] != NeBotCodes.PAREN_RIGHT.toCharArray()[0]) {
				pairB.append(chArr[i]);
				i++;
			}

			i++;

			if (i < size && chArr[i] == NeBotCodes.SINGLE_QUOT.toCharArray()[0]) {
				i++;
			}

			setEntry(pairA.toString(), pairB.toString(), NeBotCodes.NE_TYPE_SQUTO_A_AND_B, url, entrys, sent);
		} else {
			if (pairA.toString().contains(NeBotCodes.PAREN_LEFT)) {
				String tmpPairA = pairA.substring(0, pairA.toString().indexOf(NeBotCodes.PAREN_LEFT));
				String tmpPairB = "";
				if (pairA.toString().contains(NeBotCodes.PAREN_RIGHT)
						&& pairA.toString().indexOf(NeBotCodes.PAREN_RIGHT) > pairA.toString().indexOf(NeBotCodes.PAREN_LEFT)) {
					tmpPairB = pairA.substring(pairA.toString().indexOf(NeBotCodes.PAREN_LEFT) + 1, pairA.toString().indexOf(NeBotCodes.PAREN_RIGHT));
				} else {
					tmpPairB = pairA.substring(pairA.toString().indexOf(NeBotCodes.PAREN_LEFT) + 1);
				}
				setEntry(tmpPairA.toString(), tmpPairB.toString(), NeBotCodes.NE_TYPE_SQUTO_B_IN_A, url, entrys, sent);
			} else {
				setEntry(pairA.toString(), pairB.toString(), NeBotCodes.NE_TYPE_SQUTO_A, url, entrys, sent);
			}
		}
		return i;
	}

	private void extractNe(SingleUrl url, List<String> sents, List<NeEntry> entrys) {

		entrys.clear();
		for (String sent : sents) {
			LOGGER.debug("SENT : " + sent);
			char[] chArr = sent.toCharArray();
			int size = chArr.length;
			int i = 0;
			while (i < size) {
				if (chArr[i] == NeBotCodes.SINGLE_QUOT.toCharArray()[0] && singleQuotation) {
					i = extractSingleQuotNe(i, sent, url, entrys);
				} else if (chArr[i] == NeBotCodes.PAREN_LEFT.toCharArray()[0] && parentheses) {
					i = extractParenthesesNe(i, sent, url, entrys);
				} else if (chArr[i] == NeBotCodes.ANGLE_BRACKET_LEFT.toCharArray()[0] && angleBracket) {
					i = extractAngleBracketNe(i, sent, url, entrys);
				} else if (chArr[i] == NeBotCodes.SQURE_BRACKET_LEFT.toCharArray()[0] && squareBracket) {
					i = extractSqureBracketNe(i, sent, url, entrys);
				} else {
					i++;
				}
			}
		}
		sents.clear();
	}

	private void setEntry(String pairA, String pairB, String neType,SingleUrl url, List<NeEntry> entrys, String sent){
		NeEntry ne = new NeEntry();
		ne.setCrwalDate(url.crawlDate);
		ne.setWriteDate(url.writeDate);
		ne.setSiteInfo(url.siteInfo);
		ne.setUrl(url.url);
		ne.setSentence(sent);
		ne.setNeType(neType);
		ne.setPairA(trimStr(pairA));
		ne.setPairB(trimStr(pairB));
		entrys.add(ne);
	}
	private String trimStr(String str) {
		String newString = new String(str);
		while (newString.length() > 0 && newString.indexOf(' ') == 0) {
			newString = newString.substring(1);
		}

		while (newString.length() > 0 && newString.lastIndexOf(' ') == newString.length() - 1) {
			newString = newString.substring(0, newString.lastIndexOf(' '));
		}
		return newString;
	}

	private void extractText(SingleUrl url, List<String> sents) {

		sents.clear();
		String myUrl = url.url;

		String downedUrlPath = downloadedUrl;

		if (Resource.dicNebot.isDownloadedUrl(myUrl)) {
			LOGGER.debug("Duplicated URLs : " + myUrl);
		} else {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(downedUrlPath, true));
				bw.write(myUrl);
				bw.newLine();
				LOGGER.debug("URL: " + myUrl);
				Document docUrl = Jsoup.connect(myUrl).timeout(timeOut * THOUSAND).get();
				Elements eBodySelector;
				Elements eTitle;
				Elements eDate;

				if (!url.bodySelector.isEmpty()) {
					eBodySelector = docUrl.select(url.bodySelector);

					if (!url.titleSelector.isEmpty()) {
						eTitle = docUrl.select(url.titleSelector);
						for (Element e : eTitle) {
							String title = e.text();
							int ret = isProperText(url.bodyEndingText, title);
							if (ret > 0) {
								sents.add(title);
							} else {
								break;
							}
						}
					}

					if (!url.dateSelector.isEmpty()) {
						eDate = docUrl.select(url.dateSelector);
						for (Element e : eDate) {
							url.writeDate = e.text();
						}
					}
					for (Element eBody : eBodySelector) {
						String[] arrSents;
						String body = eBody.text();
						body = cleanText(body);
						if (body.length() > 0) {
							if (doSegment && this.segmenter != null) {
								arrSents = segmenter.segment_sentences(body, true);
							} else {
								arrSents = new String[1];
								arrSents[0] = body;
							}
							for (String sent : arrSents) {
								sent = trimRight(sent);
								int ret = isProperText(url.bodyEndingText, sent);
								if (ret > -1) {
									if (ret > 0) {
										sents.add(sent);
									}
								} else {
									break;
								}

							}
						}
					}
				}
				bw.close();

			} catch (FileNotFoundException e) {
				LOGGER.error("file not found", e);
			} catch (SocketTimeoutException e) {
				LOGGER.error("socket error", e);
			} catch (IOException e) {
				LOGGER.error("i/o error", e);
			}
		}
	}

	private String trimRight(String str){
		String newStr;
		newStr = str.trim();
		if(newStr.contains("[") && (newStr.indexOf('[') == (newStr.length() - 1))) {
				newStr = newStr.substring(0, newStr.length() - 1);
		}
		return newStr;
	}

	private int isProperText(List<String> bodyEndingText, String sent) {

		for (String endMark : bodyEndingText) {
			if (sent.contains(endMark)) {
				return -1;
			}
		}
		return containRightSymbal(sent);
	}
	private int isProperText(String sent ){
		return containRightSymbal(sent);
	}
	private int containRightSymbal(String sent){
		if(sent.contains(NeBotCodes.SINGLE_QUOT)){
			return 1;
		}else if(sent.contains(NeBotCodes.PAREN_LEFT)){
			return 1;
		}else if(sent.contains(NeBotCodes.ANGLE_BRACKET_LEFT)){
			return 1;
		}else if(sent.contains(NeBotCodes.SQURE_BRACKET_LEFT)){
			return 1;
		}else{
			return 0;
		}
	}

	private void extractUrl(String siteInfo, SeedUrlPattern seed, List<SingleUrl> urlPool) {

		LOGGER.debug("Step 2_iteration : extracting urls...");
		if (seed.naviType.equals(NeBotCodes.NAVITYPE_SELF)) {
			Map<String, SingleUrl> mapUrl = new HashMap<String, SingleUrl>();
			extractNewsUrl(siteInfo, seed.seedUrl, seed, mapUrl);
			addUrlPool(mapUrl, urlPool);
		} else {
			List<String> seedPages = (ArrayList<String>) collectGetTypeSeedPages(seed).clone();

			Map<String, SingleUrl> mapUrl = new HashMap<String, SingleUrl>();
			for (String url : seedPages) {
				if (seed.jsonFormat) {
					extractNewsUrlJson(siteInfo, url, seed, mapUrl);
				} else {
					extractNewsUrl(siteInfo, url, seed, mapUrl);
				}
			}
			addUrlPool(mapUrl, urlPool);
			seedPages.clear();
		}

	}

	private void addUrlPool(Map<String, SingleUrl> mapUrl, List<SingleUrl> urlPool) {
		Iterator<String> iter = mapUrl.keySet().iterator();
		while (iter.hasNext()) {
			String urlKey = iter.next();
			SingleUrl tmp = mapUrl.get(urlKey);
			urlPool.add(tmp);
		}
		mapUrl.clear();
	}

	private void extractNewsUrlJson(String siteInfo, String pageUrl, SeedUrlPattern seed, Map<String, SingleUrl> mapUrl) {

		try {
			Set<String> uniqUrls = new HashSet<String>();

			String urlListStr = readJsonUrl(pageUrl);
			JSONParser jparser1 = new JSONParser();
			JSONObject jobj1 = (JSONObject) jparser1.parse(urlListStr);

			Iterator iter = jobj1.keySet().iterator();
			while (iter.hasNext()) {
				String k1 = (String) iter.next();
				Object o1 = jobj1.get(k1);

				if (o1 instanceof ArrayList) {
					JSONArray jarr = (JSONArray) o1;
					for (Object o2 : jarr) {
						JSONParser jparser2 = new JSONParser();

						JSONObject jobj2 = (JSONObject) jparser2.parse(o2.toString());
						Iterator iter2 = jobj2.keySet().iterator();
						while (iter2.hasNext()) {
							String k2 = (String) iter2.next();
							String o3 = jobj2.get(k2).toString();
							if (k2.equals(seed.jsonKeyForUrl)) {
								uniqUrls.add(seed.baseUrl + "/" + o3);
							}
						}
					}
				} else {
					if (k1.equals(seed.jsonKeyForUrl)) {
						uniqUrls.add(seed.baseUrl + "/" + o1.toString());
					}
				}
			}
			for (String url : uniqUrls) {
				addUrlToMapper(siteInfo, seed, url, mapUrl);
			}
			uniqUrls.clear();
		} catch (ParseException e) {
			LOGGER.error("parsing error", e);
		}
	}

	private void addUrlToMapper(String siteInfo, SeedUrlPattern seed, String url, Map<String, SingleUrl> mapUrl) {

		DateFormat sdFormat = new SimpleDateFormat(seed.dateFormat);
		Date nowDate = new Date();
		String strDate = sdFormat.format(nowDate);

		SingleUrl surl = new SingleUrl();
		if (!seed.crawlDate.isEmpty()) {
			strDate = seed.crawlDate;
		}
		surl.crawlDate = strDate;
		surl.siteInfo = siteInfo;
		surl.bodyId = seed.bodyId;
		surl.bodyEndingText = seed.bodyEndingText;
		surl.bodySelector = seed.bodySelector;
		surl.titleSelector = seed.titleSelector;
		surl.dateSelector = seed.dateSelector;
		if (url.contains(NeBotCodes.BLOG_NAVER_TITLE_DELIM)) {
			surl.url = url.substring(0, url.indexOf(NeBotCodes.BLOG_NAVER_TITLE_DELIM));
			surl.title = url.substring(url.indexOf(NeBotCodes.BLOG_NAVER_TITLE_DELIM) + NeBotCodes.BLOG_NAVER_TITLE_DELIM.length());
		} else {
			surl.url = url;
		}

		surl.blogId = seed.blogId;
		surl.blogPostNo = seed.blogPostNo;
		surl.baseUrl = seed.baseUrl;
		surl.channel = seed.channel;
		mapUrl.put(url, surl);
	}

	private void extractNewsUrl(String siteInfo, String pageUrl, SeedUrlPattern seed, Map<String, SingleUrl> mapUrl) {

		try {
			BufferedWriter bwDown = new BufferedWriter(new FileWriter(downloadedUrl, true));

			Document docUrl = Jsoup.connect(pageUrl).timeout(timeOut * THOUSAND).get();

			Element eById;
			Elements eByClass;
			String htmlList = "";
			Set<String> uniqUrls = new HashSet<String>();

			if (!seed.listId.isEmpty()) {
				eById = docUrl.getElementById(seed.listId);
				htmlList = eById.html();

			} else if (!seed.listSelector.isEmpty()) {
				eByClass = docUrl.select(seed.listSelector);
				htmlList = eByClass.html();
			}

			Document docStr = Jsoup.parse(htmlList);
			Elements links = docStr.select(NeBotCodes.LINK_TAG);
			for (Element link : links) {

				if (seed.channel.equalsIgnoreCase(NeBotCodes.CHANNEL_BLOG)) {
					for (String domainPatten : seed.blogDomain) {
						String blogurl = link.attr(NeBotCodes.URL_ATTR_ABS);
						String blogTitle = link.text();
						if (blogurl.contains(domainPatten)) {
							LOGGER.debug("Step 2_SUB urls :" + blogurl);
							LOGGER.debug("Step 2_SUB title :" + blogTitle);
							if (Resource.dicNebot.isDownloadedUrl(blogurl)) {
								LOGGER.debug("Duplicated URLs : " + blogurl);
							} else {
								LOGGER.debug("New URLs : " + blogurl);
								uniqUrls.add(blogurl + NeBotCodes.BLOG_NAVER_TITLE_DELIM + blogTitle);
							}
						}
					}
				} else {
					if (seed.baseUrl.isEmpty()) {
						uniqUrls.add(link.attr(NeBotCodes.URL_ATTR_ABS));
					} else {
						uniqUrls.add(seed.baseUrl + "/" + link.attr(NeBotCodes.URL_ATTR_REL));
					}
				}
			}
			bwDown.close();
			for (String url : uniqUrls) {
				addUrlToMapper(siteInfo, seed, url, mapUrl);
			}
			uniqUrls.clear();
		} catch (HttpStatusException e) {
			LOGGER.error("JSOUP error!", e);
		} catch (IOException e) {
			LOGGER.error("I/O problem", e);
		}
	}

	private String makeUrlTreeTypePage(int i,SeedUrlPattern seed){
		StringBuilder seedUrl = new StringBuilder();
		seedUrl.append(seed.seedUrl);
		seedUrl.append("/");
		seedUrl.append(seed.page);
		seedUrl.append("/");
		seedUrl.append(String.valueOf(i));
		return seedUrl.toString();
	}
	private String makeUrlGetTypePageDate(int i,SeedUrlPattern seed){
		StringBuilder seedUrl = new StringBuilder();
		DateFormat  sdFormat = new SimpleDateFormat(seed.dateFormat);
		Date nowDate = new Date();
		String strDate = sdFormat.format(nowDate);
		seedUrl.append(seed.seedUrl);
		seedUrl.append("&");
		seedUrl.append(seed.date);
		seedUrl.append("=");

		if(!seed.crawlDate.isEmpty()){
			strDate = seed.crawlDate;
		}
		seedUrl.append(strDate);
		seedUrl.append("&");
		seedUrl.append(seed.page);
		seedUrl.append("=");
		seedUrl.append(String.valueOf(i));
		return seedUrl.toString();
	}
	private String makeUrlGetTypePage(int i,SeedUrlPattern seed){
		StringBuilder seedUrl = new StringBuilder();
		seedUrl.append(seed.seedUrl);
		seedUrl.append("&");
		seedUrl.append(seed.page);
		seedUrl.append("=");
		seedUrl.append(String.valueOf(i));
		return seedUrl.toString();
	}

	private String makeUrlGetTypeDate(int i, SeedUrlPattern seed) {

		DateFormat sdFormat = new SimpleDateFormat(seed.dateFormat);
		Date nowDate = new Date();
		String strDate = sdFormat.format(nowDate);

		StringBuilder seedUrl = new StringBuilder();
		seedUrl.append(seed.seedUrl);
		seedUrl.append("&");
		seedUrl.append(seed.date);
		seedUrl.append("=");
		if (!seed.crawlDate.isEmpty()) {
			strDate = seed.crawlDate;
		}
		seedUrl.append(strDate);

		return seedUrl.toString();
	}
	private List<String> collectTreeTypeSeedPages(SeedUrlPattern seed){
		List<String> seedPages = new ArrayList<String>();
		int i=0;
		if(!seed.page.isEmpty()){
			for(i=1;i<=seed.maxIter;i++){
				seedPages.add(makeUrlTreeTypePage(i, seed));
			}
		}
		return seedPages;
	}
	private ArrayList<String> collectGetTypeSeedPages(SeedUrlPattern seed){
		List<String> seedPages = new ArrayList<String>();
		int i=0;
		if (!seed.date.isEmpty() && !seed.page.isEmpty()) {
			for (i = 1; i <= seed.maxIter; i++) {
				seedPages.add(makeUrlGetTypePageDate(i, seed));
			}
		} else {
			if (!seed.page.isEmpty()) {
				for (i = 1; i <= seed.maxIter; i++) {
					LOGGER.debug("seed page : " + makeUrlGetTypePage(i, seed));
					seedPages.add(makeUrlGetTypePage(i, seed));
				}
			} else if (!seed.date.isEmpty()) {
				for (i = 1; i <= seed.maxIter; i++) {
					seedPages.add(makeUrlGetTypePageDate(i, seed));
				}
			}
		}
		return (ArrayList<String>) seedPages;
	}
	private void parseSeedPattern(){
		LOGGER.debug("Step 1 : parsing url pattern file[json]....");
		String seedPatternJsonStr = readProperty();
		JSONParser jparser = new JSONParser();
		try {
			JSONObject jobj = (JSONObject) jparser.parse(seedPatternJsonStr);
			Iterator iter = jobj.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				urlseeds.add(setSeeds(key, jobj));
			}
		} catch (ParseException e) {
			LOGGER.error("parsing error", e);
		}
	}

	private SeedUrlPattern setSeeds(String key, JSONObject jobj) {
		JSONObject obj = (JSONObject) jobj.get(key);
		Iterator iter = obj.keySet().iterator();
		SeedUrlPattern seed = new SeedUrlPattern();
		seed.seedInfo = key;
		while (iter.hasNext()) {
			String keyItem = (String) iter.next();
			if (keyItem.equalsIgnoreCase(NeBotCodes.SEEDURL)) {
				seed.seedUrl = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.CHANNEL)) {
				seed.channel = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.BLOGID)) {
				seed.blogId = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.BLOGPOSTNO)) {
				seed.blogPostNo = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.CRAWLDATE)) {
				seed.crawlDate = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.VAR_DATE)) {
				seed.date = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.VAR_PAGE)) {
				seed.page = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.MAXITER)) {
				seed.maxIter = Integer.valueOf(obj.get(keyItem).toString());
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.BASEURL)) {
				seed.baseUrl = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.DATEFORMAT)) {
				seed.dateFormat = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.NAVITYPE)) {
				seed.naviType = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.JSONKEYFORURL)) {
				seed.jsonKeyForUrl = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.JSONFORMAT)) {
				seed.jsonFormat = Boolean.valueOf(obj.get(keyItem).toString());
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.LISTSELECTOR)) {
				seed.listSelector = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.LISTID)) {
				seed.listId = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.BODYSELECTOR)) {
				seed.bodySelector = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.BODYID)) {
				seed.bodyId = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.TITLESELECTOR)) {
				seed.titleSelector = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.DATESELECTOR)) {
				seed.dateSelector = ((String) obj.get(keyItem)).trim();
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.BODYENDINGTEXT)) {
				if (obj.get(keyItem) instanceof ArrayList) {
					seed.bodyEndingText = (ArrayList<String>) obj.get(keyItem);
				}
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.BLOGDOMAIN)) {
				if (obj.get(keyItem) instanceof ArrayList) {
					seed.blogDomain = (ArrayList<String>) obj.get(keyItem);
				}
			} else if (keyItem.equalsIgnoreCase(NeBotCodes.RUN)) {
				seed.run = Boolean.valueOf(obj.get(keyItem).toString());
			}
		}
		return seed;
	}
	private String readProperty(){
		StringBuilder sb = new StringBuilder();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(this.seed));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			br.close();
		}catch(FileNotFoundException e){
			LOGGER.error("file not found", e);
		}catch(IOException e){
			LOGGER.error("i/o error", e);
		}
		return sb.toString();
	}
	private String readJsonUrl(String path){
		StringBuilder sb = new StringBuilder();
		try{
			InputStream is = new URL(path).openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,Charset.forName("UTF-8")));
			String line;
			while((line=br.readLine())!= null){
				sb.append(line);
			}
			is.close();
		}catch(FileNotFoundException e){
			LOGGER.error("file not found", e);
		}catch(IOException e){
			LOGGER.error("i/o error", e);
		}
		return sb.toString();
	}
	private String cleanText(String strDoc) {
		return strDoc.replaceAll("[‘’`′]", "")
				.replace(",", ", ")
				.replace("..", ".. ")
                .replace("...", "... ")
                .replace("…", "... ");
	}
	private boolean duplicateWithSysdic(String str){
		return Resource.dicNebot.isSysdic(str);
	}
	private boolean is_asciiOnlyWord(String str){
		char chArr[] = str.toCharArray();
		for(char ch : chArr){
			if(ch > 0x7f){
				return false;
			}
		}
		return true;
	}
	private boolean under_length_limit(String str){
		return(str.length()> 1 && str.length() <= this.termMaxLength);
	}
	private boolean is_stopwords(String str){
		if(Resource.dicNebot.isStopwords(str)){
			return true;
		}else{
			for(String stopwordsSubmatch : Resource.dicNebot.getStopwordsSubMatchDic()){
				if(str.contains(stopwordsSubmatch)){
					return true;
				}
			}
			return false;
		}
	}
	private boolean duplicate_with_prev(String str, String channel){
		if(channel.contains(NeBotCodes.CHANNEL_BLOG)){
			return (Resource.dicNebot.isHistoryNeBlog(str));
		}else{
			return (Resource.dicNebot.isHistoryNeNews(str));
		}
	}
	private boolean duplicate_with_tstore(String str){
		return (Resource.dicNebot.isTstoreNe(str));
	}
	private boolean duplicate_with_skcomz(String str){
		return (Resource.dicNebot.isSkcomzNe(str));
	}


}



