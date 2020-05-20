package bayley.cipher;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Constants {

  protected static final Set<Character> ENGLISH_ALPHABET = ImmutableSet.of(
          'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
          'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
          'U', 'V', 'W', 'X', 'Y', 'Z', '\'', '-'
  );

  protected static final Set<Character> ENGLISH_KNOWN_CHARACTERS = ImmutableSet.of('\'', '-');

}
