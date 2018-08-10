package com.skplanet.nlp.nebot;

public class NeEntry {
	private String neType;
	private String pairA;
	private String pairB;
	private String siteInfo;
	private String url;
	private String sentence;
	private String crwalDate;
	private String writeDate;

	public NeEntry() {

	}

	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	public String getPairA() {
		return pairA;
	}

	public void setPairA(String pairA) {
		this.pairA = pairA;
	}

	public String getPairB() {
		return pairB;
	}

	public void setPairB(String pairB) {
		this.pairB = pairB;
	}

	public String getSiteInfo() {
		return siteInfo;
	}

	public void setSiteInfo(String siteInfo) {
		this.siteInfo = siteInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getCrwalDate() {
		return crwalDate;
	}

	public void setCrwalDate(String crwalDate) {
		this.crwalDate = crwalDate;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
}
