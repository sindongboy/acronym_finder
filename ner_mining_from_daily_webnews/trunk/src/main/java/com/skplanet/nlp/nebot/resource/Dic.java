package com.skplanet.nlp.nebot.resource;

import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

public class Dic {
	private static final Logger LOGGER = Logger.getLogger(Dic.class.getName());
	public static final int MAX_LINE_LEN = 9;

	private Set<String> sStopwords;
	private Set<String> sStopwordsSubmatch;
	private Set<String> sHistoryNeNews;
	private Set<String> sHistoryNeBlog;
	private Set<String> sTstoreNe;
	private Set<String> sSkcomzNe;
	private Set<String> sSysdic;
	private Set<String> sUrlDownloaded;

	public Dic(){
		this.sStopwords = new HashSet<String>();
		this.sStopwordsSubmatch = new HashSet<String>();
		this.sSysdic = new HashSet<String>();
		this.sUrlDownloaded = new HashSet<String>();
		this.sHistoryNeNews = new HashSet<String>();
		this.sHistoryNeBlog = new HashSet<String>();
		this.sTstoreNe = new HashSet<String>();
		this.sSkcomzNe = new HashSet<String>();
	}

	public Set<String> getStopwordsDic(){
		return this.sStopwords;
	}
	public Set<String> getStopwordsSubMatchDic(){
		return this.sStopwordsSubmatch;
	}
	public Set<String> getHistoryNeDicNews(){
		return this.sHistoryNeNews;
	}
	public Set<String> getHistoryNeDicBlog(){
		return this.sHistoryNeBlog;
	}
	public Set<String> getTstoreNeDic(){
		return this.sTstoreNe;
	}

	public Set<String> getSkcomzNeDic(){
		return this.sTstoreNe;
	}

	public Set<String> getUrlDic(){
		return this.sUrlDownloaded;
	}

	public Set<String> getSysDic(){
		return this.sSysdic;
	}

	public void setDic(String filepath, Set<String> set){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath),"UTF-8"));
			String line;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				// starts with /, # is comment line
				if (line.length() > MAX_LINE_LEN && (!(line.startsWith("/") || line.startsWith("#")))) {
					set.add(line);
				}
			}
			reader.close();
		}catch(FileNotFoundException e){
			LOGGER.error("dictionary file not found: " + filepath, e);
		}catch(UnsupportedEncodingException e){
			LOGGER.error("dictionary encoding error", e);
		}catch(IOException e){
			LOGGER.error("i/o error occurred", e);
		}
		LOGGER.info(filepath + "\tLoading done...");
	}

	public boolean isStopwords(String str){
		return this.sStopwords.contains(str);
	}
	public boolean isHistoryNeNews(String str) {
		return this.sHistoryNeNews.contains(str);
	}
	public boolean isHistoryNeBlog(String str){
		return this.sHistoryNeBlog.contains(str);
	}
	public boolean isTstoreNe(String str) {
		return this.sTstoreNe.contains(str);
	}

	public boolean isSkcomzNe(String str) {
		return this.sSkcomzNe.contains(str);
	}
	public boolean isSysdic(String str) {
		return this.sSysdic.contains(str);
	}

	public boolean isDownloadedUrl(String str) {
		return this.sUrlDownloaded.contains(str);
	}

}
