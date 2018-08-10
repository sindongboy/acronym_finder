package com.skplanet.nlp.nebot;

import java.util.ArrayList;
public class SingleUrl {
	public String siteInfo;
	public String url;
	public String baseUrl;
	public String channel;
	public String blogId;
	public String blogPostNo;

	public String bodyId;
	public String bodySelector;
	public String titleSelector;
	public String dateSelector;
	public ArrayList<String>	bodyEndingText;
	
	public String title;
	public String crawlDate;
	public String writeDate;
	
	public String toString(){
		StringBuilder sb =new StringBuilder();
		sb.append("siteInfo : "+this.siteInfo);
		sb.append("\n");
		sb.append("url : "+this.url);
		sb.append("\n");
		sb.append("bodyId : "+this.bodyId);
		sb.append("\n");
		sb.append("bodyEndingText : "+this.bodyEndingText);
		sb.append("\n");
		sb.append("bodySelector : "+this.bodySelector);
		sb.append("\n");
		sb.append("titleSelector : "+this.titleSelector);
		sb.append("\n");
		sb.append("dateSelector : "+this.dateSelector);
		sb.append("\n");
		sb.append("crawlDate : "+this.crawlDate);
		sb.append("\n");
		sb.append("writeDate : "+this.writeDate);
		sb.append("\n");
		
		return sb.toString();
	}
}
