package com.skplanet.nlp.acronym;

import org.apache.log4j.Logger;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 1/27/15
 */
public final class Prop {
    //private static Logger LOGGER = Logger.getLogger(Prop.class.getName());

    public static final String NE_ACRONYM_PROP_NAME = "neacronym.properties";

    //stopwords
    public static final String STOPWORD = "stopword-acronym.txt";
    public static final String STOPWORD_SUB = "sub-stopword-acronym.txt";

    //print options
    public static final String PRINT_DRAMA_NE = "PRINT_DRAMA_NE";
    public static final String PRINT_MOVIE_NE = "PRINT_MOVIE_NE";
    public static final String PRINT_ENTER_NE = "PRINT_ENTER_NE";
    public static final String PRINT_GAME_NE = "PRINT_GAME_NE";
    public static final String PRINT_MUSIC_NE = "PRINT_MUSIC_NE";
    public static final String PRINT_ISSUE_NE = "PRINT_ISSUE_NE";
    public static final String PRINT_IT_NE = "PRINT_IT_NE";
    public static final String PRINT_PRODUCT_NE = "PRINT_PRODUCT_NE";
    public static final String PRINT_TRAVEL_NE = "PRINT_TRAVEL_NE";
    public static final String PRINT_FOOD_NE = "PRINT_FOOD_NE";
    public static final String PRINT_BOOK_NE = "PRINT_BOOK_NE";
    public static final String PRINT_SOCIETY_NE = "PRINT_SOCIETY_NE";
    public static final String FIND_ACRONYM = "FIND_ACRONYM";
    public static final String TOKEN_SUBSTRING_MATCHING = "TOKEN_SUBSTRING_MATCHING";
    public static final String CHAR_SERIAL_MATCHING = "CHAR_SERIAL_MATCHING";
    public static final String USE_PRVDUP_ENTRY = "USE_PRVDUP_ENTRY";
    public static final String USE_TSTDUP_ENTRY = "USE_TSTDUP_ENTRY";
    public static final String USE_NEW_ENTRY = "USE_NEW_ENTRY";
    
    private Prop() {
        
    }
}
