import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.*;
import java.io.IOException;

public class Fusion {

  //DEPTH LIMIT FOR RECURSIVE FUSION CHAIN SEARCH
  private static final int DEPTH_LIMIT = 4;

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
      //Get the demon representing the current element
      Race elRace = Race.fromString("element");
      Demon element = elRace.getDemon(curElement);

      //Get strongest demon from desired demon's race
      Demon strongestInRace = desiredRace.getStrongestDemon();
      //Compute fusion between strongest demon and element, if the desired demon is the weakest in the race
      Demon[] curPair = {strongestInRace, element};
      if(fusionObtainsSkills(desiredDemon, curPair) && desiredRace.getWeakestDemon().getName().equalsIgnoreCase(desiredDemon.getName())) {
        //Add this final pair to list of fusion pairs
        possibleFusionPairs.add(curPair);
      }

      //Get all demons of a lower base level, belonging to the same race as the desired demon
      List<Demon> weaker = desiredRace.getWeakerDemons(desiredDemon);
      if(weaker.size() == 0) {
        //If desired demon is the weakest demon, move to next element
        continue;
      }
      //Get strongest demon from list of weaker demoms
      Demon strongestAmongWeaker = weaker.get(weaker.size()-1);
      //For the strongest demon among the list of weaker demons, compute a fusion with the element
      curPair[0] = strongestAmongWeaker;
      curPair[1] = element;
      if(fusionObtainsSkills(desiredDemon, curPair)) {
        //Add current pair to list of fusion pairs
        possibleFusionPairs.add(curPair);
      }

    }

    //Next, find the down-fusion through elements that can result in this demon
    List<String> eDown = desiredRace.getElementsDown();
    //For each element in here, calculate a possible demon to fuse with
    for(String curElement: eDown) {
      //Get the demon representing the current element
      Race elRace = Race.fromString("element");
      Demon element = elRace.getDemon(curElement);

      //Get all demons of a higher base level, belonging to the same race as the desired demon
      List<Demon> stronger = desiredRace.getStrongerDemons(desiredDemon);
      if(stronger.size() == 0) {
        //Desired demon is the strongest in its race. Can't be produce through down fusion with elements
        break;
      }
      //Get the weakest demon among the stronger demons
      Demon weakestAmongStronger = stronger.get(stronger.size()-1);
      //For the weakest of the stronger demons, compute a fusion with the element
      Demon[] curPair = {weakestAmongStronger, element};
      if(fusionObtainsSkills(desiredDemon, curPair)) {
        //Add current pair to list of fusion pairs
        possibleFusionPairs.add(curPair);
      }
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
          //Check that average level is lower than desired demon's base level
          if(avgLvl <= desiredRace.getBaseLevel(desiredDemon)) {
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
  * Find all possible combinations to fuse desired Demon
  * @param desiredDemon the demon result that is desired
  * @return a list of array demon pairs. Each pair is a possible fusion resulting in desired demon
  * @return returns null if the desired demon can only be created through special fusion, or if the specified skill set can't be obtained from direct components
  */
  public static List<Demon[]> fuseWithoutSkillRequirements(Demon desiredDemon) {
    //Log entry
    logger.entering("Fusion","fuseWithoutSkillRequirements");
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
        //Find all pairs of demons that produce desired demon
        int numDemons = demonsInRace.size();
        for(int i=0; i<numDemons; i++) {
          for(int j=i+1; j<numDemons; j++) {
            //Produce demon pair
            Demon[] curPair = new Demon[2];
            curPair[0] = demonsInRace.get(i);
            curPair[1] = demonsInRace.get(j);
            //Add current pair as a possible fusion combination
            possibleFusionPairs.add(curPair);
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
      //Get the demon representing the current element
      Race elRace = Race.fromString("element");
      Demon element = elRace.getDemon(curElement);

      //Get strongest demon from desired demon's race
      Demon strongestInRace = desiredRace.getStrongestDemon();
      //Compute fusion between strongest demon and element, if the desired demon is the weakest in the race
      Demon[] curPair = {strongestInRace, element};
      if(desiredRace.getWeakestDemon().getName().equalsIgnoreCase(desiredDemon.getName())) {
        //Add this final pair to list of fusion pairs
        possibleFusionPairs.add(curPair);
      }

      //Get all demons of a lower base level, belonging to the same race as the desired demon
      List<Demon> weaker = desiredRace.getWeakerDemons(desiredDemon);
      if(weaker.size() == 0) {
        //If desired demon is the weakest demon, move to next element
        continue;
      }
      //Get strongest demon from list of weaker demoms
      Demon strongestAmongWeaker = weaker.get(weaker.size()-1);
      //For the strongest demon among the list of weaker demons, compute a fusion with the element
      curPair[0] = strongestAmongWeaker;
      curPair[1] = element;
      //Add current pair to list of fusion pairs
      possibleFusionPairs.add(curPair);
    }

    //Next, find the down-fusion through elements that can result in this demon
    List<String> eDown = desiredRace.getElementsDown();
    //For each element in here, calculate a possible demon to fuse with
    for(String curElement: eDown) {
      //Get the demon representing the current element
      Race elRace = Race.fromString("element");
      Demon element = elRace.getDemon(curElement);

      //Get all demons of a higher base level, belonging to the same race as the desired demon
      List<Demon> stronger = desiredRace.getStrongerDemons(desiredDemon);
      if(stronger.size() == 0) {
        //Desired demon is the strongest in its race. Can't be produce through down fusion with elements
        break;
      }
      //Get the weakest demon among the stronger demons
      Demon weakestAmongStronger = stronger.get(stronger.size()-1);
      //For the weakest of the stronger demons, compute a fusion with the element
      Demon[] curPair = {weakestAmongStronger, element};
      //Add current pair to list of fusion pairs
      possibleFusionPairs.add(curPair);
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
          //Check that average level is lower than desired demon's base level
          if(avgLvl <= desiredRace.getBaseLevel(desiredDemon)) {
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
              possibleFusionPairs.add(curPair);
            }
            //If previous demon's level is lower than average level, fusion is possible
            else if(previousDemon.getLevel() < avgLvl) {
              //Create demon pair
              Demon[] curPair = new Demon[2];
              curPair[0] = firstDemon;
              curPair[1] = secondDemon;
              possibleFusionPairs.add(curPair);
            }
            //Otherwise, fusion won't result in desired demon
          }
        }
      }
    }
    //Log exit
    logger.exiting("Fusion","fuseWithoutSkillRequirements");
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
  public static boolean fusionObtainsSkills(Demon desired, Demon[] components) {
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
  * Given a specification for a demon, find a fusion chain that will produce that demon, or a demon closest to it
  * @param demon the demon desired as the end result of fusion
  * @param curDepth the method is recursive, so this records the depth that the method has reached
  * @return a FusionChain object, which represents a fusion recipe for the desired demon
  */
  public static FusionChain findFusionChains(Demon demon, int curDepth) {
    //Log entry
    logger.entering("Fusion","findFusionChains");

    //Check that depth hasn't exceeded depth limit
    if(curDepth == DEPTH_LIMIT) {
      //Depth limit reached. Terminate at this demon
      Demon base = Race.fromString(demon.getRace().toLowerCase()).getDemon(demon.getName());
      return new FusionChain(base);
    }

    //Check if the desired demon is a special fusion
    Map<String, SpecialFusion> specials = SpecialFusion.getSpecialFusions();
    if(specials.containsKey(demon.getName())) {
      //Desired demon is a special demon. Return null for now
      //TODO: Fix this up
      // return null;
      Demon base = Race.fromString(demon.getRace().toLowerCase()).getDemon(demon.getName());
      return new FusionChain(base);
    }

    //If not, find all possible fusion combinations for desired demon and work over them
    else {
      int numSkillsFound = 0;
      int mostSkillsFound = 0;
      Demon base = Race.fromString(demon.getRace().toLowerCase()).getDemon(demon.getName());
      FusionChain bestChain = new FusionChain(base);
      //Get all fusion combinations
      List<Demon[]> combinations = fuseWithoutSkillRequirements(demon);
      for(Demon[] curCombination : combinations) {
        //Find the base demon
        Race desiredRace = Race.fromString(demon.getRace().toLowerCase());
        Demon baseDesired = desiredRace.getDemon(demon.getName());
        //Create empty fusion chain result with desired demon (base version) at top
        FusionChain result = new FusionChain(baseDesired);
        //Construct new set of skills for this combination
        Set<String> foundSkills = new HashSet<String>();
        int numCurSkills = 0;
        //Add the two components to the fusion chain
        Demon base1 = Race.fromString(curCombination[0].getRace().toLowerCase()).getDemon(curCombination[0].getName());
        Demon base2 = Race.fromString(curCombination[1].getRace().toLowerCase()).getDemon(curCombination[1].getName());
        result.addChain(new FusionChain(base1));
        result.addChain(new FusionChain(base2));
        //Add all skills in current fusion chain to set
        result.addSkillsInChain(foundSkills);
        //Find out how many of the desired skills have been found
        numCurSkills = numberOfSkillsFound(demon, foundSkills);
        //Check if the skills required in the desired demon have been found
        if(numCurSkills == demon.getNumSkills()) {
          //This combination works and provides all desired skills
          return result;
        }

        else {
          //Check result against best found so far
          if(numCurSkills > mostSkillsFound) {
            mostSkillsFound = numCurSkills;
            bestChain = result;
          }

          //Recursively call this method on each of the components to find new fusion chains
          Set<String> skillsLacking = findSkillDefficiencies(demon, result);
          //Find a fusion chain for the first component with these skills
          Demon comp1 = new Demon(result.getChain(0).getDemon());
          comp1.setSkills(skillsLacking);
          //Now find fusion chains for this demon
          result.setChain(0, findFusionChains(comp1, curDepth+1));

          //Now check skills again after finding new fusion chain for first component
          result.addSkillsInChain(foundSkills);
          numCurSkills = numberOfSkillsFound(demon, foundSkills);
          if(numCurSkills == demon.getNumSkills()) {
            //This chain works and provides all desired skill
            return result;
          }

          //Otherwise, find all skills still lacking
          skillsLacking = findSkillDefficiencies(demon, result);
          //Find a fusion chain for the second component with these skills
          Demon comp2 = new Demon(result.getChain(1).getDemon());
          comp2.setSkills(skillsLacking);
          //Now find fusion chains for this demon
          result.setChain(1, findFusionChains(comp2,curDepth+1));

          //Now check skills again after finding new fusion chains
          result.addSkillsInChain(foundSkills);
          numCurSkills = numberOfSkillsFound(demon, foundSkills);
          if(numCurSkills == demon.getNumSkills()) {
            //This chain works and provides all desired skills
            return result;
          }
          else if (numCurSkills > mostSkillsFound) {
            //Check result against best found so far
            mostSkillsFound = numCurSkills;
            bestChain = result;
          }
        }
      }
      //Return best chain found. Need to change demon to base demon before returning though
      logger.exiting("Fusion","findFusionChains");
      return bestChain;
    }
  }

  /**
    * Given a demon and a set of skills, return the number of skills that exist in the set, which also exist in the demon
    * @param demon the demon in question
    * @param the set of skills in question
    * @return the number of common skills between the demon and the provided set
    */
  private static int numberOfSkillsFound(Demon demon, Set<String> foundSkills) {
    int result = 0;
    Set<String> demonSkills = demon.getSkills();
    for(String curSkill : foundSkills) {
      if(demonSkills.contains(curSkill)) {
        result++;
      }
    }
    return result;
  }

  /**
    * Given a specified demon and a fusion chain, find the skills owned by the demon, that don't exist in the fusion chain
    * @param demon the demon in question, with the set of skills being checked against
    * @param chain the fusion chain being checked against the demon's skill-set
    * @return the set of skills owned by the demon not existing in the fusion chain
    */
  private static Set<String> findSkillDefficiencies(Demon demon, FusionChain chain) {
    Set<String> result = new HashSet<String>();
    //Find all skills in chain
    Set<String> skillsInChain = new HashSet<String>();
    chain.addSkillsInChain(skillsInChain);
    //Find all skills in demon
    Set<String> skillsInDemon = demon.getSkills();
    //Compare them
    for(String curDemonSkill : skillsInDemon) {
      if(!skillsInChain.contains(curDemonSkill)) {
        result.add(curDemonSkill);
      }
    }
    return result;
  }
}
