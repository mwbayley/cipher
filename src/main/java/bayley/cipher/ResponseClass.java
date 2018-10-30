package bayley.cipher;

import java.util.Set;

public class ResponseClass {

  String notes;
  Set<String> solutions;

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Set<String> getSolutions() {
    return solutions;
  }

  public void setSolutions(Set<String> solutions) {
    this.solutions = solutions;
  }

  public ResponseClass(String notes, Set<String> solutions) {
    this.notes = notes;
    this.solutions = solutions;
  }

  public ResponseClass() {
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(notes);
    for (String solution : solutions){
      builder.append(String.format("%n%s", solution));
    }
    return builder.toString();
  }
}
