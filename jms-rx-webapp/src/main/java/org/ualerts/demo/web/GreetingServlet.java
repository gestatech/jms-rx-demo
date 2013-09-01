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

  private static final String VIEWS_LOCATION = "/WEB-INF/views/";

  private static final long serialVersionUID = -2825813671655554174L;

  private final String formView =
      VIEWS_LOCATION + getClass().getSimpleName() + "/form.jsp";

  private final String successView =
      VIEWS_LOCATION + getClass().getSimpleName() + "/success.jsp";

  @EJB
  private GreetingService greetingService;
  
  @Override
  protected void doGet(HttpServletRequest request, 
      HttpServletResponse response) throws ServletException, IOException {
    String name = request.getParameter("name");
    if (name == null || name.trim().isEmpty()) {
      request.getRequestDispatcher(formView).forward(request, response);
    }
    else {
      request.startAsync();
      request.setAttribute("name", name);
      greetingService.generateGreeting(name, 
          new AsyncGreetingResponseHandler(request, response, successView));
    }
  }

}
