package bayley.cipher;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Set;

public class CipherSolver {

  public CipherDict dict;

  public static void main (String[] args) {
    try {
      CipherSolver cs = new CipherSolver("/usr/share/dict/american-english");
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    
  }

  CipherSolver () throws IOException {
    this("/usr/share/dict/american-english");
  }

  /**
   * a CipherSolver uses a single dictionary to solve many ciphers
   *
   * @param dictPath local path to newline delimited dictionary
   * @throws IOException if the dict doesn't exist or can't be read
   */
  CipherSolver (String dictPath) throws IOException {
    // read in the dictionary and create data structures for lookup
    dict = new TokenDict(dictPath);
    System.out.printf("Dictionary has %s words%n", dict.size());
  }

  /**
   * Refine
   *
   * returns: a list of superciphers generated by matching word against dictionary
   */
  protected Set<Cipher> refine (Cipher cipher, String scrambledWord) {
    Set<Cipher> results = new HashSet<>();
    for (String matchingWord : dict.getAll(cipher, scrambledWord)) {
      // check wordChars against keyWord
      Cipher supercipher = cipher.match(scrambledWord, matchingWord);
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
    candidateCiphers.add(new Cipher());
    // this will be the set of candidates for the next iteration
    Set<Cipher> newCandidateCiphers = new LinkedHashSet<>();
    int wordCount = 1;
    for (String scrambledWord : scrambledWords) {
      System.out.println(wordCount);
      // TODO change this to remove non-alphabet characters
      // TODO CipherSet has a shared dictionary?
      /*
      StringBuilder builder = new StringBuilder();
      for (Character c : scrambledWord.toUpperCase().toCharArray()) {
        if ()

      }*/
      String strippedWord = scrambledWord.toUpperCase().replaceAll("[^A-Z\'-]","");
      // if this word was only punctuation just skip it
      if (strippedWord.equals("")) {
        continue;
      }
      System.out.println(String.format("new word: %s", strippedWord));
      // new candidate ciphers are superciphers of old candidates that also match the new word
      System.out.println(String.format("%d candidate ciphers", candidateCiphers.size()));
      for (Cipher cipher : candidateCiphers) {
        if (cipher == null) {
          throw new RuntimeException("Got a null cypher somehow");
        }
        System.out.println(cipher);
        newCandidateCiphers.addAll(refine(cipher, strippedWord));
      }
      candidateCiphers = newCandidateCiphers;
      wordCount++;
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
