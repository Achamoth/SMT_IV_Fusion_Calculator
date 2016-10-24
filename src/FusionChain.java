import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class FusionChain {

  //Demon result
  private Demon demon;
  //Components
  private List<Demon> components;
  //Fusion chains to fuse components
  private List<FusionChain> chains;

  public FusionChain (Demon demon) {
    this.demon = demon;
    components = new ArrayList<Demon>();
    chains = new ArrayList<FusionChain>();
  }

  public FusionChain getChain(int n) {
    return this.chains.get(n);
  }

  public Demon getComponent(int n) {
    return this.components.get(n);
  }

  public Demon getDemon() {
    return this.demon;
  }

  public void addChain(FusionChain c) {
    this.chains.add(c);
    this.components.add(c.getDemon());
  }

  public void setChain(int n, FusionChain c) {
    this.chains.set(n, c);
  }

  public void setComponent(int n, Demon d) {
    this.components.set(n, d);
  }

  public void setDemon(Demon demon) {
    this.demon = demon;
  }

  /**
    * Given a set of skills, add all the skills that can be acquired from this fusion chain to the set
    * @param skills the set to add all possible skills to
    */
  public void addSkillsInChain(Set<String> skills) {
    //Add all skills that can be found off the demon result of the chain
    Set<String> skillsFromDemon = demon.getSkills();
    for(String curSkill : skillsFromDemon) {
      skills.add(curSkill);
    }
    //Add all skills that can be acuired from component's fusion chains
    if(this.components == null || this.chains == null) return ;
    for(FusionChain c : this.chains) {
      c.addSkillsInChain(skills);
    }
  }

  /**
    * Print the chain as a fusion recipe
    */
  public void printChain(int depth) {
    //Only print if there are components
    if(this.components.size() == 0) return ;
    //Print demon name
    String demonRace = this.demon.getRace().toString().substring(0,1).toUpperCase() + this.demon.getRace().toString().substring(1);
    String demonName = this.demon.getName();
    //Print tabs
    for(int i=0; i<depth; i++) System.out.print("\t");
    System.out.print(demonRace + " " + demonName + " = " );
    //Print first component
    String firstRace = this.components.get(0).getRace().toString().substring(0,1).toUpperCase() + this.components.get(0).getRace().toString().substring(1);
    String firstName = this.components.get(0).getName();
    System.out.print(firstRace + " " + firstName);
    for(int i=1; i<this.components.size(); i++) {
      Demon curComponent = this.components.get(i);
      String curRace = curComponent.getRace().toString().substring(0,1).toUpperCase() + this.components.get(i).getRace().toString().substring(1);
      String curName = curComponent.getName();
      System.out.print(" + " + curRace + " " + curName);
    }
    System.out.print("\n");

    //Now, print all fusion chains
    for(FusionChain chain : this.chains) {
      chain.printChain(depth+1);
    }
  }
}
