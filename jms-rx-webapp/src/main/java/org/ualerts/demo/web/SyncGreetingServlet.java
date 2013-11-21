package org.ualerts.demo.web;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ualerts.demo.service.GreetingResponseHandler;
import org.ualerts.demo.service.GreetingService;

@WebServlet(urlPatterns = "/sync")
public class SyncGreetingServlet extends AbstractGreetingServlet {

  private static final long serialVersionUID = 5888009870899864827L;

  @EJB
  private GreetingService greetingService;
  
  @Override
  public void init() throws ServletException {
    if (greetingService == null) {
      throw new javax.servlet.UnavailableException("no greetingService");
    }
  }

  @Override
  protected void produceGreeting(String name, HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    SyncGreetingResponseHandler responseHandler = 
        new SyncGreetingResponseHandler();
    try {
      greetingService.generateGreeting(name, responseHandler);
      try {
        respondWithGreeting(responseHandler.awaitResponse(), request, response);
      }
      catch (InterruptedException ex) {
        respondWithGreeting("INTERRUPTED", request, response);
      }
    }
    catch (Exception ex) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
          ex.toString());
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
      long start = System.currentTimeMillis();
      long now = start;
      try {
        while (greeting == null && now - start < 3000) {
          readyCondition.await(250, TimeUnit.MILLISECONDS);
          now = System.currentTimeMillis();
        }
        return greeting;
      }
      finally {
        lock.unlock();
      }
    }

  }

}
