package bayley.cipher;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.Assert;

import static bayley.cipher.Cipher.randomCipher;

public class TestCipherSolver {

  private static final int MILLISECOND = 1;
  private static final int SECOND = 1000 * MILLISECOND;

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
    String sentence = new SingleSetDict().randomSentence(10);
    String scrambled = c.encode(sentence);
    Set<String> solutions = solver.solve(scrambled, false);
    Assert.assertTrue(solutions.contains(sentence));
  }

  // This test will timeout unless the input is reordered to consider the longer, distinctive word first
  @Test(timeout=20*SECOND)
  public void testReorderCipherSolver() throws IOException {
    CipherSolver solver = new CipherSolver();
    Cipher c = randomCipher(solver.alphabet, solver.knownCharacters);
    String sentence =
            "What have you done, there is no way this one will go well at all you aardvark!";
    String scrambled = c.encode(sentence);
    Set<String> solutions = solver.solve(scrambled, false);
    Assert.assertTrue(solutions.contains(sentence.toUpperCase()));
  }

  // This is a pathological example with many short, indistinct words. This causes the solution space to explode.
  /*@Test(timeout=20*SECOND)
  public void testImpossibleCipherSolver() throws IOException {
    CipherSolver solver = new CipherSolver();
    Cipher c = randomCipher(solver.alphabet, solver.knownCharacters);
    String sentence =
            "What have you done, there is no way this one will go well at all";
    String scrambled = c.encode(sentence);
    Set<String> solutions = solver.solve(scrambled, true);
    Assert.assertTrue(solutions.contains(sentence.toUpperCase()));
  }*/
}
