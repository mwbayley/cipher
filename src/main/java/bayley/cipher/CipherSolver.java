package bayley.cipher;

import java.io.IOException;
import java.util.Set;
import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableSet;

public class CipherSolver {

  protected final CipherDict dict;
  protected final Set<Character> alphabet;
  protected final Set<Character> knownCharacters;


  // TODO move these to a config or properties file
  protected static Set<Character> englishAlphabet = ImmutableSet.of(
          'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
          'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
          'U', 'V', 'W', 'X', 'Y', 'Z', '\'', '-'
  );
  protected static Set<Character> englishKnownCharacters = ImmutableSet.of('\'', '-');

  public static void main (String[] args) {
    try {
      CipherSolver cs = new CipherSolver();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    
  }

  CipherSolver () throws IOException {
    this("/usr/share/dict/american-english", englishAlphabet, new LinkedHashSet<>(englishKnownCharacters));
  }

  /**
   * a CipherSolver uses a single dictionary to solve many ciphers
   *
   * @param dictPath local path to newline delimited dictionary
   * @throws IOException if the dict doesn't exist or can't be read
   */
  CipherSolver (String dictPath, Set<Character> alphabet, LinkedHashSet<Character> knownCharacters) throws IOException {
    // read in the dictionary and create data structures for lookup
    dict = new TokenDict(dictPath, alphabet, knownCharacters);
    this.alphabet = alphabet;
    this.knownCharacters = knownCharacters;
    System.out.println(dict.stats());
  }

  /**
   * Refine
   *
   * returns: a list of superciphers generated by matching word against dictionary
   */
  protected Set<Cipher> refine (final Cipher cipher, String scrambledWord) {
    Set<Cipher> results = new LinkedHashSet<>();
    for (String potentialMatch : dict.potentialMatches(cipher, scrambledWord)) {
      // get the Cipher implied by a match between the scrambled word and the dictionary word
      Cipher supercipher = cipher.match(scrambledWord, potentialMatch);
      if (supercipher != null) {
        results.add(supercipher);
      }
    }
    return results;
  }

  public Set<String> solve (final String scrambled) {
    String[] scrambledWords = scrambled.split(" ");
    Set<Cipher> candidateCiphers = new LinkedHashSet<>();
    // start with one candidate - an empty Cipher
    candidateCiphers.add(new Cipher(alphabet, knownCharacters));
    for (String scrambledWord : scrambledWords) {
      // TODO change this to remove non-alphabet characters
      // TODO CipherSet has a shared dictionary?
      /*
      StringBuilder builder = new StringBuilder();
      for (Character c : scrambledWord.toUpperCase().toCharArray()) {
        if ()

      }*/
      // this will be the set of candidates for the next iteration
      Set<Cipher> newCandidateCiphers = new LinkedHashSet<>();
      String strippedWord = scrambledWord.toUpperCase().replaceAll("[^A-Z\'-]","");
      // if this word was only punctuation just skip it
      if (strippedWord.equals("")) {
        continue;
      }
      // new candidate ciphers are superciphers of old candidates that also match the new word
      for (Cipher cipher : candidateCiphers) {
        if (cipher == null) {
          throw new RuntimeException("Got a null cypher somehow");
        }
        newCandidateCiphers.addAll(refine(cipher, strippedWord));
      }
      candidateCiphers = newCandidateCiphers;
    }
    if (candidateCiphers.isEmpty()) {
      return null;
    }
    Set<String> results = new LinkedHashSet<>();
    for (Cipher solutionCipher : candidateCiphers) {
      String result = solutionCipher.decode(scrambled);
      results.add(result);
    }
    return results;

  }
}
