package bayley.cipher;

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

  TokenDict(String dictPath) throws IOException {
    this.clear();
    // read in the dictionary to a hash map with multiple keys
    try(BufferedReader br = new BufferedReader(new FileReader(dictPath))) {
      String word;
      while ((word = br.readLine()) != null) {
        this.add(CipherDictKey(word), word);
        size++;
      }
    }
    printStats();
  }

  public void printStats() {
    int maxSize = 0;
    for (Map.Entry<Object, List<String>> results  : index.entrySet()) {
      int size = results.getValue().size();
      if (size > maxSize) {
        maxSize = size;
      }
    }
    System.out.printf("Maximum key matches = %d%n", maxSize);
  }

  public int size() {
    return size;
  }

  // get the list that matches the key, add the word to it, and write it back
  private void add (Object key, String word) {
    List<String> resultList = index.get(key);
    if (resultList == null) {
      resultList = new LinkedList<>();
      index.put(key, resultList);
    }
    resultList.add(word);
  }

  // should this be an iterator instead?
  public List<String> getAll (Cipher cipher, String scrambledWord) {
    return index.get(CipherDictKey(scrambledWord));
  }

  private void clear() {
    this.index = new HashMap<>();
  }

  public static Object CipherDictKey(String word) {
    if (word.length() == 0) {
      throw new RuntimeException("Can't tokenize a null or empty word");
    }
    byte[] tokenized = new byte[word.length()];
    HashMap<Character, Byte> charToByte = new HashMap<>();
    // number of unique characters encountered so far
    Byte nChars = 0;
    // tokenize the characters in the string
    for (int i = 0; i < word.length(); i++) {
      Byte prevToken = charToByte.putIfAbsent(word.charAt(i), nChars);
      tokenized[i] = (prevToken == null) ? nChars++ : prevToken;
    }
    // return a hashCode based on the array elements (and positions)
    return Arrays.hashCode(tokenized);
  }

}
