/**
 * 
 */
package com.crossover.techtrial.service;

import java.util.List;
import com.crossover.techtrial.model.Person;

public interface PersonService {
  public List<Person> getAll();
  
  public Person save(Person p);
  
  public Person findById(Long personId);
  
}
