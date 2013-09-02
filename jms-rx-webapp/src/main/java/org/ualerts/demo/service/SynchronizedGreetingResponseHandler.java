package org.ualerts.demo.service;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SynchronizedGreetingResponseHandler implements GreetingResponseHandler {

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
