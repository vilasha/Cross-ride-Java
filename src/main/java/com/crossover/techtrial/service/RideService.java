/**
 * 
 */
package com.crossover.techtrial.service;

import java.time.LocalDateTime;
import java.util.List;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Ride;

public interface RideService {
  
  public Ride save(Ride ride);
  
  public Ride findById(Long rideId);
  
  public List<TopDriverDTO> getTopDrivers(Long count, LocalDateTime startRide, LocalDateTime endRide);
  
}