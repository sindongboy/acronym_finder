package com.skplanet.nlp.acronym;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NeAcronymCodes {
	public static final String TAB = "\t";
	public static final String SPACE = " ";
	public static final String UNDERBAR = "_";
	public static final String DASH = "-";
	public static final String COLON = ":";
	public static final String COMMA = ",";
	public static final String AMPERSAND = "&";
	public static final String DOUBLEVETICALBAR = " || ";
	public static final String VETICALBAR = "|";
	public static final String PAREN_LEFT = "(";
	public static final String PAREN_RIGHT = ")";
	
	public static final String NULLSTR = "null";
	
	public static final String ACRONYM_PATTERN_YIHA = "(이하 ";
	
	public static final int SHORT_WORD_LEGNTH = 4;
	public static final int FULL_WORD_MIN_FRQ = 5;
	public static final int SHORT_WORD_MIN_FRQ = 1;
	
	public static final String[] ARR_ENTER_CAT = new String[] {"ET","DRAMA","MOVIE","MUSIC"}; 
	public static final Set<String> SET_ENTER_CAT = new HashSet<String>(Arrays.asList(ARR_ENTER_CAT));

    public static final String PRV_DUP_ENTRY_TAG = "NE_PRVDUP";
    public static final String TST_DUP_ENTRY_TAG = "NE_TSTDUP";
    public static final String NEW_NE_ENTRY_TAG = "NE_NEW";

    public static final String CAT_DRAMA = "drama";
    public static final String CAT_MOVIE = "movie";
    public static final String CAT_ENTER = "et";
    public static final String CAT_GAME = "game";
    public static final String CAT_IT = "it";
    public static final String CAT_BOOK = "book";
    public static final String CAT_MUSIC = "music";
    public static final String CAT_PRODUCT = "product";
    public static final String CAT_TRAVEL = "travel";
    public static final String CAT_FOOD = "food";
    public static final String CAT_ISSUE = "issue";
    public static final String CAT_SOCIETY = "society";

}
