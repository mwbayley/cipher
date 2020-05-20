package bayley.cipher;

import java.io.IOException;

import org.junit.Test;
import org.junit.Assert;

import static bayley.cipher.Cipher.randomCipher;

public class TestCipher {

  @Test
  public void testSimpleCipher() {
    Cipher c = new Cipher(Constants.ENGLISH_ALPHABET, Constants.ENGLISH_KNOWN_CHARACTERS);
    c.add('A', 'F');
    c.add('B', 'G');
    c.add('C', 'H');
    String mapString = c.map.toString();
    String expectedMap = "{'=', -=-, A=F, B=G, C=H}";
    Assert.assertEquals(expectedMap, mapString);
    Assert.assertEquals("FGH", c.decode("ABC"));
  }

  @Test
  public void testRandomCipher() throws IOException {
    Cipher c = randomCipher(Constants.ENGLISH_ALPHABET, Constants.ENGLISH_KNOWN_CHARACTERS);
    CipherDict dict = new SingleSetDict();
    String sentence = dict.randomSentence(10);
    String scrambled = c.encode(sentence);
    String unscrambled = c.decode(scrambled);
    Assert.assertEquals(sentence, unscrambled);
  }

  @Test
  public void testFromCollision() {
    Cipher c = new Cipher(Constants.ENGLISH_ALPHABET, Constants.ENGLISH_KNOWN_CHARACTERS);
    c.add('A', 'B');
    boolean collisionCaught = false;
    String exceptionMsg = "";
    try {
      c.add('A', 'C');
    } catch (RuntimeException e) {
      collisionCaught = true;
      exceptionMsg = e.getMessage();
    }
    Assert.assertTrue(collisionCaught);
    Assert.assertEquals("Character A is already mapped from in the cipher", exceptionMsg);
  }

  @Test
  public void testToCollision() {
    Cipher c = new Cipher(Constants.ENGLISH_ALPHABET, Constants.ENGLISH_KNOWN_CHARACTERS);
    c.add('A', 'B');
    boolean collisionCaught = false;
    String exceptionMsg = "";
    try {
      c.add('C', 'B');
    } catch (RuntimeException e) {
      collisionCaught = true;
      exceptionMsg = e.getMessage();
    }
    Assert.assertTrue(collisionCaught);
    Assert.assertEquals("Character B is already mapped to in the cipher", exceptionMsg);
  }

  @Test
  public void testCipherCloning() {
    Cipher c1 = randomCipher(Constants.ENGLISH_ALPHABET, Constants.ENGLISH_KNOWN_CHARACTERS);
    Cipher c2 = new Cipher(c1);
    Assert.assertNotSame(c1, c2);
    Assert.assertEquals(c1, c2);
  }

  @Test
  public void testSuperCipher() {
    Cipher c1 = new Cipher(Constants.ENGLISH_ALPHABET, Constants.ENGLISH_KNOWN_CHARACTERS);
    c1.add('A', 'A');
    c1.add('B', 'B');
    c1.add('C', 'C');
    Cipher c2 = new Cipher(c1);
    Assert.assertTrue(c1.isSuperCipher(c2));
    Assert.assertTrue(c2.isSuperCipher(c1));
    c1.add('D', 'D');
    Assert.assertTrue(c1.isSuperCipher(c2));
    Assert.assertFalse(c2.isSuperCipher(c1));
  }

  @Test
  public void testPositiveMatch() {
    Cipher c1 = new Cipher(Constants.ENGLISH_ALPHABET, Constants.ENGLISH_KNOWN_CHARACTERS);
    Cipher c2 = c1.match("ABC", "CAT");
    Assert.assertEquals("{'=', -=-, A=C, B=A, C=T}", c2.map.toString());
  }

  @Test
  public void testNegativeMatch() {
    Cipher c1 = new Cipher(Constants.ENGLISH_ALPHABET, Constants.ENGLISH_KNOWN_CHARACTERS);
    Cipher c2 = c1.match("AAA", "CAT");
    Assert.assertEquals(null, c2);
  }

}
