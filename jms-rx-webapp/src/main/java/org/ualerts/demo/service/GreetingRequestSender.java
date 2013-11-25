package org.ualerts.demo.service;

import javax.jms.JMSException;
import javax.jms.Session;

import org.ualerts.demo.GreetingRequest;

public interface GreetingRequestSender {

  String sendRequest(GreetingRequest request, Session session) 
      throws JMSException;
  
}
