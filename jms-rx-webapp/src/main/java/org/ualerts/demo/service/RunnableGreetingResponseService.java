package org.ualerts.demo.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;

import org.ualerts.demo.GreetingResponse;

@ApplicationScoped
public class RunnableGreetingResponseService implements GreetingResponseService,
    Runnable {

  private final BlockingQueue<Runnable> responders =
      new ArrayBlockingQueue<Runnable>(10);

  @Resource
  private ManagedThreadFactory threadFactory;
  
  private Thread worker;
  
  @PostConstruct
  public void init() {
    worker = threadFactory.newThread(this);
    worker.setName("Greeting Response Worker");
    worker.start();
  }
  
  @PreDestroy
  public void destroy() {
    worker.interrupt();
    try {
      worker.join();
    }
    catch (InterruptedException ex) {
      System.err.println("interrupted while waiting for worker to stop");
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        Runnable responder = responders.take();
        responder.run();
      }
      catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  public void deliver(final GreetingResponse response,
      final GreetingResponseHandler handler) {
    try {
      responders.put(new Runnable() {
        @Override
        public void run() {
          handler.handleResponse(response.getGreeting());
        } 
      });
    }
    catch (InterruptedException ex) {
      System.err.println("INTERRUPTED");
      Thread.currentThread().interrupt();
    }
  }

}
