package org.ualerts.demo.service;

import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.ualerts.demo.GreetingMarshaller;
import org.ualerts.demo.GreetingRequest;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GreetingRequestSenderBean implements GreetingRequestSender {

  @Resource(name = "jms/queue/test")
  private Destination requestQueue;

  @Resource(name = "jms/queue/testReply")
  private Destination replyQueue;

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
    if (Math.random() < 0.1) {
      throw new JMSException("random error");
    }
    return id;
  }

}
