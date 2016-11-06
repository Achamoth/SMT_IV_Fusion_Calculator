import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class ProgramFrame extends JFrame {

  //Sliders for modifiable attributes
  public JSlider depthSlider;
  public JSlider thresholdSlider;
  public JSlider chainSlider;

  //Checkboxes for boolean flags
  public JCheckBox useCompendium;
  public JCheckBox simpleSearch;
  public JCheckBox outputToFile;

  //Textboxes for demon (for fusion chain)
  public JComboBox demonName;
  public JComboBox demonRace;
  public JComboBox skill1;
  public JComboBox skill2;
  public JComboBox skill3;
  public JComboBox skill4;
  public JComboBox skill5;
  public JComboBox skill6;
  public JComboBox skill7;
  public JComboBox skill8;

  //Text field for simple demon search
  public JComboBox simpleSearchDemon;
  //Text field for skill search
  public JComboBox skillTextField;

  public ProgramFrame() {
    //Get screen dimensions
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    int screenHeight = screenSize.height;
    int screenWidth = screenSize.width;

    //Set frame height, width, and let platform pick screen location
    setSize(screenWidth/3, screenHeight/2);
    setLocationByPlatform(true);

    //Create content container
    JPanel container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

    /* Crete panel for the fusion attributes */
    JPanel attributes= new JPanel(new GridLayout(2,3));

    //Add search depth label and slider on first row
    attributes.add(new JLabel("Search Depth"));
    depthSlider = new JSlider(0,10,5);
    depthSlider.setPaintTicks(true);
    depthSlider.setMajorTickSpacing(5);
    depthSlider.setMinorTickSpacing(1);
    attributes.add(depthSlider);
    //Add checkbox for demon compendium
    useCompendium = new JCheckBox("Compendium");
    attributes.add(useCompendium);


    //Add skill threshold label and slider on new row
    attributes.add(new JLabel("Skill Threshold"));
    thresholdSlider = new JSlider(0,8,4);
    thresholdSlider.setPaintTicks(true);
    thresholdSlider.setMajorTickSpacing(4);
    thresholdSlider.setMinorTickSpacing(1);
    attributes.add(thresholdSlider);
    //Add checkbox for simple search
    simpleSearch = new JCheckBox("Simple Search");
    attributes.add(simpleSearch);


    //Add number of chains label and slider on new row
    JPanel lastItem = new JPanel(new GridLayout(1,3));
    lastItem.add(new JLabel("Number of Chains"));
    chainSlider = new JSlider(0,8,4);
    chainSlider.setPaintTicks(true);
    chainSlider.setMajorTickSpacing(4);
    chainSlider.setMinorTickSpacing(1);
    lastItem.add(chainSlider);
    //Add checkbox for outputting results to file
    outputToFile = new JCheckBox("Output to File");
    lastItem.add(outputToFile);

    /* Create panel for demon specification */
    //First, retrieve array of all demon names, races and skills for combo boxes
    String skills[] = Runner.getAllSkillsArray();
    String demons[] = Runner.getAllDemonsArray();
    String races[] = Runner.getAllRacesArray();
    //Sort the arrays alphabetically
    Arrays.sort(skills);
    Arrays.sort(demons);
    Arrays.sort(races);
    //Add title (Desired Demon)
    JPanel demonChainTitle = new JPanel();
    demonChainTitle.add(new JLabel("Desired Demon:"));
    JPanel demonChain = new JPanel(new GridLayout(5,4));
    //Create all textfield components to be added
    demonRace = new JComboBox(races);
    demonRace.setEditable(true);
    demonRace.setSelectedIndex(-1);
    AutoCompletion.enable(demonRace);
    demonName = new JComboBox(demons);
    AutoCompletion.enable(demonName);
    demonName.setEditable(true);
    demonName.setSelectedIndex(-1);
    skill1 = new JComboBox(skills);
    AutoCompletion.enable(skill1);
    skill1.setEditable(true);
    skill1.setSelectedIndex(-1);
    skill2 = new JComboBox(skills);
    AutoCompletion.enable(skill2);
    skill2.setEditable(true);
    skill2.setSelectedIndex(-1);
    skill3 = new JComboBox(skills);
    AutoCompletion.enable(skill3);
    skill3.setEditable(true);
    skill3.setSelectedIndex(-1);
    skill4 = new JComboBox(skills);
    AutoCompletion.enable(skill4);
    skill4.setEditable(true);
    skill4.setSelectedIndex(-1);
    skill5 = new JComboBox(skills);
    AutoCompletion.enable(skill5);
    skill5.setEditable(true);
    skill5.setSelectedIndex(-1);
    skill6 = new JComboBox(skills);
    AutoCompletion.enable(skill6);
    skill6.setEditable(true);
    skill6.setSelectedIndex(-1);
    skill7 = new JComboBox(skills);
    AutoCompletion.enable(skill7);
    skill7.setEditable(true);
    skill7.setSelectedIndex(-1);
    skill8 = new JComboBox(skills);
    AutoCompletion.enable(skill8);
    skill8.setEditable(true);
    skill8.setSelectedIndex(-1);
    //Add race label and textfield
    demonChain.add(new JLabel("Race"));
    demonChain.add(demonRace);
    //Add name label and textfield
    demonChain.add(new JLabel("Name"));
    demonChain.add(demonName);

    //Add skill 1 label and textfield on new row
    demonChain.add(new JLabel("Skill 1"));
    demonChain.add(skill1);
    //Add skill 2 label and textfield
    demonChain.add(new JLabel("Skill 2"));
    demonChain.add(skill2);

    //Add skill 3 label and textfield on new row
    demonChain.add(new JLabel("Skill 3"));
    demonChain.add(skill3);
    //Add skill 4 label and textfield
    demonChain.add(new JLabel("Skill 4"));
    demonChain.add(skill4);

    //Add skill 5 label and textfield on new row
    demonChain.add(new JLabel("Skill 5"));
    demonChain.add(skill5);
    //Add skill 6 label and textfield
    demonChain.add(new JLabel("Skill 6"));
    demonChain.add(skill6);

    //Add skill 7 label and textfield on new row
    demonChain.add(new JLabel("Skill 7"));
    demonChain.add(skill7);
    //Add skill 8 label and textfield
    demonChain.add(new JLabel("Skill 8"));
    demonChain.add(skill8);

    //Create new panel for buttons
    JPanel buttons = new JPanel();
    //Create clear button
    JButton clear = new JButton("Clear");
    //Add Listener to clear button
    ClearScreen clearAction = new ClearScreen();
    clear.addActionListener(clearAction);
    //Create search button
    JButton mainSearch = new JButton("Find fusion chain(s)");
    //Add listener to search button
    FusionSearch searchAction = new FusionSearch();
    mainSearch.addActionListener(searchAction);
    //Add buttons
    buttons.add(clear);
    buttons.add(mainSearch);

    //Create new panel for demon searh
    JPanel demonSearch = new JPanel();
    demonSearch.setLayout(new GridLayout(1,3));
    //Create label and text field and add to panel
    demonSearch.add(new JLabel("Demon Search"));
    simpleSearchDemon = new JComboBox(demons);
    simpleSearchDemon.setSelectedIndex(-1);
    AutoCompletion.enable(simpleSearchDemon);
    simpleSearchDemon.setEditable(true);
    demonSearch.add(simpleSearchDemon);
    //Create button, add listener to it, and add button to panel
    JButton simpleDemonSearchButton = new JButton("Search");
    DemonSearch demonSearchAction = new DemonSearch();
    simpleDemonSearchButton.addActionListener(demonSearchAction);
    demonSearch.add(simpleDemonSearchButton);

    //Create new panel for skill search
    JPanel skillSearch = new JPanel();
    skillSearch.setLayout(new GridLayout(1,3));
    //Create new label and text field and add to panel
    skillSearch.add(new JLabel("Skill Search"));
    skillTextField = new JComboBox(skills);
    skillTextField.setSelectedIndex(-1);
    AutoCompletion.enable(skillTextField);
    skillTextField.setEditable(true);
    skillSearch.add(skillTextField);
    //Create button, add listener to it, and add button to panel
    JButton skillSearchButton = new JButton("Search");
    SkillSearch skillSearchAction = new SkillSearch();
    skillSearchButton.addActionListener(skillSearchAction);
    skillSearch.add(skillSearchButton);


    //Add the sub-panels to the container
    container.add(attributes);
    container.add(lastItem);
    container.add(demonChainTitle);
    container.add(demonChain);
    container.add(buttons);
    container.add(demonSearch);
    container.add(skillSearch);

    //Add container to JFrame
    add(container);
    pack();
  }
}

