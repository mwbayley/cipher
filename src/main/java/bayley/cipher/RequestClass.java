package bayley.cipher;

public class RequestClass {

  String language;
  String scrambled;

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getScrambled() {
    return scrambled;
  }

  public void setScrambled(String scrambled) {
    this.scrambled = scrambled;
  }

  public RequestClass(String language, String scrambled) {
    this.language = language;
    this.scrambled = scrambled;
  }

  public RequestClass() {
  }

  @Override
  public String toString() {
    return String.format("language=%s, scrambled=%s", language, scrambled);
  }

}