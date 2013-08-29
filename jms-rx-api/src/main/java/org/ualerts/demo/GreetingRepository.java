package org.ualerts.demo;

import javax.ejb.Remote;

@Remote
public interface GreetingRepository {

  String randomGreeting();
  
}
