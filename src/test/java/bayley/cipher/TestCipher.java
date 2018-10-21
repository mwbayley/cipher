package bayley.cipher;

import org.junit.Test;
import org.junit.Assert;

public class TestCipher {

  @Test
  public void testSimpleCipher() {
    Cipher c = new Cipher();
    c.add('a', 'a');
    c.add('b', 'b');
    c.add('c', 'c');
    String cipherString = c.toString();
    String expectedString = "a:a, b:b, c:c, ':'";
    Assert.assertEquals(cipherString, expectedString);
  }


}
