package com.skplanet.nlp.resource;

import java.io.*;
import java.util.HashSet;
import java.util.Set;


public class Dic {
	private Set<String> sStopwords;
	private Set<String> sStopwordsSubmatch;

	public Dic(){
		this.sStopwords = new HashSet<String>();
		this.sStopwordsSubmatch = new HashSet<String>();
	}

	public Set<String> getStopwordsDic(){
		return this.sStopwords;
	}
	public Set<String> getStopwordsSubMatchDic(){
		return this.sStopwordsSubmatch;
	}

    public void setDic(String filepath, Set<String> set) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath),"UTF-8"));
			String line;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if (!(line.startsWith("/") || line.startsWith("#"))) { // starts with /, # is comment line
					if(line.length() > 0){
						set.add(line);
					}
				}
			}
			reader.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println(filepath+"\tLoading done...");
	}
	
	public boolean isStopwords(String str){
		return(this.sStopwords.contains(str));
	}
}
