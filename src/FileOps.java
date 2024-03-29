import java.util.logging.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.lang.NumberFormatException;


/**
  *A class for performing i/o with files
  *Finds fusion combination and demon data from database files
  *Also outputs data to files
  */

public class FileOps {

  //Delimiters for parsing data files
  private static final String COMMA_DELIMITER = ",";
  private static final String COLON_DELIMITER = ":";
  private static final String RACE_DEMON_SPLITTER = "\\|";
  private static final String NEWLINE_DELIMITER = "\n";
  //Logger
  private static final Logger logger = Logger.getLogger("FileOps");

  static {
    //Set up logger
    try {
      logger.setLevel(Level.ALL);
      //Create log directory if it doesn't already exist
      File logDir = new File("Logs");
      logDir.mkdir();
      Handler handler = new FileHandler("Logs/FileOps.log");
      logger.addHandler(handler);
    }
    catch (IOException e) {
      logger.log(Level.SEVERE, "Couldn't create log file handler for FileOps class", e);
      Runner.reportError("Couldn't create log file for FileOps class");
      System.exit(1);
    }
  }

  /**
    * Opens and reads files to populate data for given race
    * Populates race enum's lists and data structures
    *@param race the string of the race being queries
    *@param fCombination a list to be populated, which should contain 2 strings, each corresponding to one of the component race names
    *@param eUp a list of strings to be populated, containing the names of elements that result in up-fusing for this race
    *@param eDown a list of strings to be popluated, containing the names of elements that result in down-fusion for this race
    *@param demons a list of strings to be popluated, containing the names of all demons belonging to this race
    */
  public static void populateRaceData(String race, List<String[]> fCombination, List<String> eUp, List<String> eDown, List<Demon> demons, List<Demon> compendium) {
    //Log entry
    logger.entering("FileOps","populateRaceData");

    readattempt: try {
      //First, read in all demons
      readDemonList(race, demons);
      //Next, read in demon compendium
      readDemonCompendium(race, compendium);
      //Next, read in fusion combinations
      readFusionCombinations(race, fCombination);
      //Finally, read in element rules
      readElementRules(race, eUp, eDown);
    }
    catch(IOException e) {
      logger.log(Level.INFO, "An IOException occurred while reading files", e);
      Runner.reportError("Error while reading files. IOException");
      System.exit(1);
    }
    catch(NullPointerException e) {
      logger.log(Level.FINE, "Null pointer exception. Race name was likely null", e);
      Runner.reportError("Error while reading files. NullPointerException");
      System.exit(1);
    }
    catch (NumberFormatException e) {
      logger.log(Level.INFO, "Couldn't parse demon level as integer value", e);
      Runner.reportError("Incorrect formatting in one of the files. NumberFormatException");
      System.exit(1);
    }

    //Log exit
    logger.exiting("FileOps","populateRaceData");
  }

  /**
    * For a given element, populates a list of races that can be used to produce the element
    * @param element the specified element for which fusion rules are desired
    * @param races the list of races to populate
    */
  public static void populateElementFusionRules(String element, List<Race> races) {
    //Log entry
    logger.entering("FileOps","populateElementFusionRules");

    try {
      //Ensure neither element nor races are null
      if(element == null || races == null) {
        throw new NullPointerException("Element or races null for populateElementFusionRules");
      }

      //Open file containing all fusion rules for each element
      File f = new File("Rules/ElementFusions");
      if(!f.exists()) {
        System.out.println("Error. Couldn't find element fusion rules in expected location");
        Runner.reportError("Couldn't find ElementFusions file in expected location");
        System.exit(1);
      }

      //Contstruct reader and read first line (header)
      BufferedReader br = new BufferedReader(new FileReader(f));
      br.readLine();
      //Read all lines in file until the desired element is found
      String line = br.readLine();
      boolean elementRead = false;
      while(line != null && !elementRead) {
        //Tokenize line
        String tokens[] = line.split(COMMA_DELIMITER);
        //Check if the current line is for the desired element
        String curElement = tokens[0];
        if(curElement.equalsIgnoreCase(element)) {
          elementRead = true;
          //Store all races that can produce desired element
          String racesForElement[] = tokens[1].split(COLON_DELIMITER);
          for(String curRace : racesForElement) {
            Race race = Race.fromString(curRace.toLowerCase());
            races.add(race);
          }
        }
        //Read next line
        line = br.readLine();
      }
    }
    catch (IOException e) {
      logger.log(Level.INFO, "IOException occured while reading element fusion rules", e);
      Runner.reportError("Error while reading ElementFusions file. IOException");
      System.exit(1);
    }
    catch (NullPointerException e) {
      logger.log(Level.INFO, "Null pointer exception in populateElementFusionRules", e);
      Runner.reportError("Error while reading ElementFusions file. NullPointerException");
      System.exit(1);
    }
    logger.exiting("FileOps","populateElementFusionRules");
  }

