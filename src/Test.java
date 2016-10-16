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

    //Try fusing an Ares with a specific skill set
    Set<String> skills = new HashSet<String>();
    //skills.add("Fatal Sword");
    skills.add("Agi");
    skills.add("Posmudi");
    skills.add("Bufu");
    skills.add("Fatal Sword");
    Demon desired = new Demon("Fury", "Ares", 31, skills);
    List<Demon[]> fusions = Fusion.fuse(desired);
    int i=1;
    for(Demon[] pairs : fusions) {
      System.out.printf("Pair %d: ", i++);
      System.out.printf("%s + %s\n", pairs[0].getName(), pairs[1].getName());
      System.out.println();
    }
    System.out.println();

    //Try fusing an element Salamander with a specific skill set
    skills = new HashSet<String>();
    skills.add("High Fire Pleroma");
    skills.add("Panic Voice");
    skills.add("Life Gain");
    skills.add("Kannuki-Throw");
    desired = new Demon("Element", "Salamander", 36, skills);
    fusions = Fusion.fuse(desired);
    i=1;
    for(Demon[] pairs : fusions) {
      System.out.printf("Pair %d: ", i++);
      System.out.printf("%s + %s\n", pairs[0].getName(), pairs[1].getName());
      System.out.println();
    }
    System.out.println();
  }
}
