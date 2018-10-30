package bayley.cipher;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Request implements RequestHandler<RequestClass, ResponseClass> {

  public ResponseClass handleRequest(RequestClass request, Context context) {
    LambdaLogger logger = context.getLogger();
    logger.log(String.format("received : %s%n", request));
    ResponseClass response = new ResponseClass();
    try {
      CipherSolver cs = new CipherSolver();
      response.setSolutions(cs.solve(request.getScrambled()));
    } catch (IOException e) {
      response.setNotes(e.getLocalizedMessage());
    }
    logger.log(String.format("replied with : %n%s%n", response));
    return response;
  }

}