  /**
    * Finds all demons belonging to a specific race, and adds them to list passed as parameter
    * @param race the race for which we want to find all demons
    * @param demons the list to which all located demons are added
    * @throws NullPointerException if either parameter passed is null
    * @throws FileNotFoundException if file containing race's demon list is not found
    * @throws IOException if an IO error occurs while reading file
    */
  private static void readDemonList(String race, List<Demon> demons)
  throws NullPointerException, FileNotFoundException, IOException, NumberFormatException {
    //Log entry
    logger.entering("FileOps", "readDemonList");

    //Start by opening file containing all of race's demons
    File f = new File("Demon Database/" + race);
    //Ensure file exists
    if(!f.exists()) {
      logger.warning("Error. The file " + race + " does not exist in expected directory. Exiting program...");
      Runner.reportError("Couldn't find " + race +"\'s demon list in expected location. Exiting");
      System.exit(1);
    }

    //Create buffered reader for file
    BufferedReader br = new BufferedReader(new FileReader(f));

    //Read headers
    br.readLine();
    br.readLine();

    //Now, read all lines in file
    String line = br.readLine();
    while(line != null) {
      //For current line, tokenize by commas
      String tokens[] = line.split(COMMA_DELIMITER);
      //First token is demon's name
      String curDemonName = tokens[0];
      //Second token is demon's level
      int curDemonLevel = Integer.valueOf(tokens[1]);
      //Third token has demon's innate and learned skill set
      Set<String> skills = new HashSet<String>();
      //Tokenize third token by COLON_DELIMITER
      String skillListStrings[] = tokens[2].split(COLON_DELIMITER);
      for(int i=0; i<skillListStrings.length; i++) {
        skills.add(skillListStrings[i]);
      }
      //Create and add new demon to list
      demons.add(new Demon(race, curDemonName, curDemonLevel, skills));
      //Read next line
      line = br.readLine();
    }
    br.close();

    //Log exit
    logger.exiting("FileOps", "readDemonList");
  }

