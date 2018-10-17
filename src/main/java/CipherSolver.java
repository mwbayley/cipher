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
   * returns a supercipher generated by matching word
   */
  private Cipher refine (Cipher cipher, final String dictWord, String scrambledWord) {
    List<Cipher> results = new LinkedList<>();
    Cipher result = new Cipher();
    Character[] solvedWord = cipher.apply(scrambledWord);
    Object key = TokenDict.CipherDictKey(solvedWord);
    for (Character[] matchingWord : dict.getAll(key)) {
      // check wordChars against keyWord

      for (int i=0; i<matchingWord.length; i++) {
        cipher.replace(matchingWord[i], );
      }
    }
    return results;
  }

  public List<String[]> solve (final String scrambled) {
    String[] scrambledWords = scrambled.split(" ");
    List<Cipher> candidateCiphers = new ArrayList<>();
    // start with one candidate - an empty Cipher;
    candidateCiphers.add(new Cipher());
    // this will be the list of candidates for the next iteration
    List<Cipher> newCandidateCiphers = new ArrayList<>();
    if (scrambledWords.length==0) return null;
    for (String word : scrambledWords) {
      for (Cipher cipher : candidateCiphers) {
        newCandidateCiphers.addAll(refine(cipher, word));
      }
      candidateCiphers = newCandidateCiphers;
    }
    if (solutions.isEmpty()) {
      return null;
    }
    List<String> results = new LinkedList<>();
    for (Cipher solution : solutions) {
      results.add()
    }


  }
*/
}
