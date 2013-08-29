package org.ualerts.demo.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;

@Stateless
public class GreetingRepositoryBean implements GreetingRepository {

  private final List<String> greetings = new ArrayList<String>();
  
  @PostConstruct
  public void init() {
    InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream("greetings.txt");
    if (inputStream == null) {
      throw new IllegalStateException("can't locate greetings resource");
    }
    try {
      greetings.addAll(IOUtils.readLines(inputStream, "UTF-8"));
    }
    catch (IOException ex) {
      throw new IllegalStateException("can't read greetings resource");
    }
  }
  
  @Override
  public String randomGreeting() {
    int i = (int) (Math.random() * greetings.size());
    return greetings.get(i);
  }

}
