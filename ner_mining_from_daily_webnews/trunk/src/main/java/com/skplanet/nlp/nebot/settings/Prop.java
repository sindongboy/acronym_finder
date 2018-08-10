package com.skplanet.nlp.nebot.settings;

import javax.print.DocFlavor;

/**
 * Static Resource definition
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 1/14/15
 */
public final class Prop {

    // ------------- //
    // properties
    // ------------- //
    public static final String NEBOT_PROP = "nebot.properties";
    public static final String SEGMENT_PROP = "segment.properties";

    // ------------- //
    // fields
    // ------------- //
    // --> nebot properties <--
    public static final String SENT_SEGMENT = "SENT_SEGMENT";
    public static final String TERM_MAX_LENGTH = "TERM_MAX_LENGTH";
    public static final String TIME_OUT_SECONDS = "TIME_OUT_SECONDS";
    public static final String PRINT_PRV_DUP = "PRINT_PRV_DUP";
    public static final String PRINT_TSTORE_DUP = "PRINT_TSTORE_DUP";
    public static final String PRINT_SKCOMZ_DUP = "PRINT_SKCOMZ_DUP";
    public static final String PRINT_SYSDIC_DUP = "PRINT_SYSDIC_DUP";
    public static final String PRINT_ASCII_ONLY_WORD = "PRINT_ASCII_ONLY_WORD";
    public static final String SINGLE_QUOTATION = "SINGLE_QUOTATION";
    public static final String PARENTHESES = "PARENTHESES";
    public static final String ANGLE_BRACKET = "ANGLE_BRACKET";
    public static final String SQUARE_BRACKET = "SQUARE_BRACKET";
    // --> segmenter properties <--
    public static final String NGRAM_SIZE = "NGRAM_SIZE";
    public static final String EOS_FRQ_THRESHOLD = "EOS_FRQ_THRESHOLD";
    public static final String NBS_FRQ_THRESHOLD = "NBS_FRQ_THRESHOLD";
    public static final String EOS_THRESHOLD = "EOS_THRESHOLD";
    public static final String NBS_THRESHOLD = "NBS_THRESHOLD";

    // ------------- //
    // segmenter
    // ------------- //
    public static final String SEGMENT_MODEL_EOS = "ngramProb.eos";
    public static final String SEGMENT_MODEL_NBS = "ngramProb.nbs";

    // ------------- //
    // nlp
    // ------------- //
    public static final String NLP_SYSTEM_DICT = "korsysdic.txt";

    // ------------- //
    // logs
    // ------------- //
    public static final String NE_HISTORY_BLOG = "history_ne_blog.txt";
    public static final String NE_HISTORY_NEWS = "history_ne_news.txt";

    // ------------- //
    // stopwords
    // ------------- //
    public static final String STOPWORD_GEN = "stopword-nebot.txt";
    public static final String STOPWORD_GEN_SUB = "sub-stopword-nebot.txt";
    public static final String STOPWORD_SKCOMZ = "skcomz.txt";
    public static final String STOPWORD_TSTORE = "tstore.txt";

    // ------------- //
    // seeds
    // ------------- //
    public static final String SEED = "seed.json";

    // ------------- //
    // logs
    // TODO: find usage!
    // ------------- //
    public static final String URL_DOWNLOAD = "url_down.txt";
    //public static final String URL_DUPLICATE = "url_duplicated_log.txt";
    public static final String NE_FILE_OUT_NEWS_LOG = "NE_FILE_OUT_NEWS_LOG";
    public static final String NE_FILE_OUT_BLOG_LOG = "NE_FILE_OUT_BLOG_LOG";

    // ------------- //
    // output
    // ------------- //
    public static final String NE_FILE_OUT_NEWS = "NE_FILE_OUT_NEWS";
    public static final String NE_FILE_OUT_BLOG = "NE_FILE_OUT_BLOG";

}
