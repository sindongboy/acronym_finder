package com.skplanet.nlp.nebot;

import java.util.ArrayList;
public class SeedUrlPattern {
	public String seedInfo;
	public String seedUrl;
	public String channel;
	public String blogId;
	public String blogPostNo;
	public ArrayList<String> blogDomain;
	public String crawlDate;
	public String date;
	public String page;
	public int maxIter;
	public String baseUrl;
	public String dateFormat;
	public String naviType;
	public boolean jsonFormat;
	public String jsonKeyForUrl;
	public String listSelector;
	public String listId;
	public String bodySelector;
	public String bodyId;
	public String titleSelector;
	public String dateSelector;
	public ArrayList<String> bodyEndingText;
	
	public boolean run;
	
	public String toString(){
		StringBuilder sb =new StringBuilder();
		sb.append("seedInfo : "+this.seedInfo);
		sb.append("\n");
		sb.append("seedUrl : "+this.seedUrl);
		sb.append("\n");
		sb.append("channel : "+this.channel);
		sb.append("\n");
		sb.append("crawlDate : "+this.crawlDate);
		sb.append("\n");
		sb.append("var_date : "+this.date);
		sb.append("\n");
		sb.append("var_page : "+this.page);
		sb.append("\n");
		sb.append("maxIter : "+this.maxIter);
		sb.append("\n");
		sb.append("baseUrl : "+this.baseUrl);
		sb.append("\n");
		sb.append("dateFormat : "+ this.dateFormat);
		sb.append("\n");
		sb.append("naviType : "+this.naviType);
		sb.append("\n");
		sb.append("jsonFormat : "+ this.jsonFormat);
		sb.append("\n");
		sb.append("listSelector : "+this.listSelector);
		sb.append("\n");
		sb.append("listId : "+this.listId);
		sb.append("\n");
		sb.append("bodySelector : "+this.bodySelector);
		sb.append("\n");
		sb.append("bodyId : "+this.bodyId);
		sb.append("\n");
		sb.append("bodyEndingText : "+this.bodyEndingText);
		sb.append("\n");
		sb.append("titleSelector : "+this.titleSelector);
		sb.append("\n");
		sb.append("dateSelector : "+this.dateSelector);
		sb.append("\n");
		sb.append("run : "+this.run);
		sb.append("\n");
		return sb.toString();
	}
}
