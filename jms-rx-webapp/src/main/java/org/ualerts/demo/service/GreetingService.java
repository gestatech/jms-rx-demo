package org.ualerts.demo.service;

import javax.ejb.Local;

@Local
public interface GreetingService {

  void generateGreeting(String name, GreetingResponseHandler handler);
  
}
