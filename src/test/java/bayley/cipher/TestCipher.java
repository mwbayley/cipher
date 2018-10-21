package bayley.cipher;

import org.junit.Test;
import org.junit.Assert;

public class TestCipher {

  @Test
  public void testSimpleCipher() {
    Cipher c = new Cipher();
    c.add('a', 'f');
    c.add('b', 'g');
    c.add('c', 'h');
    String cipherString = c.toString();
    String expectedString = "a:f, b:g, c:h, ':'";
    Assert.assertEquals(cipherString, expectedString);
    Assert.assertEquals(c.solve("abc"), "fgh");
  }

  @Test
  public void testFromCollision() {
    Cipher c = new Cipher();
    c.add('a', 'b');
    Boolean collisionCaught = false;
    String exceptionMsg = "";
    try {
      c.add('a', 'c');
    } catch (RuntimeException e) {
      collisionCaught = true;
      exceptionMsg = e.getMessage();
    }
    Assert.assertTrue(collisionCaught);
    Assert.assertEquals(exceptionMsg, "Character a is already mapped from in the cipher");
  }

  @Test
  public void testToCollision() {
    Cipher c = new Cipher();
    c.add('a', 'b');
    Boolean collisionCaught = false;
    String exceptionMsg = "";
    try {
      c.add('c', 'b');
    } catch (RuntimeException e) {
      collisionCaught = true;
      exceptionMsg = e.getMessage();
    }
    Assert.assertTrue(collisionCaught);
    Assert.assertEquals(exceptionMsg, "Character b is already mapped to in the cipher");
  }

}
