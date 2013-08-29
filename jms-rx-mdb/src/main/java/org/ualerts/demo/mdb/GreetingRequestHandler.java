package org.ualerts.demo.mdb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.ualerts.demo.GreetingMarshaller;
import org.ualerts.demo.GreetingRequest;
import org.ualerts.demo.GreetingResponse;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", 
        propertyValue = "Auto-acknowledge")
})

public class GreetingRequestHandler implements MessageListener {

  @Resource(name = "java:/ConnectionFactory")
  private ConnectionFactory connectionFactory;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void onMessage(Message message) {
    Connection connection = null;
    try {
      String text = ((TextMessage) message).getText();
      GreetingRequest request = (GreetingRequest) 
          GreetingMarshaller.getInstance().unmarshal(text);
      System.out.println("request for: " + request.getName());
      Destination destination = message.getJMSReplyTo();
      if (destination != null) {
        connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(destination);
        GreetingResponse response = new GreetingResponse();
        response.setGreeting("Hello, " + request.getName());
        TextMessage reply = session.createTextMessage(
            GreetingMarshaller.getInstance().marshal(response));
        producer.send(reply);
      }
    }
    catch (JMSException ex) {
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

}
