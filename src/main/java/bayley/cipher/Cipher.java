package bayley.cipher;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Cipher {

  private Map<Character, Character> map;

  private final Set<Character> alphabet = ImmutableSet.of(
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
    'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
    's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\''
  );



  Cipher() {
    map = new HashMap<>();
    // prepopulate identity mapping for apostrophe;
    map.put('\'', '\'');
  }

  Cipher(Cipher cipher) {
    map = new HashMap<>(cipher.map);
  }

  void add(Character from, Character to) {
    if (!alphabet.contains(from)) {
      throw new RuntimeException(String.format("Character %c isn't in our alphabet", from));
    }
    if (!alphabet.contains(to)) {
      throw new RuntimeException(String.format("Character %c isn't in our alphabet", to));
    }
    if (map.containsValue(to)) {
      throw new RuntimeException(String.format("Character %c is already mapped to in the cipher", to));
    }
    if (map.put(from, to) != null) {
      throw new RuntimeException(String.format("Character %c is already mapped from in the cipher", from));
    }
  }

  public String apply (String word) {
    StringBuilder builder = new StringBuilder();
    for (char fromChar : word.toCharArray()) {
      Character toChar = map.get(fromChar);
      if (toChar == null) {
        toChar = '?';
      }
      builder.append(toChar);
    }
    return builder.toString();
  }

  public String solve (String[] words) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < words.length; i++) {
      if (i > 0) {
        builder.append(" ");
      }
      builder.append(apply(words[i]));
    }
    return builder.toString();
  }

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

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<Character, Character> entry : map.entrySet()) {
      Character from = entry.getKey();
      Character to = entry.getValue();
      builder.append(String.format("%c:%c, ", from, to));
    }
    // remove the extra comma and space at the end
    if (map.size() > 0) {
      builder.delete(builder.length() - 2, builder.length());
    }
    return builder.toString();
  }
}
