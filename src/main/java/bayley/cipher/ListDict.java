package bayley.cipher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class ListDict implements CipherDict {

  private List<String> dictList;
  private int size;

  ListDict() throws IOException {
    this("/usr/share/dict/words");
  }

  ListDict(String dictPath) throws IOException {
    dictList = new LinkedList<>();
    size = 0;
    // read in the dictionary to a list
    try(BufferedReader br = new BufferedReader(new FileReader(dictPath))) {
      String word;
      while ((word = br.readLine()) != null) {
        dictList.add(word);
        size++;
      }
    }
    printStats();
  }

  public void printStats() {
    System.out.println(String.format("ListDict has %n words", size));
  }

  public int size() {
    return size;
  }

  public List<String> getAll(Cipher c, String scrambled) {
    return dictList;
  }

}
