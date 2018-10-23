package bayley.cipher;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class CipherSolver {

  public CipherDict dict;

  public static void main (String[] args) {
    try {
      CipherSolver cs = new CipherSolver("/usr/share/dict/american-english");
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    
  }

  public CipherSolver () throws IOException {
    // read in the dictionary and create data structures for lookup
    dict = new TokenDict();
    System.out.printf("Dictionary has %s words%n", dict.size());
  }

  /**
   * a CipherSolver uses a single dictionary to solve many ciphers
   * @param dictPath local path to newline delimited dictionary
   * @throws IOException if the dict doesn't exist or can't be read
   */
  public CipherSolver (String dictPath) throws IOException {
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
    String solvedWord = cipher.decode(scrambledWord);
    for (String matchingWord : dict.getAll(cipher, scrambledWord)) {
      // check wordChars against keyWord
      Cipher supercipher = cipher.match(scrambledWord, matchingWord);
      if (supercipher != null) {
        results.add(supercipher);
      }
    }
    return results;
  }

  public List<String> solve (final String scrambled) {
    String[] scrambledWords = scrambled.split(" ");
    List<Cipher> candidateCiphers = new LinkedList<>();
    // start with one candidate - an empty Cipher
    candidateCiphers.add(new Cipher());
    // this will be the list of candidates for the next iteration
    List<Cipher> newCandidateCiphers = new LinkedList<>();
    int wordCount = 1;
    for (String scrambledWord : scrambledWords) {
      System.out.println(wordCount);
      String newString = scrambledWord.toUpperCase().replaceAll("[^A-Z\']","");
      // if this word was only punctuation just skip it
      if (newString.equals("")) {
        continue;
      }
      // new candidate ciphers are superciphers of old candidates that also match the new word
      System.out.println(String.format("%d candidate ciphers", candidateCiphers.size()));
      for (Cipher cipher : candidateCiphers) {
        newCandidateCiphers.addAll(refine(cipher, scrambledWord));
      }
      candidateCiphers = newCandidateCiphers;
      wordCount++;
    }
    if (candidateCiphers.isEmpty()) {
      return null;
    }
    List<String> results = new LinkedList<>();
    for (Cipher solutionCipher : candidateCiphers) {
      String result = solutionCipher.decode(scrambled);
      results.add(result);
    }
    return results;

  }
}
