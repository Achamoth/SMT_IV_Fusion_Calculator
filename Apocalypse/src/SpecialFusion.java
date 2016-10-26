import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.lang.NullPointerException;

public class SpecialFusion {

  private Demon result;
  private List<Demon> components;

  private static Map<String,SpecialFusion> specialFusions;
  private static Logger logger = Logger.getLogger("SpecialFusion");

  //Populate all special fusion data into hash map and initialize logger
  static {
    try {
      logger.setLevel(Level.ALL);
      Handler handler = new FileHandler("../logs/SpecialFusion.log");
      logger.addHandler(handler);
    }
    catch(IOException e) {
      logger.log(Level.SEVERE, "Couldn't create log file handler", e);
    }

    try{
      specialFusions = new HashMap<String,SpecialFusion>();
      FileOps.populateSpecialFusions(specialFusions);
    }
    catch(IOException e) {
      logger.log(Level.INFO, "IOException occurred while reading special fusion data", e);
      System.exit(1);
    }
    catch(NullPointerException e) {
      logger.log(Level.INFO, "Null pointer exception in populateSpecialFusions", e);
      System.exit(1);
    }
    catch(NumberFormatException e) {
      logger.log(Level.INFO, "Level can't be converted to int in special fusion data", e);
      System.exit(1);
    }
  }


  public SpecialFusion(Demon demon, List<Demon> components) {
    this.result = demon;
    this.components = components;
  }

  public Demon getResult() {
    return this.result;
  }

  public List<Demon> getComponents() {
    return this.components;
  }

  public static Map<String, SpecialFusion> getSpecialFusions() {
    return specialFusions;
  }

  public void printRecipe() {
    System.out.print(this.result.getRace().toString() + " " + this.result.getName() + ": ");
    System.out.print(this.components.get(0).getRace().toString().substring(0,1).toUpperCase()+this.components.get(0).getRace().toString().substring(1) + " " + this.components.get(0).getName());
    for(int i=1; i<this.components.size(); i++) {
      System.out.print(" + " + this.components.get(i).getRace().toString().substring(0,1).toUpperCase()+this.components.get(0).getRace().toString().substring(1) + " " + this.components.get(i).getName());
    }
    System.out.print("\n");
  }

}
