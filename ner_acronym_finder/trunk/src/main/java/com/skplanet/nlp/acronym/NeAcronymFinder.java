package com.skplanet.nlp.acronym;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.resource.Resource;

public class NeAcronymFinder{
	public static void main(String[] args) throws Exception {
		//useage(args);

		CommandLineInterface cli = new CommandLineInterface();
		cli.addOption("i", "input", true, "input named entity file", true);
		cli.parseOptions(args);

		/*
		String configFilePath = args[0];
		String neFilePath = args[1];
		*/

		//Properties properties = new Properties(configFilePath);
		//Resource.setResources(properties);
		Resource.setResources();

		//NeAcronym neacro = new NeAcronym(properties, neFilePath);
		NeAcronym neacro = new NeAcronym(cli.getOption("i"));
		neacro.find_acronym();
	}
	
	/*
	private static void useage(String[] args){
		if(args.length < 2){
			System.out.println("<USAGE> java MAIN_CLASS <CONFIG_FILE_PATH> <INPUT_FILE_PATH>");
			System.out.println("<CONFIG_FILE_PATH> : config file path");
			System.out.println("<INPUT_FILE_PATH> : input_file_path");
			return;
		}

	}
	*/
}
