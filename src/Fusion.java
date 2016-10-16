import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.*;
import java.io.IOException;

public class Fusion {

  private static final Logger logger = Logger.getLogger("Fusion");

  static {
    try {
      logger.setLevel(Level.ALL);
      Handler handler = new FileHandler("../Logs/Fusion.log");
      logger.addHandler(handler);
    }
    catch(IOException e) {
      logger.log(Level.SEVERE, "Couldn't create log file handler", e);
    }
  }

  /**
    * Find all possible combinations to fuse desired Demon
    * @param desiredDemon the demon result that is desired
    * @return a list of array demon pairs. Each pair is a possible fusion resulting in desired demon
    * @return returns null if the desired demon can only be created through special fusion, or if the specified skill set can't be obtained from direct components
    */
  public static List<Demon[]> fuse(Demon desiredDemon) {
    //Log entry
    logger.entering("Fusion","fuse");
    //Create list that stores results
    List<Demon[]> possibleFusionPairs = new ArrayList<Demon[]>();
    //First, find demon's race
    Race desiredRace = Race.fromString(desiredDemon.getRace().toLowerCase());

    //Check if race is element
    if(desiredRace.isElement()) {
      //If so, fusion is simple to perform. First, get the demon's name
      String demonName = desiredDemon.getName();
      //Now, find the corresponding Element enum
      Element element = Element.fromString(demonName);
      //Now, find list of races that can produce this element
      List<Race> races = element.getRaceFusionList();
      //For each race, compute all possible fusions
      for(Race r : races) {
        //Get all demons in current race
        List<Demon> demonsInRace = r.getDemons();
        //Find all pairs of demons that provide desired skill set
        int numDemons = demonsInRace.size();
        for(int i=0; i<numDemons; i++) {
          for(int j=i+1; j<numDemons; j++) {
            //Produce demon pair
            Demon[] curPair = new Demon[2];
            curPair[0] = demonsInRace.get(i);
            curPair[1] = demonsInRace.get(j);
            //Check if current pair provides desired skill set
            if(fusionObtainsSkills(desiredDemon, curPair)) {
              //Add current pair as a possible fusion combination
              possibleFusionPairs.add(curPair);
            }
          }
        }
      }
      //Return list of possible fusion pairs
      return possibleFusionPairs;
    }

    //Check if demon is a special fusion or not
    Map<String, SpecialFusion> specials = SpecialFusion.getSpecialFusions();
    if(specials.containsKey(desiredDemon.getName())) {
      //Return null. The caller should use special fusion method instead
      return null;
    }

    //Now, find the up-fusion through elements that can result in this demon
    List<String> eUp = desiredRace.getElementsUp();
    //For each element in here, calculate a possible demon to fuse with
    for(String curElement : eUp) {
      //TODO: Need to first store and read in elements
    }

    //Next, find the down-fusion through elements that can result in this demon
    List<String> eDown = desiredRace.getElementsDown();
    //For each element in here, calculate a possible demon to fuse with
    for(String curElement: eDown) {
      //TODO: Need to first store and read in elements
    }

    //Find two races that must be fused for this race
    List<String[]> racesNeeded = desiredRace.getFusionComponents();
    //For each combination of races, find possible fusions
    for(String[] curRacePair : racesNeeded) {
      //Now, for each race, get its list of demons
      Race r1 = Race.fromString(curRacePair[0].toLowerCase());
      Race r2 = Race.fromString(curRacePair[1].toLowerCase());
      //Get demon lists
      List<Demon> dlist1 = r1.getDemons();
      List<Demon> dlist2 = r2.getDemons();
      //Loop through all entries in first list
      for(Demon firstDemon : dlist1) {
        //Loop through all entries in r2
        for(Demon secondDemon : dlist2) {
          //Check average level of 2 demons
          int avgLvl = (r1.getBaseLevel(firstDemon) + r2.getBaseLevel(secondDemon))/2;
          //Check that average level is lower than desired demon's level
          if(avgLvl <= desiredDemon.getLevel()) {
            //Now, check that it's also higher than level of weaker demon from desired race
            Demon previousDemon = null;
            for(Demon curDemon : desiredRace.getDemons()) {
              //Check if it's the demon we're after
              if(curDemon.getName().equals(desiredDemon.getName())) {
                //It is. Break, and we'll have the previous demon
                break;
              }
              //Otherwise, move to next iteration
              previousDemon = curDemon;
            }
            //If previous demon is null, than our desired demon is the first demon
            if(previousDemon == null) {
              //Create demon pair
              Demon[] curPair = new Demon[2];
              curPair[0] = firstDemon;
              curPair[1] = secondDemon;

              //Check if pair will obtain desired skills
              boolean matchContainsSkills = fusionObtainsSkills(desiredDemon, curPair);

              if(matchContainsSkills) possibleFusionPairs.add(curPair);
            }
            //If previous demon's level is lower than average level, fusion is possible
            else if(previousDemon.getLevel() < avgLvl) {
              //Create demon pair
              Demon[] curPair = new Demon[2];
              curPair[0] = firstDemon;
              curPair[1] = secondDemon;

              //Check if pair will obtain desired skills
              boolean matchContainsSkills = fusionObtainsSkills(desiredDemon, curPair);

              if(matchContainsSkills) possibleFusionPairs.add(curPair);
            }
            //Otherwise, fusion won't result in desired demon
          }
        }
      }
    }
    //Log exit
    logger.exiting("Fusion","fuse");
    //Return result
    return possibleFusionPairs;
  }

