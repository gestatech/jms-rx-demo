package org.ualerts.demo.web;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ualerts.demo.service.GreetingService;

public abstract class AbstractGreetingServlet extends HttpServlet {

  private static final long serialVersionUID = -2574073959560484334L;

  private static final String VIEWS_LOCATION = "/WEB-INF/views/";

  private static final String FORM_VIEW = VIEWS_LOCATION + "form.jsp";

  private static final String SUCCESS_VIEW = VIEWS_LOCATION + "success.jsp";

  @EJB
  protected GreetingService greetingService;

  @Override
  protected void doGet(HttpServletRequest request, 
      HttpServletResponse response) throws ServletException, IOException {
    String name = request.getParameter("name");
    if (name == null || name.trim().isEmpty()) {
      request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }
    else {
      produceGreeting(name, request, response);
    }
  }
  
  protected abstract void produceGreeting(String name, 
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException;

  protected void respondWithGreeting(String greeting,
      HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {
    String accept = request.getHeader("Accept");
    if (accept != null && accept.contains("text/html")) {
      request.setAttribute("greeting", greeting);
      request.getRequestDispatcher(SUCCESS_VIEW).forward(request, response);
    }
    else {
      response.setContentType("text/plain");
      response.getWriter().println(greeting);
    }
  }

}
