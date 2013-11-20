package org.ualerts.demo.web;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ualerts.demo.service.GreetingResponseHandler;
import org.ualerts.demo.service.GreetingService;

@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class AsyncGreetingServlet extends AbstractGreetingServlet {

  private static final long serialVersionUID = -2825813671655554174L;
  
  @EJB
  private GreetingService greetingService;
  
  @Override
  protected void produceGreeting(String name, 
      HttpServletRequest request, HttpServletResponse response) {
    request.startAsync();
    request.setAttribute("name", name);
    greetingService.generateGreeting(name, 
        new AsyncGreetingResponseHandler(request, response));
  }
  
  private class AsyncGreetingResponseHandler implements GreetingResponseHandler {

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
        respondWithGreeting(greeting, request, response);
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

}
