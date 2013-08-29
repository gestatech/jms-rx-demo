package org.ualerts.demo;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class GreetingMarshaller {

  private static final GreetingMarshaller INSTANCE = new GreetingMarshaller();
  
  private JAXBContext context;
  
  private GreetingMarshaller() {
  }
  
  public static GreetingMarshaller getInstance() {
    return INSTANCE;
  }
  
  public String marshal(Object obj) {
    try {
      Marshaller marshaller = getContext().createMarshaller();
      StringWriter writer = new StringWriter();
      marshaller.marshal(obj, writer);
      return writer.toString();
    }
    catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public GreetingRequest unmarshal(String text) {
    try {
      Unmarshaller unmarshaller = getContext().createUnmarshaller();
      StringReader reader = new StringReader(text);
      return (GreetingRequest) unmarshaller.unmarshal(reader);
    }
    catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
  }

  private synchronized JAXBContext getContext() throws JAXBException {
    if (context == null) {
      context = JAXBContext.newInstance(GreetingRequest.class, 
          GreetingResponse.class);
    }
    return context; 
  }
  
}

