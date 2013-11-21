package org.ualerts.demo.service;

import javax.ejb.Local;
import javax.jms.JMSException;
import javax.jms.Session;

import org.ualerts.demo.GreetingRequest;

@Local
public interface GreetingRequestSender {

  String sendRequest(GreetingRequest request, Session session) 
      throws JMSException;
  
}
