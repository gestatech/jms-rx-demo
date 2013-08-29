package org.ualerts.demo.service;

import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.ualerts.demo.GreetingRequest;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GreetingServiceBean implements GreetingService {

  @Resource(name = "java:/ConnectionFactory")
  private ConnectionFactory connectionFactory;

  @Resource(name = "java:/queue/test")
  private Destination destination;
  
  @PostConstruct
  public void init() {
    if (destination == null) {
      throw new IllegalArgumentException("null destination injected");
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String generateGreeting(String name) {
    Connection connection = null;
    try {
      connection = connectionFactory.createConnection();
      Session session = connection.createSession(false, 
          Session.AUTO_ACKNOWLEDGE);
      MessageProducer producer = session.createProducer(destination);
      producer.send(session.createTextMessage(createRequest(name)));
      return null;
    }
    catch (JMSException ex) {
      ex.printStackTrace(System.err);
      throw new RuntimeException(ex);
    }
    finally {
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

  private String createRequest(String name) {
    GreetingRequest request = new GreetingRequest();
    request.setName(name);
    return request.marshal();
  }
  
}