  private static void readDemonCompendium(String race, List<Demon> compendium)
  throws NullPointerException, FileNotFoundException, IOException, NumberFormatException{
    //Log entry
    logger.entering("FileOps","readDemonCompendium");

    //Start by opening compendium file
    File f = new File("Compendium/Compendium");
    if(!f.exists()) {
      logger.warning("Error. The compendium file doesn't exist in expected location. Exiting");
      Runner.reportError("Couldn't find compendium file in expected location. Exiting");
      System.exit(1);
    }

    //Construct buffered reader over file
    BufferedReader br = new BufferedReader(new FileReader(f));

    //Read header line
    br.readLine();
    //Read all lines in file until desired race is finished, or until end of file is reached
    String previousRace = "";
    boolean raceFinished = false;
    String line = br.readLine();
    while(line != null && !raceFinished) {
      //First, tokenize line
      String[] tokens = line.split(COMMA_DELIMITER);
      //Read in race
      String curRace = tokens[0].toLowerCase();
      //Check if it's the race we're after
      if(!curRace.equals(race.toLowerCase())) {
        //If not, check if the last race encountered was
        if(previousRace.equals(race)) {
          //If so, we're done
          raceFinished = true;
          continue;
        }
        else {
          //Otherwise, read next line
          line = br.readLine();
          continue;
        }
      }
      //We have our desired race
      String curDemonName = tokens[1];
      int curDemonLevel = 0;
      try {
        curDemonLevel = Integer.valueOf(tokens[2]);
      }
      catch(NumberFormatException e) {
        logger.log(Level.INFO, "NumberFormatException reading compendium", e);
        Runner.reportError("Invalid formatting in compendium at line : " + tokens[0] + " " + curDemonName);
        System.exit(1);
      }
      //Read demon's skills
      Set<String> curDemonSkillSet = new HashSet<String>();
      String[] curDemonSkills = tokens[3].split(COLON_DELIMITER);
      for(String curSkill : curDemonSkills) {
        curDemonSkillSet.add(curSkill);
      }
      //Create and store new demon object
      compendium.add(new Demon(curRace, curDemonName, curDemonLevel, curDemonSkillSet));

      //Read next line
      line = br.readLine();
      //Update previous race
      previousRace = curRace;
    }
  }


  /**
    * Given a specific race, finds all pairs of races that fuse to produce specified race
    * @param the race for which we want to find fusion combinations
    * @param fCombination all located combinations are added to this list of string pairs
    * @throws NullPointerException if param race is null, or if fCombination is null
    * @throws FileNotFoundException if the file containing fusion rules is not located in expected location
    * @throws IOException if an IO error occurs while reading files
    */
  private static void readFusionCombinations(String race, List<String[]> fCombination)
  throws NullPointerException, FileNotFoundException, IOException {
    //Log entry
    logger.entering("FileOps", "readFusionCombinations");

    String raceFile = race.substring(0,1).toUpperCase() + race.substring(1);
    File f = new File("Rules/Combinations/"+raceFile);
    if(!f.exists()) {
      logger.warning("Error. The file containing fusion rules doesn't exist in expected location: " + raceFile);
      Runner.reportError("Couldn't find " + raceFile + "\'s fusion rules file in expected location. Exiting");
      System.exit(1);
    }

    BufferedReader br = new BufferedReader(new FileReader(f));
    //Read header line
    br.readLine();
    String line = br.readLine();
    //Read all lines in file and record any fusion combinations for desired race
    while(line != null) {
      //For current line, tokenize by commas
      String tokens[] = line.split(COMMA_DELIMITER);
      //First token is resulting race
      String curRace = tokens[0];
      if(curRace.equalsIgnoreCase(race)) {
        //Found race. Store current fusion combination
        String curFusionCombination[] = new String[2];
        curFusionCombination[0] = tokens[1];
        curFusionCombination[1] = tokens[2];
        fCombination.add(curFusionCombination);
      }
      //Read next line
      line = br.readLine();
    }
    br.close();

    //Log exit
    logger.exiting("FileOps","readDemonList");
  }

