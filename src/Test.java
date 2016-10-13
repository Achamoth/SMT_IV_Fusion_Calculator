import java.util.Set;
import java.util.HashSet;
import java.util.List;

public class Test {
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
  }
}
