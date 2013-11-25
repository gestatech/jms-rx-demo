package org.ualerts.demo.service;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.ualerts.demo.GreetingRequest;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class GreetingServiceBean implements GreetingService {

  @Resource(name = "jms/ConnectionFactory")
  private ConnectionFactory connectionFactory;
  
  @Inject
  private GreetingCorrelationService correlationService;
  
  @Inject
  private GreetingRequestSender sender;
  
  @Override
  public void generateGreeting(String name, GreetingResponseHandler handler) {
    doGenerateGreeting(name, handler);
  }
  
  private void doGenerateGreeting(String name, GreetingResponseHandler handler) {
    Connection connection = null;
    Session session = null;
    try {
      GreetingRequest request = new GreetingRequest();
      request.setName(name);
      connection = connectionFactory.createConnection();
      session = connection.createSession(true, Session.SESSION_TRANSACTED);
      String id = sender.sendRequest(request, session);
      correlationService.put(id, handler);
    }
    catch (JMSException ex) {
      try {
        session.rollback();
      }
      catch (JMSException rex) {
        ex.printStackTrace(System.err);
      }
      throw new RuntimeException(ex);
    }
    finally {
      if (session != null) {
        try {
          session.close();
        }
        catch (JMSException ex) {
          ex.printStackTrace(System.err);
        }
      }
      if (connection != null) {
        try {
          connection.close();      
        }
        catch (JMSException ex) {
          ex.printStackTrace(System.err);
        }
      }
    }
  }

}
