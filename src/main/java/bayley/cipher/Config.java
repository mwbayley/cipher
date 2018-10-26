package bayley.cipher;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Config {

  protected static Set<Character> englishAlphabet = ImmutableSet.of(
          'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
          'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
          'U', 'V', 'W', 'X', 'Y', 'Z', '\'', '-'
  );
  protected static Set<Character> englishKnownCharacters = ImmutableSet.of('\'', '-');

}
