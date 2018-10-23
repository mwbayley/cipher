package bayley.cipher;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

public class Cipher {

  /** We use Guava's HashBiMap so that we can both encode and decode ciphers efficiently
   *  We also don't use the BiMap interface because other implementations don't guarantee iteration order
   *  That would mean a bunch more sorting in each of our tests. Relying on HashBiMap avoids this.
   */
  private HashBiMap<Character, Character> map;
  private Set<Character> alphabet;

  private static Set<Character> englishAlphabet = ImmutableSet.of(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z', '\'', '-'
      );

  private static Set<Character> englishKnownCharacters = ImmutableSet.of('\'', '-');

  /**
   * Constructor for default English alphabet
   */
  Cipher() {
    this(englishAlphabet, englishKnownCharacters
    );
  }

  /**
   * Use this constructor if you're using a non-English alphabet
   * @param alphabet Set of characters to be mapped by the Cipher
   * @param knownCharacters Set of characters within words that aren't scrambled (like apostrophe and hyphen in English)
   */
  public Cipher(Set<Character> alphabet, Set<Character> knownCharacters) {
    this.alphabet = new HashSet<>(alphabet);
    this.alphabet.addAll(knownCharacters);
    map = HashBiMap.create(this.alphabet.size());
    // prepopulate identity mappings for intra-word punctuation or other non-scrambled characters
    for (Character c : knownCharacters) {
      map.put(c, c);
    }
  }

  /**
   * Use this constructor to clone an existing Cipher (keys and values copied by reference)
    */
  public Cipher(Cipher cipher) {
    map = HashBiMap.create(cipher.map);
    alphabet = cipher.alphabet;
  }

  /**
   * Add a mapping from one character to another to the cipher
   */
  void add(Character from, Character to) {
    if (!alphabet.contains(from)) {
      throw new IllegalArgumentException(String.format("Character %c isn't in our alphabet", from));
    }
    if (!alphabet.contains(to)) {
      throw new IllegalArgumentException(String.format("Character %c isn't in our alphabet", to));
    }
    boolean conflictingMappingFrom = false;
    try {
      Character prevVal = map.put(from, to);
      if (prevVal != null && prevVal != to) {
        conflictingMappingFrom = true;
      }
    }
    // This exception occurs when the value is already present in the HashBiMap
    // we will catch it and throw our own so the message is clearer in this context
    catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(String.format("Character %c is already mapped to in the cipher", to));
    }
    if (conflictingMappingFrom) {
      throw new IllegalArgumentException(String.format("Character %c is already mapped from in the cipher", from));
    }
  }

  /**
   * Create a new English language Cipher with a random completely populated mapping
   * @return the new Cipher
   */
  public static Cipher randomCipher() {
    return randomCipher(englishAlphabet, englishKnownCharacters);
  }

  /**
   * Create a new Cipher with a random completely populated mapping
   * @return the new Cipher
   */
  public static Cipher randomCipher(Set<Character> alphabet, Set<Character> knownCharacters) {
    Cipher c = new Cipher(alphabet, knownCharacters);
    LinkedList<Character> mapFromChars = new LinkedList<>(c.alphabet);
    mapFromChars.removeAll(knownCharacters);
    LinkedList<Character> mapToChars = new LinkedList<>(mapFromChars);
    Collections.shuffle(mapToChars);
    while (!mapFromChars.isEmpty() && !mapToChars.isEmpty()) {
      c.add(mapFromChars.pop(), mapToChars.pop());
    }
    return c;
  }

  public String decode (String scrambled) {
    return apply(scrambled, false);
  }

  public String encode (String unscrambled) {
    return apply(unscrambled, true);
  }

  public String apply (String scrambled, final boolean inverse) {
    StringBuilder builder = new StringBuilder();
    for (char fromChar : scrambled.toUpperCase().toCharArray()) {
      Character toChar;
      if (alphabet.contains(fromChar)) {
        if (inverse) {
          toChar = map.inverse().get(fromChar);
        } else {
          toChar = map.get(fromChar);
        }
        if (toChar == null) {
          toChar = '?';
        }
      }
      else {
        toChar = fromChar;
      }
      builder.append(toChar);
    }
    return builder.toString();
  }

  /**
   * return: the supercipher implied if the dictionary word is the encoded word
   * or null if the dictionary word doesn't match the scrambled word with the current cipher
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
      if ( (previousTo != null && !previousTo.equals(to)) || map.containsValue(to)) {
        return null;
      }
      System.out.println(String.format("Mapping %c to %c", from, to));
      newCipher.add(from, to);
    }
    return newCipher;
  }

  @Override
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

  /**
   * @return whether this is a supercipher of another cipher
   */
  public boolean isSuperCipher(Cipher otherCipher) {
    if (!alphabet.equals(otherCipher.alphabet)) {
      return false;
    }
    return map.entrySet().containsAll(otherCipher.map.entrySet());
  }
}
