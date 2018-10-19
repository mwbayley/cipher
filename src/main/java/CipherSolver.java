import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class CipherSolver {

  private CipherDict dict;

  public static void main (String[] args) {
    try {
      CipherSolver cs = new CipherSolver("/usr/share/dict/american-english");
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    
  }

  // a CipherSolver uses a single dictionary to solve many ciphers
  private CipherSolver (String dictPath) throws IOException {

    // read in the dictionary and create data structures for lookup
    dict = new TokenDict(dictPath);
    System.out.printf("Dictionary has %s words%n", dict.size());

  }

  /**
   * Refine
   *
   * returns: a list of superciphers generated by matching word against dictionary
   */
  private List<Cipher> refine (Cipher cipher, String scrambledWord) {
    List<Cipher> results = new LinkedList<>();
    String solvedWord = cipher.apply(scrambledWord);
    Object key = TokenDict.CipherDictKey(solvedWord);
    for (String matchingWord : dict.getAll(cipher, scrambledWord)) {
      // check wordChars against keyWord
      Cipher supercipher = cipher.match(scrambledWord, matchingWord);
      results.add(supercipher);
    }
    return results;
  }

  public List<String> solve (final String scrambled) {
    String[] scrambledWords = scrambled.split(" ");
    List<Cipher> candidateCiphers = new ArrayList<>();
    // start with one candidate - an empty Cipher;
    candidateCiphers.add(new Cipher());
    // this will be the list of candidates for the next iteration
    List<Cipher> newCandidateCiphers = new ArrayList<>();
    if (scrambledWords.length == 0) return null;
    for (String word : scrambledWords) {
      for (Cipher cipher : candidateCiphers) {
        newCandidateCiphers.addAll(refine(cipher, word));
      }
      candidateCiphers = newCandidateCiphers;
    }
    if (candidateCiphers.isEmpty()) {
      return null;
    }
    List<String> results = new LinkedList<>();
    for (Cipher solutionCipher : candidateCiphers) {
      String result = solutionCipher.solve(scrambledWords);
      results.add(result);
    }
    return results;

  }
}
