package com.skplanet.nlp.acronym;

import java.util.HashSet;
import java.util.Set;

public class NeEntry {
	public boolean isAcronym;
	public int frq;
	public String filterType;
	public String site;
	public String bigCategory;
	public String midCategory;
	public String channel;
	
	public String patternType;
	public String pairA;
	public Set<String> pairAMulti;
	public String pairARefine;
	public String pairB;
	public boolean hasAcronym;
	public HashSet<String> acronyms;
	public String acroCommonKey;
	
	public String url;
	public StringBuilder sentence;
	public String crwalDate;
	public String writeDate;
	
	public NeEntry(){
		this.hasAcronym = false;
		this.isAcronym = false;
		this.acronyms = new HashSet<String>();
		this.sentence = new StringBuilder();
		this.pairAMulti = new HashSet<String>();
	}
	
}
