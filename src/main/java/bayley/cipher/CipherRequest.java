package bayley.cipher;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CipherRequest implements RequestHandler<String, String> {

  public String handleRequest(String scrambled, Context context) {
    LambdaLogger logger = context.getLogger();
    logger.log("received : " + scrambled);
    String response = "";
    try {
      CipherSolver cs = new CipherSolver();
      StringBuilder builder = new StringBuilder();
      for (String solutions : cs.solve(scrambled)) {
        builder.append(response);
        builder.append('\n');
      }
      builder.deleteCharAt(builder.length() - 1);
      response = builder.toString();
    } catch (IOException e) {
      // TODO: logging and error handling;
      response = "IOException";
    }
    logger.log("replied with : " + response);
    return response;
  }


}
