package org.ualerts.demo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GreetingResponse {

  private String greeting;

  /**
   * Gets the {@code greeting} property.
   */
  public String getGreeting() {
    return greeting;
  }

  /**
   * Sets the {@code greeting} property.
   */
  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }
    
}
