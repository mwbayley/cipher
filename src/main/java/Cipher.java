import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class Cipher {

  private Map<Character, Character> map;
  private Set<Character> mappedTo;

  public enum cipherType {
    EMPTY,
    RANDOM,
    PARTIAL
  }

  public Cipher() {
    this(cipherType.EMPTY);
  }

  public Cipher(Cipher cipher) {
    this.map = new HashMap<>(cipher.map);
    this.mappedTo = new HashSet<>(cipher.mappedTo);
  }

  public Cipher(cipherType type) {
    map = new HashMap<>();
    mappedTo = new HashSet<>();
    if (type == cipherType.EMPTY) {
      return;
    }
    if (type == cipherType.RANDOM) {
      // TODO
      throw new UnsupportedOperationException("Not yet implemented");
    }
    if (type == cipherType.PARTIAL) {
      // TODO
      throw new UnsupportedOperationException("Not yet implemented");
    }
    throw new UnsupportedOperationException();
  }

  public void add(Character from, Character to) {
    if (map.put(from, to).equals(null)) {
      throw new RuntimeException("Character is already mapped in the cipher");
    }
  }

  public void replace(Character from, Character to) {
    if (!map.containsKey(from)) {
      throw new RuntimeException("From Character isn't mapped yet");
    }
    if (mappedTo.contains(to)) {
      throw new RuntimeException("To Character is already used");
    }
    mappedTo.remove(map.get(from));
    map.put(from, to);
    mappedTo.add(to);
  }

  public String apply (String word) {
    StringBuilder builder = new StringBuilder();
    for (char c : word.toCharArray()) {
      builder.append(map.get(c));
    }
    return builder.toString();
  }

  public String apply (String[] words) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < words.length; i++) {
      if (i > 0) {
        builder.append(" ");
      }
      builder.append(apply(words[i]));
    }
    return builder.toString();
  }
*/
  /**
   * return: the cipher implied if the dictionary match is the encoded word
   * or null if no match
   */
  public Cipher match(String scrambledWord, String dictWord) {
    if (scrambledWord.length() != dictWord.length()) {
      return null;
    }
    Cipher newCipher = new Cipher(this);
    for (int i = 0; i < scrambledWord.length(); i++) {
      Character from = scrambledWord.charAt(i);
      Character to = dictWord.charAt(i);
      Character previousTo = map.get(from);
      // if the character was already mapped and not mapped to the same char;
      if (previousTo != null && !previousTo.equals(to)) {
        return null;
      }
      newCipher.add(from, to);
    }
    return newCipher;
  }

}
