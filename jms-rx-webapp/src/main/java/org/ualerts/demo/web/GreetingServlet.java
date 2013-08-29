package org.ualerts.demo.web;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ualerts.demo.service.GreetingService;

@WebServlet(urlPatterns = "/", asyncSupported = true)
public class GreetingServlet extends HttpServlet {

  private static final long serialVersionUID = -2825813671655554174L;

  @EJB
  private GreetingService greetingService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected void doGet(HttpServletRequest request, 
      HttpServletResponse response) throws ServletException, IOException {
    request.startAsync();
    greetingService.generateGreeting("Cherylanne", 
        new AsyncGreetingResponseHandler(request, response));
  }

}
