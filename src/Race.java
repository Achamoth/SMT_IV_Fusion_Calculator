import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;
import java.io.IOException;
import java.lang.NullPointerException;

public enum Race {
  Deity("deity"),
  Amatsu("amatsu"),
  Megami("megami"),
  Nymph("nymph"),
  Enigma("enigma"),
  Entity("entity"),
  Godly("godly"),
  Chaos("chaos"),
  Fury("fury"),
  Kunitsu("kunitsu"),
  Kishin("kishin"),
  Zealot("zealot"),
  Lady("lady"),
  Reaper("reaper"),
  Vile("vile"),
  Tyrant("tyrant"),
  Genma("genma"),
  Yoma("yoma"),
  Fairy("fairy"),
  Night("night"),
  Herald("herald"),
  Divine("divine"),
  Fallen("fallen"),
  Avian("avian"),
  Flight("flight"),
  Raptor("raptor"),
  Jirae("jirae"),
  Brute("brute"),
  Femme("femme"),
  Jaki("jaki"),
  Dragon("dragon"),
  Snake("snake"),
  Drake("drake"),
  Avatar("avatar"),
  Holy("holy"),
  Food("food"),
  Beast("beast"),
  Wilder("wilder"),
  Tree("tree"),
  Wood("wood"),
  Ghost("ghost"),
  Spirit("spirit"),
  Undead("undead"),
  Vermin("vermin"),
  Foul("foul"),
  Element("element"),
  Fiend("fiend"),
  Famed("famed");

  private List<String[]> fusionCombination;
  private List<String> elementsUp;
  private List<String> elementsDown;
  private List<Demon> demons;
  private Map<String, Demon> stringToDemon;

  //Static string to enum map
  private static Map<String, Race> stringToEnum = new HashMap<String, Race>();

  static {
    //Populate string to enum map
    for(Race r : values()) {
      stringToEnum.put(r.toString().toLowerCase(), r);
    }
  }

  Race(String race) {
    //Populate all data for race
    fusionCombination = new ArrayList<String[]>();
    elementsUp = new ArrayList<String>();
    elementsDown = new ArrayList<String>();
    demons = new ArrayList<Demon>();
    FileOps.populateRaceData(race, fusionCombination, elementsUp, elementsDown, demons);
    //Populate string to demon map for race
    stringToDemon = new HashMap<String, Demon>();
    for(Demon d : demons) {
      stringToDemon.put(d.getName(), d);
    }
  }

  /**
    *Returns a race enum, given a string name for the desired race
    *@param s the string representation of the race. MUST BE LOWER CASE
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
    //Create a copy of the list
    List<Demon> result = new ArrayList<Demon>();
    for(Demon d : this.demons) {
      result.add(new Demon(d));
    }
    return result;
  }

  /**
    * Returns boolean value signifying whether this race enum is an element or not
    * @return true if race is element, false otherwise
    */
  public boolean isElement() {
    return this.toString().equalsIgnoreCase("element");
  }

  /**
    * Given a demon name, find and return the corresponding demon
    * @param name the desired demon's name
    * @return the corresponding demon object
    */
  public Demon getDemon(String name) {
    return new Demon(stringToDemon.get(name));
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
      //throw new NullPointerException("Null-pointer passed as parameter to getBaseLevel()");
    }
    //Get demon's name
    String name = demon.getName();
    //Get base demon off demon map
    Demon base = this.stringToDemon.get(name);
    if(base == null) {
      return -1;
    }
    return base.getLevel();
  }

  public List<Demon> getWeakerDemons(Demon demon) {
    //Create empty list to store results
    List<Demon> result = new ArrayList<Demon>();
    //Get supplied demon's base level
    int baseLevel = this.getBaseLevel(demon);
    //Loop through all demons in race's demon list
    for(Demon cur : this.demons) {
      if(cur.getLevel() < baseLevel) {
        result.add(cur);
      }
      else {
        break;
      }
    }
    //Return list
    return result;
  }

  public List<Demon> getStrongerDemons(Demon demon) {
    //Create empty list to store results
    List<Demon> result = new ArrayList<Demon>();
    //Get supplied demon's base level
    int baseLevel = this.getBaseLevel(demon);
    //Loop through all demons in race's demon list in reverse order
    for(int i=this.demons.size()-1; i>=0; i--) {
      Demon cur = this.demons.get(i);
      if(cur.getLevel() > baseLevel) {
        result.add(cur);
      }
      else {
        break;
      }
    }
    return result;
  }

  public Demon getWeakestDemon() {
    return this.demons.get(0);
  }

  public Demon getStrongestDemon() {
    return this.demons.get(demons.size()-1);
  }

}
