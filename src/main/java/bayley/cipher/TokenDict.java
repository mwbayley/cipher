package bayley.cipher;

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;


public class TokenDict implements CipherDict {

  private final HashMap<Object, LinkedHashSet<String>> index;
  private int size = 0;
  private final Set<Character> alphabet;
  // each knownCharacter will have an associated negative value used to tokenize it;
  private final HashMap<Character, Byte> knownCharacters;

  TokenDict(Set<Character> alphabet, Set<Character> knownCharacters)
          throws IOException {
    if (!alphabet.containsAll(knownCharacters)) {
      throw new IllegalArgumentException("Alphabet must contain all knownCharacters");
    }
    this.alphabet = alphabet;
    this.knownCharacters = new LinkedHashMap<>();
    // each known char will be assigned a unique negative token
    byte knownCharToken = -1;
    for (Character c : knownCharacters) {
      this.knownCharacters.put(c, knownCharToken--);
    }
    this.index = new LinkedHashMap<>();
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
      this.add(cipherDictKey(word), word);
      size++;
    }

  }

  @Override
  public String toString() {
    int maxSize = 0;
    for (Map.Entry<Object, LinkedHashSet<String>> results  : index.entrySet()) {
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

  // get the list that matches the key, add the word to it
  private void add (Object key, String word) {
    LinkedHashSet<String> resultSet = index.get(key);
    // if there are no results yet for this key we add a new set to hold them
    if (resultSet == null) {
      resultSet = new LinkedHashSet<>();
      index.put(key, resultSet);
    }
    resultSet.add(word);
  }

  @Override
  public Set<String> potentialMatches (Cipher cipher, String scrambledWord) {
    return index.get(cipherDictKey(scrambledWord));
  }

  private int cipherDictKey(String word) {
    if (word.length() == 0) {
      throw new RuntimeException("Can't tokenize a null or empty word");
    }
    byte[] tokenized = new byte[word.length()];
    HashMap<Character, Byte> charToByte = new HashMap<>();
    // number of unique characters encountered so far
    byte nChars = 0;
    // tokenize the characters in the string
    for (int i = 0; i < word.length(); i++) {
      Character c = word.charAt(i);
      // all characters must be in our alphabet;
      if (!alphabet.contains(c)) {
        throw new IllegalArgumentException(
                String.format("Word %s has non-alphabet character %c", word, c)
        );
      }
      // we will use negative values for known characters
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
          tokenized[i] = ++nChars;
        }
      }
    }
    // return a hashCode based on the array elements (and positions)
    return Arrays.hashCode(tokenized);
  }

  @Override
  public String randomWord() {
    Collection<LinkedHashSet<String>> dictSets = index.values();
    int num = (int) (Math.random() * dictSets.size());
    Set<String> randomSet = null;
    for (Set<String> list: dictSets) {
      if (--num < 0) {
        randomSet = list;
        break;
      }
    }
    if (randomSet == null) {
      throw new RuntimeException("Couldn't find a random word");
    }
    num = (int) (Math.random() * randomSet.size());
    for (String randomWord: randomSet) {
      if (--num < 0) return randomWord;
    }
    throw new RuntimeException("Couldn't find a random word");
  }
}
