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

  public FusionChain(FusionChain chain) {
    this.demon = new Demon(chain.getDemon());
    //Clone the components list
    List<Demon> inputComponents = chain.getComponents();
    this.components = new ArrayList<Demon>();
    for(Demon curComp : inputComponents) {
      this.components.add(new Demon(curComp));
    }
    //Clone the chains list
    List<FusionChain> inputChains = chain.getChains();
    this.chains = new ArrayList<FusionChain>();
    for(FusionChain curChain : inputChains) {
      this.chains.add(new FusionChain(curChain));
    }
  }

  public List<Demon> getComponents() {
    return this.components;
  }

  public List<FusionChain> getChains() {
    return this.chains;
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
    //Add all skills that can be found off the compendium demon
    Demon compendiumDemon = Race.fromString(demon.getRace().toString().toLowerCase()).getCompendiumDemon(demon.getName());
    if(compendiumDemon != null) {
      Set<String> skillsFromCompDemon = compendiumDemon.getSkills();
      for(String curSkill : skillsFromCompDemon) {
        skills.add(curSkill);
      }
    }

    //If there is no compendium demon, add all skills that can be found off the base demon result of the chain
    else {
      Set<String> skillsFromDemon = demon.getSkills();
      for(String curSkill : skillsFromDemon) {
        skills.add(curSkill);
      }
    }

    //Add all skills that can be acquired from component's fusion chains
    if(this.components == null || this.chains == null) return ;
    for(FusionChain c : this.chains) {
      c.addSkillsInChain(skills);
    }
  }

  /**
    * Print the chain as a fusion recipe
    * @param depth the current depth since the method is called recursively. Used to print correct number of tabs
    * @param skills a set of skills in the end desired demon. Used to print where skills are acquired in the fusion chain
    */
  public void printChain(int depth, Set<String> skills) {
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
    //Print the rest of the components
    for(int i=1; i<this.components.size(); i++) {
      Demon curComponent = this.components.get(i);
      String curRace = curComponent.getRace().toString().substring(0,1).toUpperCase() + curComponent.getRace().toString().substring(1);
      String curName = curComponent.getName();
      System.out.print(" + " + curRace + " " + curName);
    }
    System.out.print("\n");
    //Loop through all the components and print where the skills are found
    for(Demon curComponent : this.components) {
      //If the current component contains a skill desired in the final demon, print this information
      if(componentContainsSkill(curComponent, skills)) {
        //Print all the skills this component provides
        for(int i=0; i<depth; i++) System.out.print("\t");
        System.out.print(curComponent.getRace().toString().substring(0,1).toUpperCase() + curComponent.getRace().toString().substring(1));
        System.out.print(" " + curComponent.getName() + ": ");
        //Find all skills provided by this component
        Set<String> skillsProvidedByComponent = findSkillsProvidedByDemon(curComponent, skills);
        //Print skills
        System.out.print(skillsProvidedByComponent.toString());
        System.out.print("\n");
      }
    }

    //Now, print all fusion chains
    for(FusionChain chain : this.chains) {
      chain.printChain(depth+1, skills);
    }
  }

  /**
    * Given a demon and a set of skills, returns true if the demon contains any skills specified in the set; false otherwise
    * @param demon the demon being examined
    * @param skills the set of skills being checked against
    * @return true or false, depending on outcome
    */
  private static boolean componentContainsSkill(Demon d, Set<String> skills) {
    //Find the compendium demon
    Demon compDemon = Race.fromString(d.getRace().toLowerCase()).getCompendiumDemon(d.getName());
    if(compDemon != null) {
      //There is a compendium demon. Find its skills
      Set<String> compSkills = compDemon.getSkills();
      //Check for matches
      for(String curSkill : compSkills) {
        if(skills.contains(curSkill)) {
          return true;
        }
      }
      return false;
    }

    //If there is no compendium demon, find base demon's set of skills
    Set<String> demonSkills = d.getSkills();
    //Check for matches
    for(String curSkill : demonSkills) {
      if(skills.contains(curSkill)) {
        return true;
      }
    }
    return false;
  }

  /**
    * Given a demon and a set of skills, returns the set of common skills between the provided skill set, and the demon's skill set
    * @param demon the demon being investigated
    * @param skills the set of skills being checked against for common skills with the demon
    * @return a set of skills (as strings) that are common between the demon, and the parameter skills
    */
  private static Set<String> findSkillsProvidedByDemon(Demon d, Set<String> skills) {
    //Construct empty result
    Set<String> result = new HashSet<String>();

    //Find the compendium demon, if it exists, and return its set of skills
    Demon compDemon = Race.fromString(d.getRace().toLowerCase()).getCompendiumDemon(d.getName());
    if(compDemon !=  null) {
      Set<String> skillsfromCompDemon = compDemon.getSkills();
      for(String curSkill : skillsfromCompDemon) {
        if(skills.contains(curSkill)) {
          result.add(curSkill);
        }
      }
      return result;
    }

    //If there is no compendium demon, get base demon's set of skills
    Set<String> demonSkills = d.getSkills();
    //Loop over all of the demon's skills
    for(String curSkill : demonSkills) {
      //If current skill exists in 'skills' param, add it to the result
      if(skills.contains(curSkill)) {
        result.add(curSkill);
      }
    }
    return result;
  }

    @Override
  public boolean equals(Object o) {
    if(o == null) {
      return false;
    }
    if(!(o instanceof FusionChain)) {
      return false;
    }
    FusionChain c = (FusionChain) o;
    return (this.demon.equals(c.getDemon())
           && this.components.equals(c.getComponents())
           && this.chains.equals(c.getChains()));
  }

    @Override
  public int hashCode() {
    int code = 83;
    code *= this.demon.hashCode();
    code += code * this.components.hashCode();
    code *= code + this.chains.hashCode();
    return code;
  }
}
