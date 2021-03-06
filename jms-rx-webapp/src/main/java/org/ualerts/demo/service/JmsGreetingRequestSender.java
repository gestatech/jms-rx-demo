package org.ualerts.demo.service;

import java.lang.management.ManagementFactory;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.ualerts.demo.GreetingMarshaller;
import org.ualerts.demo.GreetingRequest;

@ApplicationScoped
public class JmsGreetingRequestSender implements GreetingRequestSender,
    JmsGreetingRequestSenderMBean {

  @Resource(name = "jms/queue/test")
  private Destination requestQueue;

  @Resource(name = "jms/queue/testReply")
  private Destination replyQueue;

  private volatile double errorProbability = 0.001;
  
  private ObjectName objName;
  
  /**
   * Gets the {@code errorProbability} property.
   * @return
   */
  public double getErrorProbability() {
    return errorProbability;
  }

  /**
   * Sets the {@code errorProbability} property.
   * @param errorProbability
   */
  public void setErrorProbability(double errorProbability) {
    this.errorProbability = errorProbability;
  }

  @PostConstruct
  public void init() { 
    try {
      MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
      objName = new ObjectName("Greeter:type=GreetingRequestSender");
      mbeanServer.registerMBean(this, objName);
    }
    catch (JMException ex) {
      throw new RuntimeException(ex);
    }
  }

  @PreDestroy
  public void destroy() {
    if (objName == null) return;
    try {
      MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
      mbeanServer.unregisterMBean(objName);
    }
    catch (JMException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  @Override
  public String sendRequest(GreetingRequest request, Session session)
      throws JMSException {
    MessageProducer producer = session.createProducer(requestQueue);
    TextMessage message =
        session.createTextMessage(GreetingMarshaller.getInstance().marshal(
            request));
    message.setJMSReplyTo(replyQueue);
    String id = UUID.randomUUID().toString();
    message.setJMSCorrelationID(id);
    producer.send(message);
    System.out.println("sent request to " + requestQueue);
    if (Math.random() < getErrorProbability()) {
      throw new JMSException("random error");
    }
    return id;
  }

}
