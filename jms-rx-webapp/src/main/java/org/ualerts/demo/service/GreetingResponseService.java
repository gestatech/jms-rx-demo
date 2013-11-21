package org.ualerts.demo.service;

import javax.ejb.Local;

import org.ualerts.demo.GreetingResponse;

@Local
public interface GreetingResponseService {

  void deliver(GreetingResponse response, GreetingResponseHandler handler);
  
}
