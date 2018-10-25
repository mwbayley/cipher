package bayley.cipher;

import java.util.List;

public interface CipherDict {

  /**
   * Print info about the dictionary to stdout
   */
  String stats();

  /**
   * @return the number of words in the dictionary
   */
  int size();

  /** should this be an iterator?
   * here we guarantee that all potential matches will be retrieved
   * NO GUARANTEE that all of these match the cipher
   * should be an efficient lookup here
   */
  List<String> getAll(Cipher cipher, String scrambledWord);

  /**
   * Get a random word from the dictionary
   * Will be used for testing only - no cryptographic robustness required
   *
   * @return a pseudorandom word from the dictionary
   */
  String randomWord();

  default String randomSentence(int length) {
    Cipher c = new Cipher();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      builder.append(randomWord());
      builder.append(' ');
    }
    builder.delete(builder.length()-1, builder.length());
    builder.append('.');
    return builder.toString();
  }

}
