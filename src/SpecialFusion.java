import java.util.List;
import java.util.ArrayList;
import java.util.logging.*;
import java.io.IOException;

public class SpecialFusion {

  private Demon result;
  private List<Demon> components;

  private static List<SpecialFusion> specialFusions;
  private static Logger logger = Logger.getLogger("SpecialFusion");

  //Populate special fusions list and set up logger
  static {
    specialFusions = new ArrayList<SpecialFusion>();

    try {
      logger.setLevel(Level.ALL);
      Handler handler = new FileHandler("../logs/SpecialFusion.log");
      logger.addHandler(handler);
    }
    catch(IOException e) {
      logger.log(Level.SEVERE, "Couldn't create log file handler", e);
    }

    try{
      FileOps.populateSpecialFusions(specialFusions);
    }
    catch(IOException e) {
      logger.log(Level.INFO, "IOException occurred while reading special fusion data", e);
      System.exit(1);
    }
    catch(NullPointerException e) {
      logger.log(Level.INFO, "Null list passed to populateSpecialFusions", e);
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

}
