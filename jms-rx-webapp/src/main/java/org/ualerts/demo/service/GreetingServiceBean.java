package org.ualerts.demo.service;

import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.ualerts.demo.GreetingMarshaller;
import org.ualerts.demo.GreetingRequest;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GreetingServiceBean implements GreetingService {

  @Resource(name = "jms/ConnectionFactory")
  private ConnectionFactory connectionFactory;

  @Resource(name = "jms/queue/test")
  private Destination requestQueue;
  
  @Resource(name = "jms/queue/testReply")
  private Destination replyQueue;
  
  @EJB
  private GreetingCorrelationService correlationService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void generateGreeting(String name, 
      GreetingResponseHandler handler) {
    Connection connection = null;
    try {
      connection = connectionFactory.createConnection();
      Session session = connection.createSession(false, 
          Session.AUTO_ACKNOWLEDGE);
      MessageProducer producer = session.createProducer(requestQueue);
      TextMessage message = session.createTextMessage(createRequest(name));
      message.setJMSReplyTo(replyQueue);
      String id = UUID.randomUUID().toString();
      message.setJMSCorrelationID(id);
      producer.send(message);
      correlationService.put(id, handler);
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
    return GreetingMarshaller.getInstance().marshal(request);
  }
  
}
