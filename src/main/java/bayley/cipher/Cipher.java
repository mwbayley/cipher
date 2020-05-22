package bayley.cipher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.HashBiMap;

public class Cipher {

  /** We use Guava's HashBiMap so that we can both encode and decode ciphers efficiently.
   *  We also don't use the BiMap interface because other implementations don't guarantee
   *  iteration order. That would mean a bunch more sorting in many of our tests.
   *  Relying on HashBiMap directly avoids this.
   */
  protected final HashBiMap<Character, Character> map;
  private final Set<Character> alphabet;

  /**
   * Create a new empty Cipher and map knownCharacters to themselves
   * @param alphabet Set of characters to be mapped by the Cipher
   * @param knownCharacters Set of characters within words that aren't scrambled
   *                        (like apostrophe - and hyphen ' in English)
   */
  Cipher(final Set<Character> alphabet, final Set<Character> knownCharacters) {
    this.alphabet = alphabet;
    if (!alphabet.containsAll(knownCharacters)) {
      throw new IllegalArgumentException("Alphabet must contain all knownCharacters");
    }
    map = HashBiMap.create(this.alphabet.size());
    // prepopulate identity mappings for intra-word punctuation or other non-scrambled characters
    for (Character c : knownCharacters) {
      map.put(c, c);
    }
  }

  /**
   * Use this constructor to clone an existing Cipher
   * (duplicate the map but copy keys and values by reference)
    */
  Cipher(Cipher cipher) {
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
    // This exception occurs when the "to" value is already present in the HashBiMap.
    // We will catch it and throw our own so the message is clearer in this context.
    catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
              String.format("Character %c is already mapped to in the cipher", to)
      );
    }
    if (conflictingMappingFrom) {
      throw new IllegalArgumentException(
              String.format("Character %c is already mapped from in the cipher", from)
      );
    }
  }

  /**
   * Static factory method for a Cipher with a random completely populated mapping (for testing)
   * @param alphabet  Set of Characters to be mapped randomly
   * @param knownCharacters Characters in alphabet that we must map to themselves
   * @return the new Cipher
   */
  public static Cipher randomCipher(final Set<Character> alphabet,
                                    final Set<Character> knownCharacters) {
    Cipher c = new Cipher(alphabet, knownCharacters);
    LinkedList<Character> mapFromChars = new LinkedList<>(c.alphabet);
    mapFromChars.removeAll(knownCharacters);
    List<Character> mapToChars = new ArrayList<>(mapFromChars);
    Collections.shuffle(mapToChars);
    mapToChars.forEach(to -> c.add(mapFromChars.pop(), to));
    return c;
  }

  public String decode (String scrambled) {
    return apply(scrambled, false);
  }

  public String encode (String unscrambled) {
    return apply(unscrambled, true);
  }

  private String apply (String scrambled, boolean inverse) {
    Map<Character, Character> map = inverse ? this.map.inverse() : this.map;
    StringBuilder builder = new StringBuilder();
    for (char fromChar : scrambled.toUpperCase().toCharArray()) {
      Character toChar;
      if (alphabet.contains(fromChar)) {
        toChar = map.get(fromChar);
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
   * This method compares the dictionary word against the scrambled word using the mappings currently in the cipher.
   * If it is a possible match, we create a new cipher that is a clone of the current one with additional mappings
   * from the characters of the dictionary one to the scrambled one. We call this new cipher a "supercipher"
   * (meaning it has a superset of the mappings).
   *
   * @return: the supercipher implied if dictionary word maps to the scrambled word or null if the dictionary word
   * doesn't match.
   */
  public Cipher match(final String scrambledWord, final String dictWord) {
    if (scrambledWord.length() != dictWord.length()) {
      return null;
    }
    Cipher c = new Cipher(this);
    for (int i = 0; i < scrambledWord.length(); i++) {
      Character from = scrambledWord.charAt(i);
      Character to = dictWord.charAt(i);
      // is there a conflicting map from this character already?
      if (c.map.containsKey(from) && c.map.get(from) != to) {
        // no match
        return null;
      }
      // is there a conflicting map to this character already?
      if (c.map.inverse().containsKey(to) && c.map.inverse().get(to) != from) {
        // no match
        return null;
      }
      c.add(from, to);
    }
    return c;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    return String.format("%s: %s", super.toString(), map);
  }

  /**
   * @return whether this is a supercipher of another cipher (i.e. superset of the mappings w/ same alphabet)
   */
  public boolean isSuperCipher(Cipher otherCipher) {
    if (!alphabet.equals(otherCipher.alphabet)) {
      return false;
    }
    return map.entrySet().containsAll(otherCipher.map.entrySet());
  }

  @Override
  public boolean equals(Object o) {
    // if the Cipher is compared with itself then return true
    if (o == this) {
      return true;
    }
    // check if o is an instance of Cipher or not ("null instanceof [type]" also returns false)
    if (!(o instanceof Cipher)) {
      return false;
    }
    Cipher c = (Cipher) o;
    // typically we create many ciphers referencing the same alphabet
    // so alphabet.equals() should usually just compare the references and return true without a deep equals()
    return alphabet.equals(c.alphabet) && map.equals(c.map);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alphabet, map);
  }
}
