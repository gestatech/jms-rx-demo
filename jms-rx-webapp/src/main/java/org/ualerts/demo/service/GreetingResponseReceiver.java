package org.ualerts.demo.service;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
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
  
  @EJB
  private GreetingCorrelationService correlationService;
  
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
      GreetingResponseHandler handler = correlationService.take(
          message.getJMSCorrelationID());
      if (handler != null) {
        handler.handleResponse(response.getGreeting());
      }
      else {
        System.err.println("response with no handler: " 
            + response.getGreeting());
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
