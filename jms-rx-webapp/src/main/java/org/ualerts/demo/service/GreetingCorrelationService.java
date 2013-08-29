package org.ualerts.demo.service;

import javax.ejb.Local;

@Local
public interface GreetingCorrelationService {

  void put(String id, GreetingResponseHandler handler);
  
  GreetingResponseHandler take(String id);
  
}
