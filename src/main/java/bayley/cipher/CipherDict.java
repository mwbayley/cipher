package bayley.cipher;

import java.util.List;

public interface CipherDict {

  /**
   * Print info about the dictionary to stdout
   */
  void printStats();

  /**
   * @return the number of words in the dictionary
   */
  int size();

  // should this be an iterator?
  // here we guarantee that all potential matches will be retrieved
  // NO GUARANTEE that all of these match the cipher
  // should be an efficient lookup here
  List<String> getAll(Cipher cipher, String scrambledWord);


}
