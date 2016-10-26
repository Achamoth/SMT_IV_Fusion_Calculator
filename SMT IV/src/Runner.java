import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;
import java.util.Scanner;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.lang.NullPointerException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Runner {

  private static Map<String, SpecialFusion> specialFusions = new HashMap<String, SpecialFusion>();
  private static Logger logger = Logger.getLogger("SpecialFusion");
  private static ProgramFrame frame;

  public static Map<String, SpecialFusion> getSpecialFusions() {
    return specialFusions;
  }

  public static ProgramFrame getFrame() {
    return frame;
  }

  public static void main(String[] args) {

    try {
      //Try to set up logger file handler
      logger.setLevel(Level.ALL);
      Handler handler = new FileHandler("../logs/Runner.log");
      logger.addHandler(handler);
      //First, read all fusion rules
      FileOps.readFusionChart();
      //Next, read all special fusions
      FileOps.populateSpecialFusions(specialFusions);
    }
    catch (IOException e) {
      logger.log(Level.INFO, "IOException reading data files", e);
      System.exit(1);
    }
    catch (NullPointerException e) {
      logger.log(Level.INFO, "NullPointerException reading data files", e);
      System.exit(1);
    }
    catch(NumberFormatException e) {
      logger.log(Level.INFO, "NumberFormatException reading data files", e);
      System.exit(1);
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

        //Set up scanner to take user input
        /*Scanner in = new Scanner(System.in);

        //Ask user for desired demon's details
        System.out.print("Would you like to use the compendium(y/n): ");
        String compendiumUsage = in.nextLine().trim();
        if(compendiumUsage.equals("y")) {
        Race.useCompendiumDemons();
        System.out.print("What skill threshold would you like for evaluating compendium demons: ");
        int skillThreshold = Integer.valueOf(in.nextLine().trim());
        Fusion.setSkillThreshold(skillThreshold);
      }
      else {
      Race.dontUseCompendiumDemons();
    }

    System.out.print("Enter the desired depth limit: ");
    // int desiredDepthLimit = in.nextInt();
    int desiredDepthLimit = Integer.valueOf(in.nextLine().trim());
    Fusion.setDepthLimit(desiredDepthLimit);

    System.out.print("Enter the desired demon's race: ");
    String desiredRace = in.nextLine().trim();

    System.out.print("Enter the desired demon's name: ");
    String desiredDemon = in.nextLine().trim();

    System.out.print("Enter the desired demon's level: ");
    int level = in.nextInt();

    System.out.print("How many skills would you like the demon to have: ");
    int numSkills = in.nextInt();

    System.out.println("Enter the desired skills:");
    in.nextLine();
    Set<String> skills = new HashSet<String>();
    for(int i=0; i<numSkills; i++) skills.add(in.nextLine().trim());
    System.out.println();

    System.out.print("How many fusion chains do you want: ");
    int numChains = Integer.valueOf(in.nextLine().trim());

    //Create demon object
    Demon desired = new Demon(desiredRace, desiredDemon, level, skills);

    //Find fusion chains for demon
    List<FusionChain> allChains = Fusion.findFusionChains(desired, numChains);
    //Get rid of duplicates
    Set<FusionChain> recipes = new HashSet<FusionChain>();
    for(FusionChain curChain : allChains) {
    recipes.add(curChain);
  }
  //Print fusion chains
  System.out.println();
  System.out.println("Fusing: " + desired.getRace().toString().substring(0,1).toUpperCase()+desired.getRace().toString().substring(1)+" "+desired.getName());
  System.out.println("Skills: " + skills.toString()+"\n");
  System.out.println();
  for(FusionChain recipe : recipes) {
  System.out.println();
  recipe.printChain(0,skills);
  System.out.println();
}*/
//System.out.println();
}
});
}
}

class ProgramFrame extends JFrame {

  //Sliders for modifiable attributes
  public JSlider depthSlider;
  public JSlider thresholdSlider;
  public JSlider chainSlider;

