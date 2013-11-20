package org.ualerts.demo.web;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ualerts.demo.service.GreetingResponseHandler;
import org.ualerts.demo.service.GreetingService;

@WebServlet(urlPatterns = "/sync")
public class SyncGreetingServlet extends HttpServlet {

  private static final long serialVersionUID = 5888009870899864827L;

  private static final String ACCEPT_HEADER = "Accept";
  private static final String TEXT_HTML_TYPE = "text/html";
  private static final String TEXT_PLAIN_TYPE = "text/plain";

  private static final String VIEWS_LOCATION = "/WEB-INF/views/";
  private static final String FORM_VIEW = VIEWS_LOCATION + "form.jsp";
  private static final String SUCCESS_VIEW = VIEWS_LOCATION + "success.jsp";

  @EJB
  private GreetingService greetingService;

  
  @Override
  public void init() throws ServletException {
    if (greetingService == null) {
      throw new javax.servlet.UnavailableException("no greetingService");
    }
  }

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

  protected void produceGreeting(String name, HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    SyncGreetingResponseHandler responseHandler = 
        new SyncGreetingResponseHandler();
    greetingService.generateGreeting(name, responseHandler);
    try {
      respondWithGreeting(responseHandler.awaitResponse(), request, response);
    }
    catch (InterruptedException ex) {
      respondWithGreeting("INTERRUPTED", request, response);
    }
  }
  
  private static class SyncGreetingResponseHandler 
      implements GreetingResponseHandler {

    private final Lock lock = new ReentrantLock();
    private final Condition readyCondition = lock.newCondition();
    
    private String greeting;
    
    @Override
    public void handleResponse(String greeting) {
      lock.lock();
      try {
        this.greeting = greeting;
        readyCondition.signalAll();
      }
      finally {
        lock.unlock();
      }
    }

    public String awaitResponse() throws InterruptedException {
      lock.lock();
      try {
        while (greeting == null) {
          readyCondition.await();
        }
        return greeting;
      }
      finally {
        lock.unlock();
      }
    }

  }

}
