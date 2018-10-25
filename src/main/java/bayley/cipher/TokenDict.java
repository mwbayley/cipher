package bayley.cipher;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class TokenDict implements CipherDict {

  private HashMap<Object, List<String>> index;
  private int size;

  TokenDict() throws IOException {
    this("/usr/share/dict/words");
  }

  TokenDict(String dictPath) throws IOException {
    this.index = new HashMap<>();
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
    for (Map.Entry<Object, List<String>> results  : index.entrySet()) {
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
    List<String> resultList = index.get(key);
    if (resultList == null) {
      resultList = new LinkedList<>();
      index.put(key, resultList);
    }
    resultList.add(word);
  }

  public List<String> getAll (Cipher cipher, String scrambledWord) {
    return index.get(CipherDictKey(scrambledWord));
  }

  private static int CipherDictKey(String word) {
    if (word.length() == 0) {
      throw new RuntimeException("Can't tokenize a null or empty word");
    }
    byte[] tokenized = new byte[word.length()];
    HashMap<Character, Byte> charToByte = new HashMap<>();
    // number of unique characters encountered so far
    Byte nChars = 0;
    // tokenize the characters in the string
    for (int i = 0; i < word.length(); i++) {
      Character thisChar = Character.toLowerCase(word.charAt(i));
      // we will hard-code mappings to negative values for punctuation (since it isn't scrambled)
      // we reserve the token -1 for an apostrophe
      if (thisChar.equals('\'')) {
        tokenized[i] = -1;
        continue;
      }
      // we reserve the token -2 for a hyphen
      if (thisChar.equals('-')) {
        tokenized[i] = -2;
        continue;
      }
      Byte prevToken = charToByte.putIfAbsent(thisChar, nChars);
      tokenized[i] = (prevToken == null) ? nChars++ : prevToken;
    }
    // return a hashCode based on the array elements (and positions)
    return Arrays.hashCode(tokenized);
  }

  public String randomWord() {
    Collection<List<String>> dictLists = index.values();
    int num = (int) (Math.random() * dictLists.size());
    List<String> randomList = null;
    for (List<String> list: dictLists) {
      if (--num < 0) {
        randomList = list;
        break;
      }
    }
    if (randomList == null) {
      throw new RuntimeException("Can't find a random word");
    }
    num = (int) (Math.random() * randomList.size());
    for (String randomWord: randomList) {
      if (--num < 0) return randomWord;
    }
    throw new RuntimeException("Can't find a random word");
  }
}
