package org.ualerts.demo.mdb.spring;

import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.interceptor.Interceptors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.ualerts.demo.GreetingMarshaller;
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

  @Resource(name = "jms/ConnectionFactory")
  private ConnectionFactory connectionFactory;
  
  @Autowired(required = true)
  private MessageListenerAdapter adapter;
  
  @EJB
  private GreetingRepository greetingRepository;
  
  @PostConstruct
  public void init() {
    System.out.println(adapter);
  }
  
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
      Destination destination = message.getJMSReplyTo();
      if (destination != null) {
        connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(destination);
        TextMessage reply = session.createTextMessage(
            GreetingMarshaller.getInstance().marshal(
                createResponse(request)));
        reply.setJMSCorrelationID(message.getJMSCorrelationID());
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

  private GreetingResponse createResponse(GreetingRequest request) {
    GreetingResponse response = new GreetingResponse();
    String template = greetingRepository.randomGreeting();
    String greeting = MessageFormat.format(template, request.getName());
    response.setGreeting(greeting);
    return response;
  }

}
