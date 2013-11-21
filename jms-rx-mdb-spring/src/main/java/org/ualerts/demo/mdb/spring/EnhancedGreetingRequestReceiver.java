package org.ualerts.demo.mdb.spring;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.support.converter.MessageConverter;
import org.ualerts.demo.GreetingRequest;
import org.ualerts.demo.GreetingResponse;
import org.ualerts.demo.repository.GreetingRepository;

@MessageDriven
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)

@Interceptors(SpringBeanAutowiringInterceptor.class)

public class EnhancedGreetingRequestReceiver implements MessageListener {

  @Autowired(required = true)
  private JmsTemplate jmsTemplate;
  
  @Autowired(required = true)
  private MessageConverter messageConverter;
  
  @EJB
  private GreetingRepository greetingRepository;
    
  /**
   * {@inheritDoc}
   */
  @Override
  public void onMessage(final Message message) {
    try {
      GreetingRequest request = (GreetingRequest) 
          messageConverter.fromMessage(message);
      System.out.println("received request for " + request.getName());
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
        System.out.println("sent reply to " + destination);
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
    String template = greetingRepository.randomGreeting();
    String greeting = String.format(template, request.getName());
    response.setGreeting(greeting);
    return response;
  }

}
