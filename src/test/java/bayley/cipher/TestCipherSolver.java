package bayley.cipher;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.Assert;

import static bayley.cipher.Cipher.randomCipher;

public class TestCipherSolver {

  @Test
  public void testRefine() throws IOException {
    String solution = "HALLOWEEN";
    Cipher encodingCipher = Cipher.randomCipher();
    String scrambled = encodingCipher.encode(solution);
    CipherSolver solver = new CipherSolver();
    Cipher emptyCipher = new Cipher();
    Set<Cipher> ciphers = solver.refine(emptyCipher, scrambled);
    Set<String> solutions = new LinkedHashSet<>();
    for (Cipher c : ciphers) {
      solutions.add(c.decode(scrambled));
    }
    Set<String> expected = ImmutableSet.of(
            "HALLOWEEN",
            "BUCCANEER",
            "BUZZKILLS",
            "PUSSYFOOT",
            "BALLYHOOS",
            "PUFFBALLS"
    );
    Assert.assertEquals(expected, solutions);
  }

  @Test
  public void testSimpleCipherSolver() throws IOException {
    Cipher c = randomCipher();
    CipherSolver solver = new CipherSolver();
    String sentence = solver.dict.randomSentence(10);
    String scrambled = c.encode(sentence);
    Set<String> solutions = solver.solve(scrambled);
    Assert.assertTrue(solutions.contains(sentence));
  }

}
