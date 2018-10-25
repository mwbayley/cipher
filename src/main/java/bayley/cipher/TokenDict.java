package bayley.cipher;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class TokenDict implements CipherDict {

  private final HashMap<Object, Set<String>> index;
  private int size;
  private final Set<Character> alphabet;
  // need LinkedHashSet to guarantee consistent iteration order over knownCharacters;
  private final LinkedHashSet<Character> knownCharacters;

  TokenDict() throws IOException {
    this("/usr/share/dict/words",
            CipherSolver.englishAlphabet,
            new LinkedHashSet<>(CipherSolver.englishKnownCharacters)
            );
  }

  TokenDict(String dictPath, Set<Character> alphabet, LinkedHashSet<Character> knownCharacters) throws IOException {
    if (!alphabet.containsAll(knownCharacters)) {
      throw new IllegalArgumentException("Alphabet must contain all knownCharacters");
    }
    this.alphabet = alphabet;
    this.knownCharacters = knownCharacters;
    this.index = new LinkedHashMap<>();
    // read in the dictionary to a hash map with keys based on a tokenized representation of the word
    try(BufferedReader br = new BufferedReader(new FileReader(dictPath))) {
      String word;
      while ((word = br.readLine()) != null) {
        this.add(CipherDictKey(word), word.toUpperCase());
        size++;
      }
    }
    System.out.println(stats());
  }

  public String stats() {
    int maxSize = 0;
    for (Map.Entry<Object, Set<String>> results  : index.entrySet()) {
      int size = results.getValue().size();
      if (size > maxSize) {
        maxSize = size;
      }
    }
    return String.format("Size = %d, maximum key matches = %d", this.size, maxSize);
  }

  public int size() {
    return size;
  }

  // get the list that matches the key, add the word to it
  private void add (Object key, String word) {
    Set<String> resultList = index.get(key);
    if (resultList == null) {
      resultList = new LinkedHashSet<>();
      index.put(key, resultList);
    }
    resultList.add(word);
  }

  public Set<String> potentialMatches (Cipher cipher, String scrambledWord) {
    return index.get(CipherDictKey(scrambledWord));
  }

  private int CipherDictKey(String word) {
    if (word.length() == 0) {
      throw new RuntimeException("Can't tokenize a null or empty word");
    }
    byte[] tokenized = new byte[word.length()];
    HashMap<Character, Byte> charToByte = new HashMap<>();
    // number of unique characters encountered so far
    Byte nChars = 0;
    // tokenize the characters in the string
    wordCharLoop: for (int i = 0; i < word.length(); i++) {
      Character thisChar = Character.toLowerCase(word.charAt(i));
      // we will use negative values for known Characters
      byte knownCharToken = 0;
      for (Character c : knownCharacters) {
        if (thisChar.equals(c)) {
          tokenized[i] = --knownCharToken;
          continue wordCharLoop;
        }
      }
      Byte prevToken = charToByte.putIfAbsent(thisChar, nChars);
      tokenized[i] = (prevToken == null) ? nChars++ : prevToken;
    }
    // return a hashCode based on the array elements (and positions)
    return Arrays.hashCode(tokenized);
  }

  public String randomWord() {
    Collection<Set<String>> dictSets = index.values();
    int num = (int) (Math.random() * dictSets.size());
    Set<String> randomSet = null;
    for (Set<String> list: dictSets) {
      if (--num < 0) {
        randomSet = list;
        break;
      }
    }
    if (randomSet == null) {
      throw new RuntimeException("Can't find a random word");
    }
    num = (int) (Math.random() * randomSet.size());
    for (String randomWord: randomSet) {
      if (--num < 0) return randomWord;
    }
    throw new RuntimeException("Can't find a random word");
  }
}
