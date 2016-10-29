import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.lang.NullPointerException;
import java.awt.*;
import javax.swing.*;
import java.io.File;

public class Runner {

  //Integer constants for receiving error messages from FileOps, to print appropriate error message to GUI
  public static final int NULLPOINTER = 1;
  public static final int IO = 2;
  public static final int NUMBERFORMAT = 3;
  //Integer constants for reporting missing files
  public static final int ELEMENTFUSIONSFILE = 4;


  private static Map<String, SpecialFusion> specialFusions = new HashMap<String, SpecialFusion>();
  private static Logger logger = Logger.getLogger("SpecialFusion");
  private static Set<String> allSkills;
  private static Set<Demon> allDemons;
  private static Set<Race> allRaces;
  private static ProgramFrame frame;

  public static Map<String, SpecialFusion> getSpecialFusions() {
    return specialFusions;
  }

  public static ProgramFrame getFrame() {
    return frame;
  }

  public static Set<String> getAllSkills() {
    return allSkills;
  }

  public static String[] getAllSkillsArray() {
    String result[] = new String[allSkills.size()];
    int i=0;
    for(String skill : allSkills) {
      result[i++] = skill;
    }
    return result;
  }

  public static String[] getAllDemonsArray() {
    String result[] = new String[allDemons.size()];
    int i=0;
    for(Demon demon : allDemons) {
      result[i++] = demon.getName();
    }
    return result;
  }

  public static String[] getAllRacesArray() {
    String result[] = new String[allRaces.size()];
    int i=0;
    for(Race race : allRaces) {
      result[i++] = race.toString().substring(0,1).toUpperCase() + race.toString().substring(1);
    }
    return result;
  }

  public static void reportError(String error) {
    JOptionPane.showMessageDialog(new JFrame(), error);
  }

  public static void main(String[] args) {

    //Try and set up logs
    try {
      //Try to set up logger file handler
      logger.setLevel(Level.ALL);
      //Create log directory if it doesn't already exist
      File logDir = new File("Logs");
      logDir.mkdir();
      Handler handler = new FileHandler("Logs/Runner.log");
      logger.addHandler(handler);
    }
    catch (IOException e) {
      logger.log(Level.INFO, "Error setting up log file", e);
      JOptionPane.showMessageDialog(new JFrame(), "Couldn't create log file for Runner class");
    }

    //Now try and read in data
    try {
      //First, read all fusion rules
      FileOps.readFusionChart();
      //Next, read all special fusions
      FileOps.populateSpecialFusions(specialFusions);
      // Populate list of skills
      allSkills = FileOps.findSkillList();
    }
    catch (IOException e) {
      logger.log(Level.INFO, "IOException reading data files", e);
      JOptionPane.showMessageDialog(new JFrame(), "Error reading in data files");
      System.exit(1);
    }
    catch (NullPointerException e) {
      logger.log(Level.INFO, "NullPointerException reading data files", e);
      JOptionPane.showMessageDialog(new JFrame(), "Error reading in data files");
      System.exit(1);
    }
    catch(NumberFormatException e) {
      logger.log(Level.INFO, "NumberFormatException reading data files", e);
      JOptionPane.showMessageDialog(new JFrame(), "Formatting error in data files");
      System.exit(1);
    }

    //Populate set of demons and races
    allDemons = new HashSet<Demon>();
    allRaces = new HashSet<Race>();
    //Loop over all races
    for(Race r : Race.values()) {
      //Add race to set of races
      allRaces.add(r);
      //Loop over all demons in race
      List<Demon> demonsInRace = r.getDemons();
      for(Demon d : demonsInRace) {
        //Add demon to set of demons
        allDemons.add(d);
      }
    }

    EventQueue.invokeLater(new Runnable() {
      public void run()
      {
        //Set up frame
        frame = new ProgramFrame();
        frame.setTitle("SMT IV Fusion Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
      }
    });
  }
}
