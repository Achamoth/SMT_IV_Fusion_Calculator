import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;
import java.util.Scanner;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.lang.NullPointerException;

public class Runner {

  private static Map<String, SpecialFusion> specialFusions = new HashMap<String, SpecialFusion>();
  private static Logger logger = Logger.getLogger("SpecialFusion");

  public static void main(String[] args) {
    try {
      //Try to set up logger file handler
      logger.setLevel(Level.ALL);
      Handler handler = new FileHandler("../logs/Runner.log");
      logger.addHandler(handler);
      //First, read all fusion rules
      FileOps.readFusionChart();
      //Next, read all special fusions
      FileOps.populateSpecialFusions(specialFusions);
    }
    catch (IOException e) {

    }
    catch (NullPointerException e) {

    }
    catch(NumberFormatException e) {

    }
    //Set up scanner to take user input
    /*Scanner in = new Scanner(System.in);

    //Ask user for desired demon's details
    //CLEAN THIS UP
    System.out.print("Enter the desired demon's race: ");
    String desiredRace = in.nextLine().trim();
    System.out.print("Enter the desired demon's name: ");
    String desiredDemon = in.nextLine().trim();
    System.out.print("Enter the desired demon's level: ");
    int level = in.nextInt();
    System.out.print("How many skills would you like the demon to have: ");
    int numSkills = in.nextInt();
    System.out.println("Enter the desired skills:");
    in.nextLine();
    Set<String> skills = new HashSet<String>();
    for(int i=0; i<numSkills; i++) skills.add(in.nextLine().trim());

    //Create demon object
    Demon desired = new Demon(desiredRace, desiredDemon, level, skills);

    //Fuse demon
    if(specialFusions.containsKey(desiredDemon)) {
      //Demon is a special fusion
      System.out.println();
      SpecialFusion special = specialFusions.get(desiredDemon);
      //Check if the special fusion combination produces desired skills
      List<Demon> componentList = special.getComponents();
      Demon[] componentArray = new Demon[componentList.size()];
      int i=0;
      for(Demon d : componentList) {
        componentArray[i++] = d;
      }
      if(Fusion.fusionObtainsSkills(desired, componentArray)) {
        special.printRecipe();
      }
    }
    else {
      //Demon is a normal fusion
      List<Demon[]> fusions = Fusion.fuse(desired);
      int i=1;
      //Print fusion combinations
      System.out.println();
      System.out.println("Fusing: " + desired.getRace().toString().substring(0,1).toUpperCase()+desired.getRace().toString().substring(1)+" "+desired.getName());
      System.out.println("Skills: " + skills.toString()+"\n");
      for(Demon[] pairs : fusions) {
        System.out.printf("Pair %d: ", i++);
        System.out.printf("%s + %s\n", pairs[0].getRace().toString().substring(0,1).toUpperCase()+pairs[0].getRace().toString().substring(1)+" "+pairs[0].getName(),
                                       pairs[1].getRace().toString().substring(0,1).toUpperCase()+pairs[1].getRace().toString().substring(1)+" "+pairs[1].getName());
        System.out.println();
      }
    }
    System.out.println();*/

    //Attempt a fusion chain
    Set<String> asuraSkills = new HashSet<String>();
    asuraSkills.add("Enduring Soul");
    asuraSkills.add("Repel Phys");
    asuraSkills.add("Repel Fire");
    asuraSkills.add("Grand Tack");
    asuraSkills.add("Null Elec");
    Demon Asura = new Demon("Fury", "Asura", 99, asuraSkills);

    FusionChain recipe = Fusion.findFusionChains(Asura, 0);

    //Try printing fusion chain
    recipe.printChain(0, asuraSkills);

    //Try printing shiva
    Race fury = Race.fromString("fury");
    Demon shiva = fury.getCompendiumDemon("Shiva");
    if(shiva == null) System.out.println("Not found");
    else System.out.println(shiva.toString());
  }
}
