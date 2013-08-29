package org.ualerts.demo.mdb;

import javax.ejb.Local;

@Local
public interface GreetingRepository {

  String randomGreeting();
  
}
