import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.*;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.lang.NullPointerException;

public class Test {

  private static List<SpecialFusion> specialFusions = new ArrayList<SpecialFusion>();
  private static Logger logger = Logger.getLogger("SpecialFusion");

  public static void main(String[] args) {

    //Try fusing an Asura with a specific skill set
    /*Set<String> skills = new HashSet<String>();
    Demon desired = new Demon("Fury", "Ares", 99, null);
    List<Demon[]> fusions = Fusion.fuse(desired);
    int i=1;
    System.out.println("Fusing: " + desired.getName());
    for(Demon[] pairs : fusions) {
      System.out.printf("Pair %d: ", i++);
      System.out.printf("%s + %s\n", pairs[0].getName(), pairs[1].getName());
      System.out.println();
    }
    System.out.println();*/

    //Try printing all fusion rules for Deity
    /*Race race = Race.fromString("deity");
    List<String[]> combinations = race.getFusionComponents();
    for(String[] curCombination : combinations) {
      System.out.println(race.toString() + ": " + curCombination[0] + " + " + curCombination[1]);
    }*/

    //Try running fusion chart reader
    FileOps.readFusionChart();
  }
}