  //Checkboxes for boolean flags
  public JCheckBox useCompendium;
  public JCheckBox simpleSearch;
  public JCheckBox outputToFile;

  //Textboxes for demon (for fusion chain)
  public JTextField demonName;
  public JTextField demonRace;
  public JTextField skill1;
  public JTextField skill2;
  public JTextField skill3;
  public JTextField skill4;
  public JTextField skill5;
  public JTextField skill6;
  public JTextField skill7;
  public JTextField skill8;

  public ProgramFrame() {
    //Get screen dimensions
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    int screenHeight = screenSize.height;
    int screenWidth = screenSize.width;

    //Set frame height, width, and let platform pick screen location
    setSize(screenWidth/3, screenHeight/2);
    setLocationByPlatform(true);

    //Create content panel
    JPanel container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

    /* Add panel for the fusion attributes */
    JPanel attributes= new JPanel(new GridLayout(2,3));
    //Add search depth label and slider, and option for compendium search
    attributes.add(new JLabel("Search Depth"));
    depthSlider = new JSlider(0,10,5);
    depthSlider.setPaintTicks(true);
    depthSlider.setMajorTickSpacing(5);
    depthSlider.setMinorTickSpacing(1);
    attributes.add(depthSlider);
    useCompendium = new JCheckBox("Compendium");
    attributes.add(useCompendium);
    //Add skill threshold label and slider, and option for simple search
    attributes.add(new JLabel("Skill Threshold"));
    thresholdSlider = new JSlider(0,8,4);
    thresholdSlider.setPaintTicks(true);
    thresholdSlider.setMajorTickSpacing(4);
    thresholdSlider.setMinorTickSpacing(1);
    attributes.add(thresholdSlider);
    simpleSearch = new JCheckBox("Simple Search");
    attributes.add(simpleSearch);
    //Add number of chains label and slider
    JPanel lastItem = new JPanel(new GridLayout(1,3));
    lastItem.add(new JLabel("Number of Chains"));
    chainSlider = new JSlider(0,8,4);
    chainSlider.setPaintTicks(true);
    chainSlider.setMajorTickSpacing(4);
    chainSlider.setMinorTickSpacing(1);
    lastItem.add(chainSlider);
    outputToFile = new JCheckBox("Output to File");
    lastItem.add(outputToFile);

    /* Add panel for demon specification */
    JPanel demonChainTitle = new JPanel();
    demonChainTitle.add(new JLabel("Desired Demon:"));
    JPanel demonChain = new JPanel(new GridLayout(5,4));
    demonRace = new JTextField();
    demonName = new JTextField();
    skill1 = new JTextField();
    skill2 = new JTextField();
    skill3 = new JTextField();
    skill4 = new JTextField();
    skill5 = new JTextField();
    skill6 = new JTextField();
    skill7 = new JTextField();
    skill8 = new JTextField();
    demonChain.add(new JLabel("Race"));
    demonChain.add(demonRace);
    demonChain.add(new JLabel("Name"));
    demonChain.add(demonName);
    demonChain.add(new JLabel("Skill 1"));
    demonChain.add(skill1);
    demonChain.add(new JLabel("Skill 2"));
    demonChain.add(skill2);
    demonChain.add(new JLabel("Skill 3"));
    demonChain.add(skill3);
    demonChain.add(new JLabel("Skill 4"));
    demonChain.add(skill4);
    demonChain.add(new JLabel("Skill 5"));
    demonChain.add(skill5);
    demonChain.add(new JLabel("Skill 6"));
    demonChain.add(skill6);
    demonChain.add(new JLabel("Skill 7"));
    demonChain.add(skill7);
    demonChain.add(new JLabel("Skill 8"));
    demonChain.add(skill8);
    JPanel searchButton = new JPanel();
    JButton mainSearch = new JButton("Find fusion chain(s)");
    FusionSearch searchAction = new FusionSearch();
    mainSearch.addActionListener(searchAction);
    searchButton.add(mainSearch);

    //Add the sub-panels to the container
    container.add(attributes);
    container.add(lastItem);
    container.add(demonChainTitle);
    container.add(demonChain);
    container.add(searchButton);

    //Add container to JFrame
    add(container);
    pack();
  }
}

