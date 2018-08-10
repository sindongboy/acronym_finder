package com.skplanet.nlp.acronym;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.resource.Resource;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class NeAcronym {
    public static final Logger LOGGER = Logger.getLogger(NeAcronym.class.getName());
    
    //public Properties properties;
    public String filePath;
    public String fileName;
    //public String filePathOutGen;
    //public String filePathOutGenPure;
    //public String filePathOutAcro;
    //public String filePathOutLog;
    public Map<String, NeEntry> neMap;
    public Map<String, NeEntry> neMapAcro;
    public Map<String, NeEntry> neMapGen;
    public Map<String, NeEntry> neMapLog;
    public Vector<NeEntry> shortTokenNe;
    public Vector<NeEntry> largeTokenNe;

    public NeAcronym(String filePath) {

        //replace 'Properties' with 'omp-config'
        Configuration config = Configuration.getInstance();
        try {
            config.loadProperties(Prop.NE_ACRONYM_PROP_NAME);
        } catch (IOException e) {
            LOGGER.error("failed to load properties: " + Prop.NE_ACRONYM_PROP_NAME);
        }


        //this.properties = properties;
        this.filePath = filePath;
        this.fileName = getFilename(filePath);
        //this.filePathOutGen = this.properties.FILE_OUTPATH_NE + "/" + this.fileName + ".gen";
        //this.filePathOutGenPure = this.properties.FILE_OUTPATH_NE + "/" + this.fileName + ".pure";
        //this.filePathOutAcro = this.properties.FILE_OUTPATH_ACRO + "/" + this.fileName + ".acro";
        //this.filePathOutLog = this.properties.FILE_OUTPATH_LOG + "/" + this.fileName + ".log";
        this.neMap = new HashMap<String, NeEntry>();
        this.neMapAcro = new HashMap<String, NeEntry>();
        this.neMapGen = new HashMap<String, NeEntry>();
        this.neMapLog = new HashMap<String, NeEntry>();
        this.shortTokenNe = new Vector<NeEntry>();
        this.largeTokenNe = new Vector<NeEntry>();
    }

    /**
     * replace file name with normalized file name
     * @param filePath raw file path
     * @return normalized file path/name
     */
    private String getFilename(String filePath) {
        // TODO: remove or replace
        String fileName;
        if (filePath.contains("/")) {
            // get file name from file path
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        }else{
            // just attach date format to the file name
            DateFormat  sdFormat = new SimpleDateFormat("yyyyMMdd");
            Date nowDate = new Date();
            String strDate = sdFormat.format(nowDate);
            fileName = "NE_DEFAULT_" + strDate;
        }
        return fileName;
    }

    public void findAcronym() {
        Configuration config = Configuration.getInstance();
        try {
            config.loadProperties(Prop.NE_ACRONYM_PROP_NAME);
        } catch (IOException e) {
            LOGGER.error("failed to load properties: " + Prop.NE_ACRONYM_PROP_NAME);
        }

        // TODO: check 'load() logic'
        load();
        if ("true".equals(config.readProperty(Prop.NE_ACRONYM_PROP_NAME, Prop.FIND_ACRONYM))) {
            divide_ne_by_tokenlength();
            mappingAcronymByStringMatching();
            merge_byCommonAcronym();
            saveNeAcro();
            saveNeGen();
            saveNeLog();
        } else {
            saveNeGenPure();
        }
    }

    private void merge_byCommonAcronym() {
        Set<String> keys = this.neMap.keySet();
        for (String key : keys) {
            NeEntry ne = this.neMap.get(key);
            String pairA = ne.pairA;
            String acroComm = ne.acroCommonKey;
            if (!ne.isAcronym && ne.hasAcronym) {
                if (this.neMapAcro.containsKey(acroComm)) {
                    NeEntry neComm = this.neMapAcro.get(acroComm);
                    neComm.frq = neComm.frq + ne.frq - this.neMap.get(pairA).frq;

                    for (String paira : ne.pairAMulti) {
                        neComm.pairAMulti.add(paira);
                    }

                    if (ne.acronyms.size() > 0) {
                        for (String acro : ne.acronyms) {
                            neComm.acronyms.add(acro);
                        }
                    }
                    neComm.sentence.append(NeAcronymCodes.DOUBLEVETICALBAR);
                    neComm.sentence.append(ne.sentence.toString());

                } else {
                    this.neMapAcro.put(acroComm, ne);
                }
            } else {
                if (ne.isAcronym) {
                    this.neMapLog.put(acroComm, ne);
                } else {
                    this.neMapGen.put(acroComm, ne);
                }
            }
        }
    }

    private void saveNeLog(String inputFile) {
        //
        // TODO : This is NOT just LOG, should set this to be some kind of raw family
        //
        BufferedWriter bwLog;

        try {
            bwLog = new BufferedWriter(new FileWriter(inputFile, false));

            Set<String> keys = this.neMapLog.keySet();
            for (String key : keys) {
                NeEntry ne = this.neMapLog.get(key);
                StringBuilder sb = getNe(ne);
                bwLog.write(sb.toString());
                bwLog.write("\n");
            }
            bwLog.close();
        } catch (IOException e) {

        }
    }
    private boolean isPrintableCategory(String category){
        if(category.equals(NeAcronymCodes.CAT_DRAMA)){
            return this.properties.PRINT_DRAMA_NE;
        }else if(category.equals(NeAcronymCodes.CAT_MOVIE)){
            return this.properties.PRINT_MOVIE_NE;
        }else if(category.equals(NeAcronymCodes.CAT_ENTER)){
            return this.properties.PRINT_ENTER_NE;
        }else if(category.equals(NeAcronymCodes.CAT_GAME)){
            return this.properties.PRINT_GAME_NE;
        }else if(category.equals(NeAcronymCodes.CAT_ISSUE)){
            return this.properties.PRINT_ISSUE_NE;
        }else if(category.equals(NeAcronymCodes.CAT_PRODUCT)){
            return this.properties.PRINT_PRODUCT_NE;
        }else if(category.equals(NeAcronymCodes.CAT_TRAVEL)){
            return this.properties.PRINT_TRAVEL_NE;
        }else if(category.equals(NeAcronymCodes.CAT_FOOD)){
            return this.properties.PRINT_FOOD_NE;
        }else if(category.equals(NeAcronymCodes.CAT_SOCIETY)){
            return this.properties.PRINT_SOCIETY_NE;
        }else if (category.equals(NeAcronymCodes.CAT_IT)) {
            return this.properties.PRINT_IT_NE;
        }else if(category.equals(NeAcronymCodes.CAT_BOOK)){
            return this.properties.PRINT_BOOK_NE;
        }else if(category.equals(NeAcronymCodes.CAT_MUSIC)){
            return this.properties.PRINT_MUSIC_NE;
        } else {
            return false;
        }
    }

    private void saveNeGen(String inputFile) {
        BufferedWriter bwGen;
        try {
            bwGen = new BufferedWriter(new FileWriter(inputFile, false));

            Set<String> keys = this.neMapGen.keySet();
            for (String key : keys) {
                NeEntry ne = this.neMapGen.get(key);
                if(ne.frq >= this.properties.NE_FRQ_MIN_LIMIT && is_printable_category(ne.bigCategory)){
                    StringBuilder sb = getNe(ne);
                    bwGen.write(sb.toString());
                    bwGen.write("\n");
                }
            }
            bwGen.close();
        } catch (IOException e) {
            LOGGER.error(inputFile + " not found", e);
        }
    }

    private void saveNeGenPure(String inputFile) {
        BufferedWriter bwGen;

        try {
            bwGen = new BufferedWriter(new FileWriter(inputFile, false));

            Set<String> keys = this.neMap.keySet();
            for (String key : keys) {
                NeEntry ne = this.neMap.get(key);
                if(ne.frq >= this.properties.NE_FRQ_MIN_LIMIT && is_printable_category(ne.bigCategory)){
                    StringBuilder sb = getNe(ne);
                    bwGen.write(sb.toString());
                    bwGen.write("\n");
                }
            }
            bwGen.close();
        } catch (IOException e) {
            LOGGER.error(inputFile + " not found", e);
        }
    }

    private void saveNeAcro(String inputFile) {
        BufferedWriter bwAcro;

        try {
            bwAcro = new BufferedWriter(new FileWriter(inputFile, false));

            Set<String> keys = this.neMapAcro.keySet();
            for (String key : keys) {
                NeEntry ne = this.neMapAcro.get(key);
                StringBuilder sb = getNe(ne);

                if (!ne.isAcronym) {

                    if (ne.hasAcronym) {
                        bwAcro.write(sb.toString());
                        bwAcro.write("\n");
                    }
                }
            }
            bwAcro.close();
        } catch (IOException e) {
            LOGGER.error(inputFile + " not found", e);
        }
    }

    private StringBuilder getNe(NeEntry ne) {
        StringBuilder sb = new StringBuilder();
        if (ne.isAcronym) {
            sb.append("ACRONYM");
        } else {
            sb.append("GENERAL");
        }
        sb.append(NeAcronymCodes.TAB);


        if (ne.hasAcronym) {
            sb.append("HAS_ACRONYM");
        } else {
            sb.append("NO_ACRONYM");
        }
        sb.append(NeAcronymCodes.TAB);


        sb.append(ne.frq);
        sb.append(NeAcronymCodes.TAB);

        if (ne.hasAcronym) {
            sb.append(ne.acroCommonKey);
        } else {
            sb.append(NeAcronymCodes.NULLSTR);
        }
        sb.append(NeAcronymCodes.TAB);


        sb.append(ne.filterType);
        sb.append(NeAcronymCodes.TAB);

        sb.append(ne.site);
        sb.append(NeAcronymCodes.TAB);

        sb.append(ne.bigCategory);
        sb.append(NeAcronymCodes.TAB);

        sb.append(ne.midCategory);
        sb.append(NeAcronymCodes.TAB);

        sb.append(ne.channel);
        sb.append(NeAcronymCodes.TAB);

        sb.append(ne.crwalDate);
        sb.append(NeAcronymCodes.TAB);

        for (String paira : ne.pairAMulti) {
            sb.append(paira);
            sb.append(NeAcronymCodes.DOUBLEVETICALBAR);
        }
        sb.append(NeAcronymCodes.TAB);


        if (ne.hasAcronym) {
            for (String acro : ne.acronyms) {
                sb.append(acro);
                sb.append(NeAcronymCodes.VETICALBAR);
            }

        } else {
            sb.append(NeAcronymCodes.NULLSTR);
        }
        sb.append(NeAcronymCodes.TAB);

        sb.append(ne.sentence.toString());
        sb.append(NeAcronymCodes.TAB);

        sb.append(ne.url);
        sb.append(NeAcronymCodes.TAB);

        sb.append(ne.writeDate);

        return sb;
    }

    private String replaceUnderbarWithSpace(String str) {
        while (str.contains(NeAcronymCodes.UNDERBAR)) {
            str = str.replace(NeAcronymCodes.UNDERBAR, NeAcronymCodes.SPACE);
        }
        return str;
    }

    private String removeSpace(String str) {
        while (str.contains(NeAcronymCodes.SPACE)) {
            str = str.replace(NeAcronymCodes.SPACE, "");
        }
        while (str.contains(NeAcronymCodes.UNDERBAR)) {
            str = str.replace(NeAcronymCodes.UNDERBAR, "");
        }
        return str;
    }

    private String trimStr(String str){
        while(str.contains("'")) {
            str = str.replace("'", "");
        }
        while(str.contains(" ")) {
            str = str.replace(" ", "");
        }
        while(str.length() > 0 && str.indexOf(NeAcronymCodes.SPACE)==0){
            str = str.substring(1);
        }
        while(str.length() > 0 && str.lastIndexOf(NeAcronymCodes.SPACE)==str.length()-1){
            str = str.substring(0,str.lastIndexOf(NeAcronymCodes.SPACE));
        }
        return str;
    }

    private boolean isAcronym(String keyShort, String keyLarge) {
        char[] shortArr = keyShort.toCharArray();
        int prvIndex = -1;
        int curIndex = -1;
        int index = -1;
        for (char ch : shortArr) {
            index++;
            if ((curIndex = keyLarge.indexOf(ch, prvIndex + 1)) == -1) {
                return false;
            } else {

                if (curIndex <= prvIndex) {
                    return false;
                }
                if (index == 0) {
                    if (!(curIndex == 0) || (curIndex > 0 && keyLarge.charAt(curIndex - 1) == ' ')) {
                        return false;
                    }
                }
                prvIndex = curIndex;

            }
        }
        return true;
    }

    private boolean isAsciiOnlyWord(String str) {
        char chArr[] = str.toCharArray();
        for (char ch : chArr) {
            if (ch > 0x7f) {
                return false;
            }
        }
        return true;
    }

    private boolean checkWordFrq(NeEntry neShort, NeEntry neLarge) {
        return (neShort.frq >= NeAcronymCodes.SHORT_WORD_MIN_FRQ && neLarge.frq >= NeAcronymCodes.FULL_WORD_MIN_FRQ);

    }

    private void tokenSubstringMatching(String keyShort, String keyLarge, NeEntry neshort, NeEntry nelarge) {
        if (checkWordFrq(neshort, nelarge) && !isAsciiOnlyWord(keyShort) && keyLarge.contains(keyShort) && isSameCategory(neshort.bigCategory, nelarge.bigCategory)) {
            neshort.isAcronym = true;
            nelarge.frq = nelarge.frq + neshort.frq;

            String shortPairA = replaceUnderbarWithSpace(neshort.pairA);
            nelarge.hasAcronym = true;
            nelarge.acronyms.add(shortPairA);
            nelarge.acroCommonKey = shortPairA;

            nelarge.sentence.append(NeAcronymCodes.DOUBLEVETICALBAR);
            nelarge.sentence.append(neshort.sentence.toString());

        }
    }

    private void charSerialMatching(String keyShort, String keyLarge, NeEntry neshort, NeEntry nelarge) {
        if (checkWordFrq(neshort, nelarge) &&
                !isAsciiOnlyWord(keyShort) &&
                !keyLarge.contains(keyShort) &&
                isSameCategory(neshort.bigCategory, nelarge.bigCategory) &&
                isAcronym(keyShort, keyLarge)) {
            
            LOGGER.debug("char serial matching short :" + keyShort + " large : " + keyLarge);

            neshort.isAcronym = true;
            nelarge.frq = nelarge.frq + neshort.frq;
            nelarge.hasAcronym = true;
            nelarge.acronyms.add(neshort.pairA);
            nelarge.acroCommonKey = neshort.pairA;

            nelarge.sentence.append(NeAcronymCodes.DOUBLEVETICALBAR);
            nelarge.sentence.append(neshort.sentence);
        }
    }

    private void mappingAcronymByStringMatching() {
        for (NeEntry neshort : this.shortTokenNe) {
            String keyShort = neshort.pairARefine;
            keyShort = trimStr(keyShort);

            for (NeEntry nelarge : this.largeTokenNe) {
                String keyLarge = nelarge.pairARefine;
                String keyLargeNoSpace = removeSpace(keyLarge);

                if (this.properties.TOKEN_SUBSTRING_MATCHING) {
                    tokenSubstringMatching(keyShort, keyLarge, neshort, nelarge);
                }

                if (this.properties.CHAR_SERIAL_MATCHING) {
                    charSerialMatching(keyShort, keyLargeNoSpace, neshort, nelarge);
                }
            }
        }
    }

    private boolean isSameCategory(String cat1, String cat2) {
        if (cat1.equals(cat2)) {
            return true;
        } else if (NeAcronymCodes.SET_ENTER_CAT.contains(cat1) && NeAcronymCodes.SET_ENTER_CAT.contains(cat2)) {
            return true;
        } else {
            return false;
        }
    }

    private void divide_ne_by_tokenlength() {
        Set<String> keys = this.neMap.keySet();
        String delims = "[" + NeAcronymCodes.SPACE + "]+";
        for (String key : keys) {
            NeEntry ne = this.neMap.get(key);
            if (ne.pairARefine.split(delims).length <= 2 && ne.pairARefine.length() <= NeAcronymCodes.SHORT_WORD_LEGNTH) {
                this.shortTokenNe.add(ne);
            } else {
                this.largeTokenNe.add(ne);
            }
        }

    }

    private void load() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.filePath), "UTF-8"));

            String line;
            String delims = "[" + NeAcronymCodes.TAB + "]";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] tokens = line.split(delims);
                set_nemap(tokens);
            }

            reader.close();

        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {

        } catch (IOException e) {

        }
    }

    private String extract_acronym_bypattern(String pairB) {
        String acronym = "";
        if (pairB.contains(NeAcronymCodes.ACRONYM_PATTERN_YIHA)) {
            acronym = pairB.substring(pairB.indexOf(NeAcronymCodes.ACRONYM_PATTERN_YIHA) + NeAcronymCodes.ACRONYM_PATTERN_YIHA.length());
            if (acronym.contains(NeAcronymCodes.PAREN_RIGHT)) {
                acronym = acronym.substring(0, acronym.indexOf(NeAcronymCodes.PAREN_RIGHT));
                if (acronym.contains(NeAcronymCodes.COMMA)) {
                    acronym = acronym.substring(0, acronym.indexOf(NeAcronymCodes.COMMA));
                }
            }
        }

        return acronym;
    }

    private String refine_pairA(String pairA) {
        while (pairA.contains(NeAcronymCodes.UNDERBAR)) {
            pairA = pairA.replace(NeAcronymCodes.UNDERBAR, NeAcronymCodes.SPACE);
        }
        while (pairA.contains(NeAcronymCodes.DASH)) {
            pairA = pairA.replace(NeAcronymCodes.DASH, NeAcronymCodes.SPACE);
        }
        while (pairA.contains(NeAcronymCodes.COMMA)) {
            pairA = pairA.replace(NeAcronymCodes.COMMA, NeAcronymCodes.SPACE);
        }
        while (pairA.contains(NeAcronymCodes.COLON)) {
            pairA = pairA.replace(NeAcronymCodes.COLON, NeAcronymCodes.SPACE);
        }
        while (pairA.contains(NeAcronymCodes.AMPERSAND)) {
            pairA = pairA.replace(NeAcronymCodes.AMPERSAND, NeAcronymCodes.SPACE);
        }
        return pairA;
    }
    private boolean is_printableType(String type) {
        if (type.equals(NeAcronymCodes.PRV_DUP_ENTRY_TAG)) {
            return this.properties.USE_PRVDUP_ENTRY;
        }else if (type.equals(NeAcronymCodes.TST_DUP_ENTRY_TAG)) {
            return this.properties.USE_TSTDUP_ENTRY;
        }else if (type.equals(NeAcronymCodes.NEW_NE_ENTRY_TAG)) {
            return this.properties.USE_NEW_ENTRY;
        } else {
            return  true;
        }
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

    private void set_nemap(String[] tokens) {
        String filterType = "";
        String site = "";
        String bigCategory = "";
        String midCategory = "";
        String channel = "";
        String crawlDate = "";
        String patternType = "";
        String pairA = "";
        String pairARefine = "";
        String pairARefineNoSpace = "";
        String pairB = "";
        String sentence = "";
        String url = "";
        String writeDate = "";
        boolean filterFlag = true;
        boolean stopwordsFlag = false;
        for (int i = 0; i < tokens.length && filterFlag; i++) {
            if (i == 0) {
                filterType = tokens[i];
                filterFlag = is_printableType(filterType);
            } else if (i == 1) {
                site = tokens[i];
            } else if (i == 2) {
                bigCategory = tokens[i];
            } else if (i == 3) {
                midCategory = tokens[i];
            } else if (i == 4) {
                channel = tokens[i];
            } else if (i == 5) {
                crawlDate = tokens[i];
            } else if (i == 6) {
                patternType = tokens[i];
            } else if (i == 7) {
                pairA = tokens[i].trim();
                pairA = trimStr(pairA);
                stopwordsFlag = is_stopwords(pairA);
                pairARefine = refine_pairA(pairA);
                pairARefineNoSpace = removeSpace(pairARefine);
            } else if (i == 8) {
                pairB = tokens[i];
            } else if (i == 9) {
                sentence = tokens[i];
            } else if (i == 10) {
                url = tokens[i];
            } else if (i == 11) {
                writeDate = tokens[i];
            } else {
            }
        }
        String acronym = "";
        if (pairA.length() <= this.properties.LENGTH_MAX_LIMIT && filterFlag && !stopwordsFlag) {
            if (this.neMap.containsKey(pairA)) {
                NeEntry ne = this.neMap.get(pairA);
                ne.frq = ne.frq + 1;
                acronym = extract_acronym_bypattern(pairB);
                acronym = trimStr(acronym);
                if (acronym.length() > 0) {
                    if (this.properties.TOKEN_SUBSTRING_MATCHING || !pairARefineNoSpace.contains(acronym)) {
                        
                        LOGGER.debug("pattern_YIHA matching short :" + acronym + " large : " + pairARefineNoSpace);

                        ne.hasAcronym = true;
                        ne.acronyms.add(acronym);
                        ne.acroCommonKey = acronym;
                    }
                }
                ne.pairAMulti.add(pairA);
            } else {
                NeEntry ne = new NeEntry();
                ne.frq = 1;
                ne.filterType = filterType;
                ne.site = site;
                ne.bigCategory = bigCategory;
                ne.midCategory = midCategory;
                ne.channel = channel;
                ne.crwalDate = crawlDate;
                ne.patternType = patternType;
                ne.pairA = pairA;
                ne.pairAMulti.add(pairA);
                ne.pairARefine = pairARefine;
                ne.pairB = pairB;

                acronym = extract_acronym_bypattern(pairB);
                acronym = trimStr(acronym);
                if (acronym.length() > 0) {
                    if(this.properties.TOKEN_SUBSTRING_MATCHING || !pairARefineNoSpace.contains(acronym)){
                        LOGGER.debug("pattern_YIHA matching short :" + acronym + " large : " + pairARefineNoSpace);
                        ne.hasAcronym = true;
                        ne.acroCommonKey = acronym;
                        ne.acronyms.add(acronym);
                    }
                } else {
                    ne.acroCommonKey = replaceUnderbarWithSpace(pairA);
                }
                ne.sentence.append(sentence);
                ne.url = url;
                ne.writeDate = writeDate;
                this.neMap.put(pairA, ne);
            }
        }
    }
}