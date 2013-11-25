package org.ualerts.demo.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@ApplicationScoped
public class JpaGreetingRepository implements GreetingRepository {

  @PersistenceContext
  private EntityManager entityManager;
  
  @Override
  public String randomGreeting() {
    TypedQuery<GreetingTemplate> query = entityManager.createQuery(
        "select distinct t from GreetingTemplate t order by t.id", 
        GreetingTemplate.class);
    List<GreetingTemplate> templates = query.getResultList();
    if (templates.size() == 0) {
      return "Hello, %s.";
    }
    return templates.get((int)(Math.random() * templates.size())).getText();
  }

}
