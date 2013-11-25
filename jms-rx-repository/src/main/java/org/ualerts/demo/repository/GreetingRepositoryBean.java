package org.ualerts.demo.repository;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GreetingRepositoryBean implements GreetingRepository {

  @Inject
  private GreetingRepository delegate;
  
  @Override
  public String randomGreeting() {
    return delegate.randomGreeting();
  }

}