class ClearScreen implements ActionListener {
  public void actionPerformed(ActionEvent event) {
    /* The user has pressed the clear button. Clear all entries */
    //First, get the frame from runner class
    ProgramFrame frame = Runner.getFrame();

    //Clear all demon spec entries
    frame.demonRace.setSelectedIndex(-1);
    frame.demonName.setSelectedIndex(-1);
    frame.skill1.setSelectedIndex(-1);
    frame.skill2.setSelectedIndex(-1);
    frame.skill3.setSelectedIndex(-1);
    frame.skill4.setSelectedIndex(-1);
    frame.skill5.setSelectedIndex(-1);
    frame.skill6.setSelectedIndex(-1);
    frame.skill7.setSelectedIndex(-1);
    frame.skill8.setSelectedIndex(-1);

    //Clear skill and demon search entries as well
    frame.simpleSearchDemon.setSelectedIndex(-1);
    frame.skillTextField.setSelectedIndex(-1);
  }
}

class DemonSearch implements ActionListener {
  public void actionPerformed(ActionEvent event) {
    /* The user has pressed the demon search button. Perform demon search */
    //First, get the frame from runner class
    ProgramFrame frame = Runner.getFrame();

    //Now, get the string value of the demon name that was searched by the user
    String demonName = (String) frame.simpleSearchDemon.getSelectedItem();

    //Ensure it isn't empty
    if(demonName.isEmpty()) {
      JOptionPane.showMessageDialog(frame, "A demon name must be entered");
      return ;
    }

    //Try to find the demon
    for(Race race : Race.values()) {
      //Pretty bad, but I'll loop over all races and check each one for the demon
      Demon demon = race.getDemon(demonName);
      if(demon == null) {
        continue;
      }

      //If demon was found, print it's info
      JDialog dialog = new JDialog(frame);
      JPanel panel = new JPanel(new BorderLayout());
      JTextArea output = new JTextArea();

      //Add all output to a string builder
      StringBuilder sb = new StringBuilder();
      //Add the demon's race
      sb.append(demon.getRace().substring(0,1).toUpperCase() + demon.getRace().substring(1));
      //Add the demon's name
      sb.append(" " + demon.getName() + "\n");
      //Add the demon's base level
      sb.append("Base Level: " + demon.getLevel() + "\n");
      //Add the demon's skills
      Set<String> demonSkills = demon.getSkills();
      for(String skill : demonSkills) {
        sb.append(skill + "\n");
      }
      //Add output to text area
      output.setText(sb.toString());

      //Create new scroll pane with output
      JScrollPane scroll = new JScrollPane(output);
      panel.add(scroll, BorderLayout.CENTER);
      dialog.getContentPane().add(panel);
      dialog.setVisible(true);
      dialog.pack();

      //Exit method
      return ;
    }

    //If nothing was found by now, print error, as demon doesn't exist in data
    JOptionPane.showMessageDialog(frame, "Demon couldn't be found. Please note that search is case-sensitive");
  }
}

