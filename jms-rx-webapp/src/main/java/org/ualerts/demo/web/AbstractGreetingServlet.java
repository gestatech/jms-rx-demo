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

  private static final String ACCEPT_HEADER = "Accept";

  private static final String TEXT_HTML_TYPE = "text/html";

  private static final String TEXT_PLAIN_TYPE = "text/plain";

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
      if (headerContains(request, ACCEPT_HEADER, TEXT_HTML_TYPE)) {
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
      }
      else {
        response.setContentType(TEXT_PLAIN_TYPE);
        response.getWriter().println("Specify a name using the 'name' parameter");
      }
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
    if (headerContains(request, ACCEPT_HEADER, TEXT_HTML_TYPE)) {
      request.setAttribute("greeting", greeting);
      request.getRequestDispatcher(SUCCESS_VIEW).forward(request, response);
    }
    else {
      response.setContentType(TEXT_PLAIN_TYPE);
      response.getWriter().println(greeting);
    }
  }

  private boolean headerContains(HttpServletRequest request, String header,
      String s) {
    String value = request.getHeader(header);
    return value != null && value.contains(s);
  }
  
}
