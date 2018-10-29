package bayley.cipher;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CipherRequest implements RequestHandler<String, String> {

  public String handleRequest(String scrambled, Context context) {
    LambdaLogger logger = context.getLogger();
    logger.log(String.format("received : %s%n", scrambled));
    String response;
    try {
      CipherSolver cs = new CipherSolver();
      StringBuilder builder = new StringBuilder();
      for (String solution : cs.solve(scrambled)) {
        builder.append(String.format("%s%n", solution));
      }
      builder.deleteCharAt(builder.length() - 1);
      response = builder.toString();
    } catch (IOException e) {
      response = e.getLocalizedMessage();
    }
    logger.log(String.format("replied with : %n%s%n", response));
    return response;
  }

}
