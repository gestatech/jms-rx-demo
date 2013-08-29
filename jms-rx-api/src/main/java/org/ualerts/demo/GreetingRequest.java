package org.ualerts.demo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GreetingRequest {

  private String name;

  /**
   * Gets the {@code name} property.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the {@code name} property.
   */
  public void setName(String name) {
    this.name = name;
  }
  
}
