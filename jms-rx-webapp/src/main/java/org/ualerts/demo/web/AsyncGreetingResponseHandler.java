package org.ualerts.demo.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ualerts.demo.service.GreetingResponseHandler;

public class AsyncGreetingResponseHandler implements GreetingResponseHandler {

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final String successView;
  
  public AsyncGreetingResponseHandler(HttpServletRequest request,
      HttpServletResponse response, String successView) {
    this.request = request;
    this.response = response;
    this.successView = successView;
  }

  @Override
  public void handleResponse(String greeting) {
    try {
      String accept = request.getHeader("Accept");
      if (accept != null && accept.contains("text/html")) {
        request.setAttribute("greeting", greeting);
        request.getRequestDispatcher(successView).forward(request, response);
      }
      else {
        response.setContentType("text/plain");
        response.getWriter().println(greeting);
      }
      request.getAsyncContext().complete();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    catch (ServletException ex) {
      throw new RuntimeException(ex);
    }
  }

}
