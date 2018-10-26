package bayley.cipher;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CipherRequest implements RequestHandler<String, String>{
  public String handleRequest(String scrambled, Context context) {
    String response = "";
    try {
      CipherSolver cs = new CipherSolver();
      StringBuilder builder = new StringBuilder();
      for (String solutions : cs.solve(scrambled)) {
        builder.append(response);
        builder.append('\n');
      }
      response = builder.toString();
    } catch (IOException e) {
      // TODO: logging and error handling;
      response = "IOError";
    }
    return response;
  }


}
