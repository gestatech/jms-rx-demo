package org.ualerts.demo.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ualerts.demo.service.GreetingResponseHandler;

public class AsyncGreetingResponseHandler implements GreetingResponseHandler {

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  
  public AsyncGreetingResponseHandler(HttpServletRequest request,
      HttpServletResponse response) {
    this.request = request;
    this.response = response;
  }

  @Override
  public void handleResponse(String greeting) {
    try {
      response.getWriter().println(greeting);
      request.getAsyncContext().complete();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
