import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public enum Element {
  Erthys("erthys"),
  Aeros("aeros"),
  Aquans("aquans"),
  Flaemis("flaemis"),
  Gnome("gnome"),
  Sylph("sylph"),
  Undine("undine"),
  Salamander("salamander");

  //Contains list of races that can produce this element
  private List<Race> races;

  //Static variables
  private static Map<String, Element> stringToEnum = new HashMap<String, Element>();

  static {
    //Set up string to enum map
    for(Element e : values()) {
      stringToEnum.put(e.toString(), e);
    }
  }

  Element(String element) {
    races = new ArrayList<Race>();
    FileOps.populateElementFusionRules(element, races);
  }

  /**
    * Given a string representation of an element, return the enum
    * @param element the element desiredRace
    * @return the element enum. Null if no corresponding element enum could be found for provided string
    */
  public static Element fromString(String element) {
    return stringToEnum.get(element);
  }

  public List<Race> getRaceFusionList() {
    return this.races;
  }
}
