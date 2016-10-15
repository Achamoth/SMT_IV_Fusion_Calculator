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
    Set<String> skills = new HashSet<String>();
    //skills.add("Fatal Sword");
    skills.add("Agi");
    skills.add("Posmudi");
    skills.add("Bufu");
    skills.add("Fatal Sword");
    Demon ares = new Demon("Fury", "Ares", 31, skills);
    List<Demon[]> getFusions = Fusion.fuse(ares);
    int i=1;
    for(Demon[] pairs : getFusions) {
      System.out.printf("Pair %d: ", i++);
      System.out.printf("%s + %s\n", pairs[0].getName(), pairs[1].getName());
      System.out.println();
    }

    //Try getting all special fusions
    Map<String, SpecialFusion> specialFusions = SpecialFusion.getSpecialFusions();
    Set<String> specialFusionDemons = specialFusions.keySet();
    for(String curSpecialName : specialFusionDemons) {
      //Get demon
      SpecialFusion curSpecial = specialFusions.get(curSpecialName);
      //Print out special fusion name
      System.out.println("Demon Name: " + curSpecial.getResult().getName());
      System.out.println("Skills: " + curSpecial.getResult().getSkills().toString());
      System.out.println();
    }
  }
}
