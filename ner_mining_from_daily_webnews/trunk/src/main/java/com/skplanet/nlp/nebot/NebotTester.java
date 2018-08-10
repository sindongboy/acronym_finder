package com.skplanet.nlp.nebot;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.nebot.resource.Resource;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * 'harvest_ne.sh' 에서 사용되는 메인 드라이버
 *
 * 개체명을 웹페이지에서 수집하는 목적.
 *
 * @author 윤창호M
 * @document Donghun Shin / donghun.shin@sk.com
 * @modified Donghun Shin / donghun.shin@sk.com
 */
public class NebotTester {
	private static final Logger LOGGER = Logger.getLogger(NebotTester.class.getName());

	private static final int ARG_LEN = 2;

	public static void main(String[] args) throws Exception {

		CommandLineInterface cli = new CommandLineInterface();
		cli.addOption("t", "type", true, "run type [ url, text, batch ]", true);
		cli.parseOptions(args);

		usage(args);

		String runtypeFlag = cli.getOption("t");

		Resource.setResources();

		if ("url".equals(runtypeFlag)) {
			runConsoleUrl();
		} else if ("text".equals(runtypeFlag)) {
			runConsoleText();
		} else if ("batch".equals(runtypeFlag)) {
			runBatch();
		} else {
			usage(args);
		}
	}

	/**
	 * Console Mode (URL)
	 * @throws Exception
	 */
	public static void runConsoleUrl() throws Exception{
		System.out.print("\nInput URL : ");
		
		Nebot nebot = new Nebot();
		
		String strUrl;
		BufferedReader inStd=new BufferedReader(new InputStreamReader(System.in));
		while((strUrl=inStd.readLine()) != null){
			nebot.harvestFromUrl(strUrl);
			System.out.print("\nInput URL : ");
		}
		
	}

	/**
	 * Console Mode (TEXT)
	 *
	 */
	public static void runConsoleText() {
		System.out.print("\nInput Text : ");
		Nebot nebot = new Nebot();

		String strText;
		BufferedReader inStd = new BufferedReader(new InputStreamReader(System.in));
		try {
			while ((strText = inStd.readLine()) != null) {
                nebot.harvestFromText(strText);
                System.out.print("\nInput Text : ");
            }
		} catch (IOException e) {
			LOGGER.error("failed to read input", e);
		}

	}

	/**
	 * Batch Mode
	 *
	 */
	public static void runBatch() {
		Nebot nebot = new Nebot();
		nebot.harvest();
	}

	/**
	 * Usage
	 * @param args argument list
	 */
	private static void usage(String[] args){
		if(args.length < ARG_LEN){
			System.out.println("<USAGE> java MAIN_CLASS <CONFIG_FILE_PATH> <RUN_TYPE>");
			System.out.println("<CONFIG_FILE_PATH> : config file path");
			System.out.println("<RUN_TYPE> : -s standard mode and output");
			System.out.println("                    : -b batch mode and file output");
		}

	}

	private NebotTester() {
		// dummy constructor
	}
}
