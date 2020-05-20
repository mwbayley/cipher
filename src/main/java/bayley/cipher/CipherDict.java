package bayley.cipher;

import java.util.Set;

/**
 * A CipherDict holds a dictionary in memory and permits lookup of potential matches
 * of scrambled words. A valid minimal implementation is to just return the whole dictionary.
 * You could also return only words of the same length, or subset the results another way.
 * There is NO guarantee that all results returned match the supplied cipher.
 * The goal here is on an efficient lookup of potential matches.
 */
public interface CipherDict {

  /**
   * How many matches would we get if we didn't know any mappings yet?
   * We'll use this to determine what order to examine words in
   */
  int nSimilarWords (String scrambledWord);

  /**
   * Here we guarantee that all potential matches will be retrieved
   * NO GUARANTEE that all of these match the cipher
   * should be an efficient lookup here
   */
  Set<String> potentialMatches(Cipher cipher, String scrambledWord);

  /**
   * Get a "random" word from the dictionary
   * Will be used for testing only - no mathematical robustness required
   *
   * @return a (not really) random word from the dictionary
   */
  String randomWord();

  default String randomSentence(int length) {
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