  /**
    * Reads in rules file and finds all element rules for a given race
    * Determines which elements result in up-fusion and down-fusion
    * @param race the race for which we want to determine element rules
    * @eUp elements that result in up-fusion are added to this list
    * @eDown elements that result in down-fusion are added to this list
    * @throws FileNotFoundException if the rules file is not found in expected location
    * @throws IOException if an IO error occurs during parsing of the rules file
    * @throws NullPointerException if param race is null, or if eUp or eDown are null
    */
  private static void readElementRules(String race, List<String> eUp, List<String> eDown)
  throws FileNotFoundException, IOException, NullPointerException {
    //Log entry
    logger.entering("FileOps","readElementRules");

    //Open file containing element rules
    File f = new File("Rules/ElementRules");
    if(!f.exists()) {
      logger.warning("The file containing all element rules wasn't found");
      Runner.reportError("Couldn't find ElementRules in expected location");
      System.exit(1);
    }
    BufferedReader br = new BufferedReader(new FileReader(f));

    //Read header
    br.readLine();
    //Read all lines in file until desired race is found, or until no more lines exist
    String line = br.readLine();
    boolean raceFound = false;
    while(line != null && !raceFound) {
      //Tokenize current line by commas
      String[] tokens = line.split(COMMA_DELIMITER);
      //Check if it's the desired race
      if(tokens[0].equalsIgnoreCase(race)) {
        //If so, store elements in appropriate lists
        String elementsUp[] = tokens[1].split(COLON_DELIMITER);
        for(int i=0; i<elementsUp.length; i++) {
          eUp.add(elementsUp[i]);
        }
        String elementsDown[] = tokens[2].split(COLON_DELIMITER);
        for(int i=0; i<elementsDown.length; i++) {
          eDown.add(elementsDown[i]);
        }
        //Record that we've found the race
        raceFound = true;
      }
      //Read next line
      line = br.readLine();
    }
    br.close();

    //Log exit
    logger.exiting("FileOps","readElementRules");
  }

  /**
    * Reads in file containing all special fusions, and stores them in
    * internal data structures, for use with SpecialFusion class
    * @param specials the list in which to store all special fusions found
    * @throws NullPointer exception if parameter specials is null
    * @throws IOException if IO error occurs while reading file
    * @throws FileNotFoundException if file can't be located in expected location
    */
    public static void populateSpecialFusions(Map<String,SpecialFusion> specials)
    throws IOException, NullPointerException, NumberFormatException, FileNotFoundException {
      //Log entry
      logger.entering("populateSpecialFusions", "FileOps");

      //Open file containing special fusions
      File f = new File("Special/SpecialFusions");
      if(!f.exists()) {
        System.out.println("The file containing all special fusions wasn't found");
        Runner.reportError("Couldn't find SpecialFusions in expected location");
        System.exit(1);
      }
      BufferedReader br = new BufferedReader(new FileReader(f));

      //Read file line by line
      String line = br.readLine(); //Read header file
      line = br.readLine();
      while(line != null) {
        //Tokenize string
        String tokens[] = line.split(COMMA_DELIMITER);
        //Ensure line is formatted properly
        if(tokens.length != 4) {
          logger.info("Formatting error in special fusions file at line: " + tokens[0] + " " + tokens[1]);
          Runner.reportError("Formatting error in special fusions file at line: " + tokens[0] + " " + tokens[1]);
          System.exit(1);
        }
        //Read in race, name and level of demon
        String race = tokens[0];
        String name = tokens[1];
        int level = Integer.valueOf(tokens[2]);
        //Read in special fusion components
        List<Demon> components = new ArrayList<Demon>();
        String componentNames[] = tokens[3].split(COLON_DELIMITER);
        for(int i=0; i<componentNames.length; i++) {
          String raceDemonPair[] = componentNames[i].split(RACE_DEMON_SPLITTER);
          //Print out error message if the line has a formatting error
          if(raceDemonPair.length != 2) {
            logger.info("Error in special fusions file at line: " + race + " " + name);
            Runner.reportError("Error in special fusions file at line: " + race + " " + name);
            System.exit(1);
          }
          Race curComponentRace = Race.fromString(raceDemonPair[0].toLowerCase());
          String demonName = raceDemonPair[1];
          Demon curDemon = null;
          try{
            curDemon = curComponentRace.getDemon(demonName);
          } catch(NullPointerException e) {
            logger.info("Couldn't find race " + raceDemonPair[0] + " in special fusions file at line: " + race + " " + name);
            logger.log(Level.FINE, "NullPointerException in populateSpecialFusions", e);
            Runner.reportError("Couldn't find race " + raceDemonPair[0] + " in special fusions file at line: " + race + " " + name);
            System.exit(1);
          }
          if(curDemon == null) {
            logger.info("Couldn't find demon " + demonName + " in race " + raceDemonPair[0] + " while reading special fusions file");
            Runner.reportError("Couldn't find demon " + demonName + " in race " + raceDemonPair[0] + " while reading special fusions file");
            System.exit(1);
          }
          components.add(curDemon);
        }
        //Find demon's skills from race data
        Set<String> skills = null;
        try {
          skills = Race.fromString(race.toLowerCase()).getDemon(name).getSkills();
        } catch(NullPointerException e) {
          logger.info("Couldn't find demon " + name + " in race " + race.toString());
          logger.log(Level.FINE, "NullPointerException in populateSpecialFusions", e);
          Runner.reportError("Couldn't find demon " + name + " in race " + race.toString() + " while reading special fusions");
          System.exit(1);
        }
        //Create demon and special fusion
        Demon finalDemon = new Demon(race, name, level, skills);
        SpecialFusion special = new SpecialFusion(finalDemon, components);

        //Add special fusion to list
        specials.put(name,special);

        //Read next line of file
        line = br.readLine();
      }
      br.close();

      logger.exiting("populateSpecialFusions","fileOps");
    }

