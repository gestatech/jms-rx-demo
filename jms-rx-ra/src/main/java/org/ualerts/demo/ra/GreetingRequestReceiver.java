package org.ualerts.demo.ra;

import java.text.MessageFormat;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.ualerts.demo.GreetingRequest;
import org.ualerts.demo.GreetingResponse;

@Component
public class GreetingRequestReceiver implements MessageListener {

  @Autowired(required = true)
  private JmsTemplate jmsTemplate;
  
  @Autowired(required = true)
  private MessageConverter messageConverter;
  
  @Override
  public void onMessage(final Message message) {
    try {
      GreetingRequest request = (GreetingRequest) 
          messageConverter.fromMessage(message);
      Destination destination = message.getJMSReplyTo();
      if (destination != null) {
        jmsTemplate.convertAndSend(destination, createResponse(request),
            new MessagePostProcessor() {
              public Message postProcessMessage(Message reply)
                  throws JMSException {
                reply.setJMSCorrelationID(message.getJMSCorrelationID());
                return reply;
              }          
        });
      }
    }
    catch (ClassCastException ex) {
      System.err.println("ignored non-request message");
    }
    catch (JMSException ex) {
      throw new RuntimeException(ex);
    }
  }

  private GreetingResponse createResponse(GreetingRequest request) {
    GreetingResponse response = new GreetingResponse();
    String template = "Hello, {0}.";
    String greeting = MessageFormat.format(template, request.getName());
    response.setGreeting(greeting);
    return response;
  }

}
