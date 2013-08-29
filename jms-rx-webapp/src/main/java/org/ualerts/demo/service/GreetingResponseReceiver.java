package org.ualerts.demo.service;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.ualerts.demo.GreetingMarshaller;
import org.ualerts.demo.GreetingResponse;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", 
        propertyValue = "Auto-acknowledge")
})

public class GreetingResponseReceiver implements MessageListener {
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void onMessage(Message message) {
    Connection connection = null;
    try {
      String text = ((TextMessage) message).getText();
      GreetingResponse response = (GreetingResponse) 
          GreetingMarshaller.getInstance().unmarshal(text);
      System.out.println("response received: " + response.getGreeting());
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