  /**
    * Given a desired demon, and two components to fuse the demon, check if the components
    * will provide the desired demon's skill set
    * @param desirdDemon the demon that is desired from fusion
    * @param fusionComponents the demons that will be fused to obtain the desired demon
    * @return true if the components will provide for the desired demon's skill set
    */
    private static boolean fusionObtainsSkills(Demon desired, Demon[] components) {
      //Log entry
      logger.entering("Fusion","fusionObtainsSkills");
      //Store all skills acquired from components
      Set<String> skillsFromFusion = new HashSet<String>();

      //Loop over all components
      for(int i=0; i<components.length; i++) {
        Demon curDemon = components[i];
        Set<String> curDemonSkills = curDemon.getSkills();
        for(String curSkill : curDemonSkills) {
          skillsFromFusion.add(curSkill);
        }
      }

      //Find the demon's set of innate skills
      Set<String> innateSkills = null;
      Race race = Race.fromString(desired.getRace().toLowerCase());
      Demon demon = race.getDemon(desired.getName());
      innateSkills = demon.getSkills();

      if(innateSkills == null) {
        logger.warning("Couldn't find demon's innate skills. Demon is " + demon.getName());
        System.exit(1);
      }

      //Now, find set of all skills in desired demon
      Set<String> desiredSkills = desired.getSkills();

      //Check that all skills in desiredSkills exist in skillsFromFusion
      for(String curSkill : desiredSkills) {
        if(!skillsFromFusion.contains(curSkill) && !innateSkills.contains(curSkill)) {
          //Fusion won't provide for this skill. Return false
          logger.exiting("Fusion","fusionObtainsSkills");
          return false;
        }
      }
      logger.exiting("Fusion","fusionObtainsSkills");
      return true;
    }

    /**
      * Given a specification for a demon, find all fusion chains that will produce specified demon
      * @param demon the demon desired as the end result of fusion
      * @return a list of demon pair lists, with each list containing one valid fusion chain
      */
    List<List<Demon[]>> findFusionChains(Demon demon) {
      //TODO: Complete
      return null;
    }

    /**
      * Given a demon, return the set of all skills that can be acquired from that demon's possible fusions
      * @param demon the demon to be fused
      * @param depth since this method is called recursively, we will need to terminate at a specific depth. This parameter passes along the current depth
      * @return a set of all skills that can be required from valid fusion chains for that demon
      */
      Set<String> findPossibleSkills(Demon demon, int curDepth) {
        //TODO: Complete
        //TODO: The difficulty here is in setting termination flags. This could technically go on infinitely
        //TODO: Should set some sort of depth limit
        //TODO: May need to return list of sets instead, since each possible fusion of demon could have a different set of skills, so one set for each fusion combination
        return null;
      }
}
