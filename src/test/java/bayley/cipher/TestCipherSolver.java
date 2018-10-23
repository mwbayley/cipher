package bayley.cipher;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;

import static bayley.cipher.Cipher.randomCipher;

public class TestCipherSolver {

  @Test
  public void testSimpleCipherSolver() throws IOException {
    Cipher c = randomCipher();
    CipherSolver solver = new CipherSolver();
    String sentence = solver.dict.randomSentence(10);
    System.out.println(sentence);
    String scrambled = c.encode(sentence);
    System.out.println(scrambled);
    List<String> solutions = solver.solve(scrambled);
    for (String solution : solutions) {
      System.out.println(solution);
    }
    Assert.assertTrue(solutions.contains(sentence));
  }



}
