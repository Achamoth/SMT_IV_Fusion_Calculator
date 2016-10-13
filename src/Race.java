import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;
import java.io.IOException;
import java.lang.NullPointerException;

public enum Race {
  Fury("fury"),
  Deity("deity"),
  Kunitsu("kunitsu"),
  Dragon("dragon"),
  Lady("lady");

  private List<String[]> fusionCombination;
  private List<String> elementsUp;
  private List<String> elementsDown;
  private List<Demon> demons;

  //Static string to enum map
  private static Map<String, Race> stringToEnum = new HashMap<String, Race>();
  private static Logger logger = Logger.getLogger("Race");

  static {
    //Populate string to enum map
    for(Race r : values()) {
      stringToEnum.put(r.toString(), r);
    }
    //Set up logger
    try {
      logger.setLevel(Level.ALL);
      Handler handler = new FileHandler("../Logs/Race.log");
      logger.addHandler(handler);
    }
    catch(IOException e) {
      logger.log(Level.SEVERE, "Couldn't create log file handler for race", e);
    }
  }

  Race(String race) {
    //Populate all data for race
    fusionCombination = new ArrayList<String[]>();
    elementsUp = new ArrayList<String>();
    elementsDown = new ArrayList<String>();
    demons = new ArrayList<Demon>();
    FileOps.populateRaceData(race, fusionCombination, elementsUp, elementsDown, demons);
  }

  /**
    *Returns a race enum, given a string name for the desired race
    *@param s the string representation of the race
    *return the race enum corresponding to the string representation
    */
  public static Race fromString(String s) {
    return stringToEnum.get(s);
  }

  public List<String[]> getFusionComponents() {
    return this.fusionCombination;
  }

  public List<String> getElementsUp() {
    return this.elementsUp;
  }

  public List<String> getElementsDown() {
    return this.elementsDown;
  }

  public List<Demon> getDemons() {
    return this.demons;
  }

  /**
    * Given a demon belonging to this race, return the base level of the demon
    * @param demon the demon for which we want to find the base level
    * @throws NullPointerException if demon is null
    * @return the base level of the supplied demon. -1 if supplied demon couldn't be found in race's demon list
    */
  public int getBaseLevel(Demon demon) {
    //Ensure demon isn't null
    if(demon == null)  {
      logger.fine("Null demon passed to getBaseLevel in Race");
      throw new NullPointerException("Null-pointer passed as parameter to getBaseLevel()");
    }
    //Get demon's name
    String name = demon.getName();
    //Search race's demon list
    for(Demon curDemon : this.demons) {
      if(curDemon.getName().equalsIgnoreCase(name)) {
        return curDemon.getLevel();
      }
    }
    //If we've made it here, the demon didn't exist in this race's demon list. Return -1
    return -1;
  }

}
