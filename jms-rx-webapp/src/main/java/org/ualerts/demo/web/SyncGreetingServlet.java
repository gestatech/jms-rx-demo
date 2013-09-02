package org.ualerts.demo.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/sync")
public class SyncGreetingServlet extends AbstractGreetingServlet {

  private static final long serialVersionUID = 5888009870899864827L;

  @Override
  protected void produceGreeting(String name, HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    String greeting = greetingService.generateGreeting(name);
    respondWithGreeting(greeting, request, response);
  }
  
}