    /**
      * Reads fusion chart file, and creates all fusion rule files inside appropriate directory
      */
    public static void readFusionChart() {
      //Log entry
      logger.entering("readFusionChart","FileOps");
      try {
        //First, open fusion chart file
        File f = new File("Rules/FusionChart");
        if(!f.exists()) {
          logger.warning("FusionChart file not found in expected location. Exiting");
          Runner.reportError("FusionChart file not found in expected location. Exiting");
          System.exit(1);
        }
        //Construct buffered reader
        BufferedReader br = new BufferedReader(new FileReader(f));
        //Read header file
        String line = br.readLine();
        //Construct hashmaps to store race index positions
        Map<String,Integer> raceToIndex = new HashMap<String,Integer>();
        Map<Integer,String> indexToRace = new HashMap<Integer,String>();
        //Construct hashmap to store fusion combinations
        Map<String,List<String[]>> fusionCombos = new HashMap<String,List<String[]>>();
        //Parse header file
        String[] header = line.split(COMMA_DELIMITER);
        for(int i=0; i<header.length; i++) {
          raceToIndex.put(header[i], new Integer(i));
          indexToRace.put(new Integer(i), header[i]);
          fusionCombos.put(header[i], new ArrayList<String[]>());
        }
        //Find number of races
        int numRaces = header.length;
        //Read all lines in file
        line = br.readLine();
        while(line != null) {
          //Tokenize the current line
          String[] tokens = line.split(COMMA_DELIMITER);
          //Find the current race
          String secondRace = tokens[0];
          //Compare current race to all other appropriate races and see fusion result
          for(int i=1; i<tokens.length; i++) {
            String fusionResult = tokens[i].trim();
            if(fusionResult.equals("-")) {
              //No fusion
              continue;
            }
            else {
              //Calculate offset for current race
              int offset = raceToIndex.get(secondRace) + 1;
              //Find first race
              int firstRacePosition = i+offset-1;
              String firstRace = indexToRace.get(firstRacePosition);
              //Now store fusion
              List<String[]> curFusionCombos = fusionCombos.get(fusionResult);
              String curPair[] = {firstRace, secondRace};
              curFusionCombos.add(curPair);
              fusionCombos.put(fusionResult, curFusionCombos);
            }
          }
          //Read next line in file
          line = br.readLine();
        }

        //Now, try and write all fusion combinations found out to files
        Set<String> races = fusionCombos.keySet();
        for(String curRace : races) {
          //Open file to write fusion combinations to
          FileWriter fw = new FileWriter("Rules/Combinations/"+curRace);
          fw.write("***Resulting Race, Race 1, Race 2***\n");
          //Read all fusion combinations
          List<String[]> curRaceCombos = fusionCombos.get(curRace);
          for(String[] curRaceCombination : curRaceCombos) {
            //Write current combination
            fw.write(curRace.trim()+","+curRaceCombination[1].trim()+","+curRaceCombination[0]+"\n");
          }
          //Close file
          fw.flush();
          fw.close();
        }
      }
      catch(IOException e) {
        logger.log(Level.WARNING, "IOException while reading fusion chart", e);
        Runner.reportError("Error while reading fusion chart. IOException");
        System.exit(1);
      }
      catch(NullPointerException e) {
        logger.log(Level.WARNING, "Null pointer exception while reading fusion chart", e);
        Runner.reportError("Error while reading fusiion chart. NullPointerException");
        System.exit(1);
      }
      //Log exit
      logger.exiting("readFusionChart","FileOps");
    }

