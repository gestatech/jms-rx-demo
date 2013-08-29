package org.ualerts.demo.repository;

import javax.ejb.Remote;

@Remote
public interface GreetingRepository {

  String randomGreeting();
  
}
