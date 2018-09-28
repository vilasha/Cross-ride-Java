/**
 * 
 */
package com.crossover.techtrial.repositories;

import com.crossover.techtrial.model.Ride;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported = false)
public interface RideRepository extends CrudRepository<Ride, Long> {

}