    /**
      * Reads demon files, and constructs set of all skills in game
      * @return a set of skills (in string format)
      */
      public static Set<String> findSkillList()
      throws FileNotFoundException, IOException {
        //Construct empty result
        Set<String> skillsFound = new HashSet<String>();

        //Open all files in Demon Database directory
        File databaseDir = new File("Demon Database");
        File[] allRaceFiles = databaseDir.listFiles();

        for(File curRace : allRaceFiles) {
          if(curRace.isFile() && curRace.exists()) {
            //Construct buffered reader on file
            BufferedReader br = new BufferedReader(new FileReader(curRace));
            //Read header lines
            br.readLine();
            br.readLine();
            //Read all lines in current race file
            String line = br.readLine();
            while(line != null) {
              //Tokenize current line
              String tokens[] = line.split(COMMA_DELIMITER);
              //Tokenize 3rd token (skills list)
              String curDemonSkills[] = tokens[2].split(COLON_DELIMITER);
              //Add all of the current demon's skills to the overall set of skills
              for(String curSkill : curDemonSkills) {
                skillsFound.add(curSkill);
              }
              line = br.readLine();
            }
          }
        }
        //Return result
        return skillsFound;
      }

    /**
      * Given a set of fusion chain recipes for a demon, print them out to a file
      * @param recipes the set of recipes to produce the demon
      * @param desired the demon being fused (the recipes are for this demon)
      */
    public static void outputResults(Set<FusionChain> recipes, Demon desired)
    throws NullPointerException, IOException {
      //Log entry
      logger.entering("outputResults","FileOps");
      //Open output file
      FileWriter fw = new FileWriter(desired.getName()+".txt");
      //Write demon info
      fw.write("Fusing: " + desired.getRace().toString().substring(0,1).toUpperCase()+desired.getRace().toString().substring(1)+" "+desired.getName()+"\n");
      fw.write("Skills: " + desired.getSkills().toString()+"\n\n");
      //Loop over all fusion chains
      for(FusionChain recipe : recipes) {
        fw.write("\n");
        fw.write(recipe.toString(0,desired.getSkills()));
        fw.write("\n");
      }
      //Close file
      fw.flush();
      fw.close();
    }


    public static void outputSimpleFusionResults(List<Demon[]> recipes, Demon desired)
    throws NullPointerException, IOException {
      //Log entry
      logger.entering("outputSimpleFusionResults","FileOps");
      //Open output file
      FileWriter fw = new FileWriter(desired.getName()+".txt");
      //Write demon info
      fw.write("Fusing: " + desired.getRace().toString().substring(0,1).toUpperCase()+desired.getRace().toString().substring(1)+" "+desired.getName()+"\n");
      fw.write("Skills: " + desired.getSkills().toString()+"\n\n");
      //Loop over all recipes
      for(Demon[] recipe : recipes) {
        fw.write(recipe[0].getRace().toString().substring(0,1).toUpperCase()+recipe[0].getRace().toString().substring(1)+" "+recipe[0].getName());
        for(int i=1; i<recipe.length; i++) {
          fw.write(" + " + recipe[i].getRace().toString().substring(0,1).toUpperCase()+recipe[i].getRace().toString().substring(1)+" "+recipe[i].getName());
        }
        fw.write("\n");
      }
      //Close file
      fw.flush();
      fw.close();
    }
}