class FusionSearch implements ActionListener {
  public void actionPerformed(ActionEvent event) {
    /* The user has pressed the fusion search button. Perform fusion search */

    //Get frame
    ProgramFrame frame = Runner.getFrame();
    //Find out whether user wants to use compendium
    boolean useCompendium = frame.useCompendium.isSelected();
    boolean simpleSearch = frame.simpleSearch.isSelected();
    boolean toFile = frame.outputToFile.isSelected();

    //Find demon data
    String name = frame.demonName.getText();
    String race = frame.demonRace.getText();
    String[] skills = new String[8];
    skills[0] = frame.skill1.getText();
    skills[1] = frame.skill2.getText();
    skills[2] = frame.skill3.getText();
    skills[3] = frame.skill4.getText();
    skills[4] = frame.skill5.getText();
    skills[5] = frame.skill6.getText();
    skills[6] = frame.skill7.getText();
    skills[7] = frame.skill8.getText();
    Set<String> skillSet = new HashSet<String>();
    for(int i=0; i<8; i++) {
      if(!skills[i].isEmpty()) {
        skillSet.add(skills[i]);
      }
    }

    //Find search attributes
    int searchDepth = frame.depthSlider.getValue();
    int skillThreshold = frame.thresholdSlider.getValue();
    int numChains = frame.chainSlider.getValue();;

    /* CHECK DATA */
    //Make sure that race and name have been entered
    if(race.isEmpty() || name.isEmpty()) {
      //Print error message
      JOptionPane.showMessageDialog(frame, "The demon's race and name must be filled in");
      return ;
    }

    //Make sure that race exists
    Race r = Race.fromString(race.toLowerCase());
    if(r == null) {
      //Print an error
      JOptionPane.showMessageDialog(frame, "The specified race wasn't found");
      return ;
    }
    //Make sure that demon exists
    Demon base = r.getDemon(name);
    if(base == null) {
      //Print an error
      JOptionPane.showMessageDialog(frame, "The specified demon wasn't found in the specified race");
      return ;
    }

    /* Action the flags */
    //Compendium search
    if(useCompendium) Race.useCompendiumDemons();
    else Race.dontUseCompendiumDemons();
    //Search depth
    Fusion.setDepthLimit(searchDepth);
    //Skill threshold
    Fusion.setSkillThreshold(skillThreshold);

    //Create demon object
    Demon desired = new Demon(race, name, 99, skillSet);

    //Perform search
    if(simpleSearch) {
      //TODO: Complete this
      //Perform simple search
    }
    else {
      //Find fusion chains for demon
      List<FusionChain> allChains = Fusion.findFusionChains(desired, numChains);
      //Get rid of duplicates
      Set<FusionChain> recipes = new HashSet<FusionChain>();
      for(FusionChain curChain : allChains) {
        recipes.add(curChain);
      }

      //Output fusion chains
      if(toFile) {
        //Output results to file
        try {
          FileOps.outputResults(recipes, desired);
        }
        catch(Exception e) {
          System.out.println("Error writing results to output file");
        }
      }
      else {
        //Print fusion chains
        //TODO: DON'T PRINT TO STDOUT. PRINT TO A NEW WINDOW
        System.out.println();
        System.out.println("Fusing: " + desired.getRace().toString().substring(0,1).toUpperCase()+desired.getRace().toString().substring(1)+" "+desired.getName());
        System.out.println("Skills: " + skillSet.toString()+"\n");
        System.out.println();
        for(FusionChain recipe : recipes) {
          System.out.println();
          recipe.printChain(0,skillSet);
          System.out.println();
        }
      }
    }
  }
}
