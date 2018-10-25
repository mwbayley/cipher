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
    CipherSolver solver = new CipherSolver();
    Cipher encodingCipher = Cipher.randomCipher(solver.alphabet, solver.knownCharacters);
    String scrambled = encodingCipher.encode(solution);
    Cipher emptyCipher = new Cipher(solver.alphabet, solver.knownCharacters);
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
    CipherSolver solver = new CipherSolver();
    Cipher c = randomCipher(solver.alphabet, solver.knownCharacters);
    String sentence = solver.dict.randomSentence(10);
    String scrambled = c.encode(sentence);
    Set<String> solutions = solver.solve(scrambled);
    Assert.assertTrue(solutions.contains(sentence));
  }

}
