package bayley.cipher;

import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class TokenDict implements CipherDict {

  private final Map<Integer, Set<String>> index;
  private int size = 0;
  private final Set<Character> alphabet;
  // each knownCharacter will have an associated negative value used to tokenize it;
  private final Map<Character, Byte> knownCharacters;

  TokenDict(Set<Character> alphabet, Set<Character> knownCharacters) throws IOException {
    if (!alphabet.containsAll(knownCharacters)) {
      throw new IllegalArgumentException("Alphabet must contain all knownCharacters");
    }
    this.alphabet = alphabet;
    this.knownCharacters = new HashMap<>();
    // each known char will be assigned a unique negative token
    byte knownCharToken = -1;
    for (Character c : knownCharacters) {
      this.knownCharacters.put(c, knownCharToken--);
    }
    this.index = new HashMap<>();
    // read in the dictionary to a hash map with keys
    // based on a tokenized representation of the word
    BufferedReader dictReader = new BufferedReader(
            new InputStreamReader(
                    getClass().getResourceAsStream(
                            "/dictionaries/american-english.txt")));
    String word;
    wordLoop: while ((word = dictReader.readLine()) != null) {
      word = word.toUpperCase();
      // exclude words with non-alphabet characters
      for (Character c : word.toCharArray()) {
        if (!alphabet.contains(c)) {
          // skip this word
          continue wordLoop;
        }
      }
      this.add(word);
      size++;
    }
  }

  // get the set that matches the key, add the word to it
  private void add (String word) {
    Integer key = cipherDictKey(word);
    Set<String> matchingSet = index.get(key);
    // if there are no results yet for this key we add a new set to hold them
    if (matchingSet == null) {
      matchingSet = new HashSet<>();
      index.put(key, matchingSet);
    }
    matchingSet.add(word);
  }

  @Override
  public String toString() {
    int maxSize = 0;
    for (Map.Entry<Integer, Set<String>> results  : index.entrySet()) {
      int size = results.getValue().size();
      if (size > maxSize) {
        maxSize = size;
      }
    }
    return String.format(
            "%s has %d words, unique keys = %d, maximum key matches = %d",
            super.toString(),
            this.size,
            index.size(),
            maxSize
    );
  }

  @Override
  public int nSimilarWords (String scrambledWord) {
    return index.get(cipherDictKey(scrambledWord)).size();
  }

  @Override
  public Set<String> potentialMatches (Cipher cipher, String scrambledWord) {
    return index.get(cipherDictKey(scrambledWord));
  }

  /**
   * The secret to this dictionary is the way that we partition it using a tokenized representation of the word.
   * We'll use a byte array of the same length as the word where the value in each position is the ordering
   * in which we first observed that character in the word. For example:
   *    original    scrambled     key
   *    cat     ->    wer    ->   [0][1][2]
   *    cat     ->    jui    ->   [0][1][2]
   *    all     ->    rtt    ->   [0][1][1]
   *    ally    ->    wllk   ->   [0][1][1][2]
   *    bob     ->    jxj    ->   [0][1][0]
   * The exception is for characters like ' and - that occur within words but don't get scrambled.
   * We assign them negative values instead:
   *    won't   ->   jkl'g   ->   [0][1][2][-1][3]
   * We can't use this byte array as the key directly because byte[] uses object identity for hashCode and equals.
   * Instead we'll generate the key explicitly with Arrays.hashCode() and store it in an Integer.
   */
  private Integer cipherDictKey(String scrambledWord) {
    if (scrambledWord == null || scrambledWord.length() == 0) {
      throw new IllegalArgumentException("Can't tokenize a null or empty word");
    }
    byte[] tokenized = new byte[scrambledWord.length()];
    Map<Character, Byte> charToByte = new HashMap<>();
    // number of unique characters encountered so far
    byte nChars = 0;
    // tokenize the characters in the string
    for (int i = 0; i < scrambledWord.length(); i++) {
      Character c = scrambledWord.charAt(i);
      // all characters must be in our alphabet;
      if (!alphabet.contains(c)) {
        throw new IllegalArgumentException(
                String.format("Word %s has non-alphabet character %c", scrambledWord, c)
        );
      }
      // we will use negative values for known characters like ' and -
      Byte knownCharToken = knownCharacters.get(c);
      if (knownCharToken != null) {
        tokenized[i] = knownCharToken;
      }
      // other alphabet characters are represented by positive tokens
      else {
        Byte prevToken = charToByte.putIfAbsent(c, nChars);
        // if we have seen this character already in this word we give it the same token
        if (prevToken != null) {
          tokenized[i] = prevToken;
        }
        // if this is a new character we give it the next token
        else {
          tokenized[i] = nChars++;
        }
      }
    }
    // explicitly create the hashCode based on the array elements and positions
    return Arrays.hashCode(tokenized);
  }

  /**
   * This implementation picks a random hashCode and then a random word with that hashCode. As a result, it
   * disproportionately picks "unique-looking" words.
   */
  @Override
  public String randomWord() {
    Collection<Set<String>> dictSubsets = index.values();
    int num = (int) (Math.random() * dictSubsets.size());
    Collection<String> randomSubset = null;
    for (Collection<String> subset : dictSubsets) {
      if (--num < 0) {
        randomSubset = subset;
        break;
      }
    }
    if (randomSubset == null) {
      throw new RuntimeException("Couldn't find a random word");
    }
    num = (int) (Math.random() * randomSubset.size());
    for (String randomWord: randomSubset) {
      if (--num < 0) return randomWord;
    }
    throw new RuntimeException("Couldn't find a random word");
  }
}
