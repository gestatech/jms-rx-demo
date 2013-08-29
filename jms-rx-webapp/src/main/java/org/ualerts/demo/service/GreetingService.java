package org.ualerts.demo.service;

import javax.ejb.Local;

@Local
public interface GreetingService {

  String generateGreeting(String name);
  
}