class SkillSearch implements ActionListener {
  public void actionPerformed(ActionEvent event) {
    /* The user has pressed the skill search button. Perform skill search */
    //First, get the frame from the runner class
    ProgramFrame frame = Runner.getFrame();

    //Get the string value of the skill from the frame
    String desiredSkill = (String) frame.skillTextField.getSelectedItem();

    //Make sure it isn't empty
    if(desiredSkill.isEmpty()) {
      JOptionPane.showMessageDialog(frame, "A skill must be enetered");
      return ;
    }

    //Store a list of demons that have the skill
    List<Demon> demonsWithSkill = new ArrayList<Demon>();

    //Try to find the skill
    for(Race race : Race.values()) {
      List<Demon> demons = race.getDemons();
      for(Demon demon : demons) {
        //Check if the current demon has the skill
        if(demon.getSkills().contains(desiredSkill)) {
          //Add the demon to the list
          // String curResult = demon.getRace().substring(0,1).toUpperCase() + demon.getRace().substring(1) + " " + demon.getName();
          demonsWithSkill.add(demon);
        }
      }
    }

    //If no demons were found, return an error message
    if(demonsWithSkill.isEmpty()) {
      JOptionPane.showMessageDialog(frame, "No demons with that skill were found. Please note that search is case-sensitive");
      return ;
    }

    //Otherwise, return list of demons
    JDialog dialog = new JDialog(frame);
    JPanel panel = new JPanel(new BorderLayout());
    JTextArea output = new JTextArea();

    //Sort list of demons by level
    demonsWithSkill.sort(new Comparator<Demon>() {
      public int compare(Demon d1, Demon d2) {
        return d1.getLevel() - d2.getLevel();
      }
    });

    StringBuilder sb = new StringBuilder();
    sb.append(desiredSkill + ":\n");
    for(Demon demon : demonsWithSkill) {
      sb.append(demon.getRace().substring(0,1).toUpperCase() + demon.getRace().substring(1) + " " + demon.getName() + "\n");
    }
    output.setText(sb.toString());

    JScrollPane scroll = new JScrollPane(output);
    panel.add(scroll, BorderLayout.CENTER);
    dialog.getContentPane().add(panel);
    dialog.setVisible(true);
    dialog.pack();
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

    //Get list of all demon skills
    Set<String> allSkills = Runner.getAllSkills();

    //Find demon data
    String name = (String) frame.demonName.getSelectedItem();
    String race = (String) frame.demonRace.getSelectedItem();
    String[] skills = new String[8];
    skills[0] = (String) frame.skill1.getSelectedItem();
    skills[1] = (String) frame.skill2.getSelectedItem();
    skills[2] = (String) frame.skill3.getSelectedItem();
    skills[3] = (String) frame.skill4.getSelectedItem();
    skills[4] = (String) frame.skill5.getSelectedItem();
    skills[5] = (String) frame.skill6.getSelectedItem();
    skills[6] = (String) frame.skill7.getSelectedItem();
    skills[7] = (String) frame.skill8.getSelectedItem();
    Set<String> skillSet = new HashSet<String>();
    for(int i=0; i<8; i++) {
      if(skills[i] != null) {
        if(!allSkills.contains(skills[i])) {
          JOptionPane.showMessageDialog(frame, "The skill \"" + skills[i] + "\" does not exist");
          return ;
        }
        skillSet.add(skills[i]);
      }
    }

    //Find search attributes
    int searchDepth = frame.depthSlider.getValue();
    int skillThreshold = frame.thresholdSlider.getValue();
    int numChains = frame.chainSlider.getValue();;

    /* CHECK DATA */
    //Make sure that race and name have been entered
    if(race == null || name == null) {
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
      //Perform simple search
      List<Demon[]> recipes = Fusion.fuseWithoutSkillRequirements(desired);
      if(toFile) {
        //Output results to file
        try {
          FileOps.outputSimpleFusionResults(recipes, desired);
        }
        catch(Exception e) {
          JOptionPane.showMessageDialog(frame, "Error writing results to output file");
        }
      }
      else {
        //Print simple fusion results
        JDialog dialog = new JDialog(frame);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JTextArea output = new JTextArea();
        //Add all output to the text field
        StringBuilder sb = new StringBuilder();
        sb.append("Fusing: " + desired.getRace().toString().substring(0,1).toUpperCase()+desired.getRace().toString().substring(1)+" "+desired.getName()+"\n");
        sb.append("Skills: " + desired.getSkills().toString()+"\n\n");
        //Loop over all recipes
        for(Demon[] recipe : recipes) {
          sb.append(recipe[0].getRace().toString().substring(0,1).toUpperCase()+recipe[0].getRace().toString().substring(1)+" "+recipe[0].getName());
          for(int i=1; i<recipe.length; i++) {
            sb.append(" + " + recipe[i].getRace().toString().substring(0,1).toUpperCase()+recipe[i].getRace().toString().substring(1)+" "+recipe[i].getName());
          }
          sb.append("\n");
        }
        output.setText(sb.toString());

        //Add output text to the modeless dialog
        JScrollPane scroll = new JScrollPane(output);
        panel.add(scroll,BorderLayout.CENTER);
        dialog.getContentPane().add(panel);
        dialog.setVisible(true);
        dialog.pack();
      }
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
          JOptionPane.showMessageDialog(frame, "Error writing results to output file");
        }
      }
      else {
        //Print fusion chain to new modeless dialog
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Fusing: " + desired.getRace().toString().substring(0,1).toUpperCase()+desired.getRace().toString().substring(1)+" "+desired.getName()+"\n");
        sb.append("Skills: " + skillSet.toString()+"\n");
        sb.append("\n");
        for(FusionChain recipe : recipes) {
          sb.append("\n");
          sb.append(recipe.toString(0,skillSet));
          sb.append("\n");
        }
        String finalOut = sb.toString();

        //Create new modeless dialog, and print finalOut to scrolling resizable textarea
        //http://stackoverflow.com/questions/3843493/java-jtextarea-that-auto-resizes-and-scrolls
        //https://docs.oracle.com/javase/tutorial/uiswing/misc/modality.html
        JDialog dialog = new JDialog(frame);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JTextArea output = new JTextArea();
        output.setText(finalOut);
        JScrollPane scroll = new JScrollPane(output);
        panel.add(scroll, BorderLayout.CENTER);
        dialog.getContentPane().add(panel);
        dialog.setVisible(true);
        dialog.pack();
      }
    }
  }
}
