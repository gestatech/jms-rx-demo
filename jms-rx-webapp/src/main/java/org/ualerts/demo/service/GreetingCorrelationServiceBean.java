package org.ualerts.demo.service;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GreetingCorrelationServiceBean
    implements GreetingCorrelationService {

  private final Map<String, GreetingResponseHandler> handlerMap =
      new HashMap<String, GreetingResponseHandler>();
  
  @Override
  public void put(String id, GreetingResponseHandler handler) {
    handlerMap.put(id, handler);
  }

  @Override
  public GreetingResponseHandler take(String id) {
    return handlerMap.remove(id);
  }

}
