package bayley.cipher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

public class ListDict implements CipherDict {

  private Set<String> dictSet;
  private int size;

  ListDict() throws IOException {
    this("/usr/share/dict/words");
  }

  ListDict(String dictPath) throws IOException {
    dictSet = new LinkedHashSet<>();
    size = 0;
    // read in the dictionary to a list
    try(BufferedReader br = new BufferedReader(new FileReader(dictPath))) {
      String word;
      while ((word = br.readLine()) != null) {
        dictSet.add(word.toUpperCase());
        size++;
      }
    }
  }

  public String stats() {
    return String.format("ListDict has %d words", size);
  }

  public int size() {
    return size;
  }

  public Set<String> potentialMatches(Cipher c, String scrambled) {
    return dictSet;
  }

  public String randomWord() {
    int num = new Random().nextInt(size + 1);
    int i = 0;
    String lastWord = "";
    for(String word : dictSet)
    {
      if (i == num)
        return word;
      i++;
      lastWord = word;
    }
    return lastWord;
  }
}
