import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
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
    Scanner in = new Scanner(System.in);

    //Ask user for desired demon's details
    //CLEAN THIS UP
    System.out.print("Would you like to use the compendium(y/n) (NOT READY YET): ");
    String compendiumUsage = in.nextLine().trim();
    compendiumUsage = "n";
    if(compendiumUsage.equals("y")) Race.useCompendiumDemons();
    else Race.dontUseCompendiumDemons();

    System.out.print("Enter the desired depth limit: ");
    // int desiredDepthLimit = in.nextInt();
    int desiredDepthLimit = Integer.valueOf(in.nextLine().trim());
    Fusion.setDepthLimit(desiredDepthLimit);

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
    System.out.println();

    System.out.print("How many fusion chains do you want: ");
    int numChains = Integer.valueOf(in.nextLine().trim());

    //Create demon object
    Demon desired = new Demon(desiredRace, desiredDemon, level, skills);

    //Find fusion chains for demon
    List<FusionChain> allChains = Fusion.findFusionChains(desired, numChains);
    //Get rid of duplicates
    Set<FusionChain> recipes = new HashSet<FusionChain>();
    for(FusionChain curChain : allChains) {
      recipes.add(curChain);
    }
    //Print fusion chains
    System.out.println();
    System.out.println("Fusing: " + desired.getRace().toString().substring(0,1).toUpperCase()+desired.getRace().toString().substring(1)+" "+desired.getName());
    System.out.println("Skills: " + skills.toString()+"\n");
    System.out.println();
    for(FusionChain recipe : recipes) {
      System.out.println();
      recipe.printChain(0,skills);
      System.out.println();
    }
    System.out.println();
  }
}
