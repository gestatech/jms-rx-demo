package org.ualerts.demo.mdb.spring;

import java.text.MessageFormat;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
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

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "queue/test"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode",
        propertyValue = "Auto-acknowledge")
})

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
    catch (JMSException ex) {
      throw new RuntimeException(ex);
    }
  }

  private GreetingResponse createResponse(GreetingRequest request) {
    GreetingResponse response = new GreetingResponse();
    String template = greetingRepository.randomGreeting();
    String greeting = MessageFormat.format(template, request.getName());
    response.setGreeting(greeting);
    return response;
  }

}
