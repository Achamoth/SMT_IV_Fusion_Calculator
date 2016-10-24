import java.util.HashSet;
import java.util.Set;

public class Demon {

  private int level;
  private String race;
  private Set<String> skills;
  private String name;

  /**
    *Constructor for demon class
    *Creates new Demon with specified attributes
    *Initializes an empty hash set if no skills are passed in
    */
  public Demon(String race, String name, int level, Set<String> skills) {
    this.race = race;
    this.name = name;
    this.level = level;
    if(skills != null) {
      this.skills = skills;
    }
    else {
      this.skills = new HashSet<String>();
    }
  }

  public Demon(Demon d) {
    this.level = d.getLevel();
    this.name = d.getName();
    this.race = d.getRace();
    this.skills = d.getSkills();
  }

  //Getter methodds
  public String getRace() {
    return this.race;
  }

  public int getLevel() {
    return this.level;
  }

  public String getName() {
    return this.name;
  }

  public Set<String> getSkills() {
    return this.skills;
  }

  public int getNumSkills() {
    return this.skills.size();
  }

  //Simple setter method for skills
  public void setSkills(Set<String> skills) {
    if(skills == null) {
      this.skills = new HashSet<String>();
    }
    else {
      this.skills = skills;
    }
  }

  /**
    *Adds a new skill to the demon's skill set, so long as the demon has room
    *If the demon already has 8 skills, the addition will fail and method returns false
    *If addition succeeds, method returns true
    *@param newSkill the skill to be added
    *@return a boolean value, indicating whether addition was successful or not
    */
  public boolean addSkill(String newSkill) {
    if(skills.size() < 8) {
      this.skills.add(newSkill);
      return true;
    }
    else {
      return false;
    }
  }

  /**
    *Swaps one of the demon's existing skills for a new skill
    *Returns true if the swap is successful
    *If the demon doesn't have the existing skill, swap fails, and false is returned
    *@param oldSkill the skill to be swapped out
    *@param newSkill the skill to be swapped in
    *@return a boolean indicating success/failure
    */
  public boolean swapSkill(String oldSkill, String newSkill) {
    if(skills.contains(oldSkill)) {
      skills.remove(oldSkill);
      skills.add(newSkill);
      return true;
    }
    else {
      return false;
    }
  }

  /**
    * Returns string representation of current demon
    *
    */
    @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.race + "\n");
    sb.append(this.name + "\n");
    sb.append(this.level + "\n");
    sb.append(this.skills.toString() + "\n");
    sb.append("\n");
    return sb.toString();
  }

}
