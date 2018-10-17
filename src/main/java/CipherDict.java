import java.util.List;

public interface CipherDict<LookupType> {

  /**
   * Print info about the dictionary to stdout
   */
  void printStats();

  /**
   * @return the number of words in the dictionary
   */
  int size();

  // should this be an iterator?
  List<Character[]> getAll(LookupType key);


}
