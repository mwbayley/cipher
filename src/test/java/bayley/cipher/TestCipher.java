package bayley.cipher;

import java.io.IOException;

import org.junit.Test;
import org.junit.Assert;

import static bayley.cipher.Cipher.randomCipher;


public class TestCipher {


  @Test
  public void testSimpleCipher() {
    Cipher c = new Cipher();
    c.add('A', 'F');
    c.add('B', 'G');
    c.add('C', 'H');
    String cipherString = c.toString();
    String expectedString = "':', -:-, A:F, B:G, C:H";
    Assert.assertEquals(expectedString, cipherString);
    Assert.assertEquals("FGH", c.decode("ABC"));
  }

  @Test
  public void testRandomCipher() throws IOException {
    Cipher c = randomCipher();
    CipherDict dict = new ListDict();
    String sentence = dict.randomSentence(10);
    System.out.println(sentence);
    String scrambled = c.encode(sentence);
    System.out.println(scrambled);
    String unscrambled = c.decode(scrambled);
    System.out.println(unscrambled);
    Assert.assertEquals(sentence, unscrambled);
  }

  @Test
  public void testFromCollision() {
    Cipher c = new Cipher();
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
    Cipher c = new Cipher();
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
    Cipher c1 = randomCipher();
    Cipher c2 = new Cipher(c1);
    Assert.assertNotSame(c1, c2);
    Assert.assertTrue(c1.isSuperCipher(c2));
    Assert.assertTrue(c2.isSuperCipher(c1));
    Assert.assertEquals(c1, c2);
  }

  @Test
  public void testRefine() {
    
  }

}
