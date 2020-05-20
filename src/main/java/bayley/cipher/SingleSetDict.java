package bayley.cipher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class SingleSetDict implements CipherDict {

  private final Set<String> dictSet;
  private int size;

  SingleSetDict() throws IOException {
    this(Constants.ENGLISH_ALPHABET);
  }

  SingleSetDict(Set<Character> alphabet) throws IOException {
    dictSet = new LinkedHashSet<>();
    size = 0;
    // read in the dictionary to a list
    BufferedReader dictReader = new BufferedReader(
            new InputStreamReader(
                    getClass().getResourceAsStream("/dictionaries/american-english.txt")));
    String word;
    wordLoop: while ((word = dictReader.readLine()) != null) {
      word = word.toUpperCase();
      // exclude words with non-alphabet characters
      for (Character c : word.toCharArray()) {
        if (!alphabet.contains(c)) {
          continue wordLoop;
        }
      }
      dictSet.add(word);
      size++;
    }
  }

  @Override
  public String toString() {
    return String.format("%s has %d words", super.toString(), size);
  }

  @Override
  public int nSimilarWords(String scrambledWord) {
    return size;
  }

  @Override
  public Set<String> potentialMatches(Cipher c, String scrambled) {
    return dictSet;
  }

  @Override
  public String randomWord() {
    int num = new Random().nextInt(size + 1);
    int i = 0;
    String lastWord = "";
    for (String word : dictSet) {
      if (i++ == num) {
        return word;
      }
      lastWord = word;
    }
    return lastWord;
  }
}
