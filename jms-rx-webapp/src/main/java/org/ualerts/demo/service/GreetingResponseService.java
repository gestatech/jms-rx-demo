package org.ualerts.demo.service;

import org.ualerts.demo.GreetingResponse;

public interface GreetingResponseService {

  void deliver(GreetingResponse response, GreetingResponseHandler handler);
  
}
