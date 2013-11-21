package org.ualerts.demo.service;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.ualerts.demo.GreetingMarshaller;
import org.ualerts.demo.GreetingResponse;

@MessageDriven
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class GreetingResponseReceiver implements MessageListener {
  
  @EJB
  private GreetingCorrelationService correlationService;
  
  @EJB
  private GreetingResponseService responseService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void onMessage(Message message) {
    try {
      String text = ((TextMessage) message).getText();        
      GreetingResponse response = (GreetingResponse) 
          GreetingMarshaller.getInstance().unmarshal(text);
      System.out.println("received greeting: " + response.getGreeting());
      GreetingResponseHandler handler = correlationService.take(
          message.getJMSCorrelationID());
      
      if (handler != null) {
        responseService.deliver(response, handler);
      }
      else {
        System.err.println("response with no handler: " 
            + response.getGreeting());
      }
    }
    catch (ClassCastException ex) {
      System.err.println("ignoring non-response message");
    }
    catch (JMSException ex) {
      throw new RuntimeException(ex);
    }
  }

}
