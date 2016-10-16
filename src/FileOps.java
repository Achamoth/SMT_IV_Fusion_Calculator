import java.util.logging.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.lang.NumberFormatException;


/**
  *A class for performing i/o with files
  *Finds fusion combination and demon data from database files
  *Also outputs data to files
  */

public class FileOps {

  private static final String COMMA_DELIMITER = ",";
  private static final String COLON_DELIMITER = ":";
  private static final String RACE_DEMON_SPLITTER = "%";
  private static final String NEWLINE_DELIMITER = "\n";
  private static final Logger logger = Logger.getLogger("FileOps");

  static {
    //Set up logger
    try {
      logger.setLevel(Level.ALL);
      Handler handler = new FileHandler("../Logs/FileOps.log");
      logger.addHandler(handler);
    }
    catch (IOException e) {
      logger.log(Level.SEVERE, "Couldn't create log file handler for FileOps class", e);
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
  public static void populateRaceData(String race, List<String[]> fCombination, List<String> eUp, List<String> eDown, List<Demon> demons) {
    //Log entry
    logger.entering("FileOps","populateRaceData");

    readattempt: try {
      //First, read in all demons
      readDemonList(race, demons);
      //Next, read in fusion combination
      readFusionCombinations(race, fCombination);
      //Finally, read in element rules
      readElementRules(race, eUp, eDown);
    }
    catch(IOException e) {
      logger.log(Level.INFO, "An IOException occurred while reading files", e);
      System.exit(1);
    }
    catch(NullPointerException e) {
      logger.log(Level.FINE, "Null pointer exception. Race name was likely null", e);
      System.exit(1);
    }
    catch (NumberFormatException e) {
      logger.log(Level.INFO, "Couldn't parse demon level as integer value", e);
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
      File f = new File("../Rules/ElementFusions");
      if(!f.exists()) {
        System.out.println("Error. Couldn't find element fusion rules in expected location");
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
      System.exit(1);
    }
    catch (NullPointerException e) {
      logger.log(Level.INFO, "Null pointer exception in populateElementFusionRules", e);
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
    File f = new File("../Demon Database/" + race);
    //Ensure file exists
    if(!f.exists()) {
      logger.warning("Error. The file " + race +".txt does not exist in expected directory. Exiting program...");
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
    File f = new File("../Rules/Combinations/"+raceFile);
    if(!f.exists()) {
      logger.warning("Error. The file containing fusion rules doesn't exist in expected location");
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
    File f = new File("../Rules/ElementRules");
    if(!f.exists()) {
      logger.warning("The file containing all element rules wasn't found");
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
    * Reads in file containing list of all demons, and stores them in
    * internal data structures
    * @param compendium the set in which to store all demons found in file
    * @throws NullPointerException if parameter compendium is null
    */
  public static void readCompendium(Set<Demon> compendium) {
    //TODO
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
      File f = new File("../Special/SpecialFusions");
      if(!f.exists()) {
        System.out.println("The file containing all special fusions wasn't found");
        System.exit(1);
      }
      BufferedReader br = new BufferedReader(new FileReader(f));

      //Read file line by line
      String line = br.readLine(); //Read header file
      line = br.readLine();
      while(line != null) {
        //Tokenize string
        String tokens[] = line.split(COMMA_DELIMITER);
        //Read in race, name and level of demon
        String race = tokens[0];
        String name = tokens[1];
        int level = Integer.valueOf(tokens[2]);
        //Read in special fusion components
        List<Demon> components = new ArrayList<Demon>();
        String componentNames[] = tokens[3].split(COLON_DELIMITER);
        for(int i=0; i<componentNames.length; i++) {
          String raceDemonPair[] = componentNames[i].split(RACE_DEMON_SPLITTER);
          Race curComponentRace = Race.fromString(raceDemonPair[0].toLowerCase());
          String demonName = raceDemonPair[1];
          Demon curDemon = curComponentRace.getDemon(demonName);
          components.add(curDemon);
        }
        //Read in demon's skills
        String skills[] = tokens[4].split(COLON_DELIMITER);
        Set<String> skillset = new HashSet<String>();
        for(int i=0; i<skills.length; i++) {
          skillset.add(skills[i]);
        }
        //Create demon and special fusion
        Demon finalDemon = new Demon(race, name, level, skillset);
        SpecialFusion special = new SpecialFusion(finalDemon, components);

        //Add special fusion to list
        specials.put(name,special);

        //Read next line of file
        line = br.readLine();
      }
      br.close();

      logger.exiting("populateSpecialFusions","fileOps");
    }
}
