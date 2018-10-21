package bayley.cipher;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.Assert;


public class TestCipher {

  public Cipher randomCipher() {
    Cipher c = new Cipher();
    LinkedList<Character> mapFromChars = new LinkedList<>(Cipher.alphabet);
    mapFromChars.remove('\'');
    mapFromChars.remove('-');
    LinkedList<Character> mapToChars = new LinkedList<>(mapFromChars);
    Collections.shuffle(mapToChars);
    while (!mapFromChars.isEmpty() && !mapToChars.isEmpty()) {
      c.add(mapFromChars.pop(), mapToChars.pop());
    }
    return c;
  }

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
    boolean collisionCaught = false;
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
    boolean collisionCaught = false;
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
