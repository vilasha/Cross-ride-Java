/**
 * 
 */
package com.crossover.techtrial.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.PersonRepository;
import com.crossover.techtrial.repositories.RideRepository;

@Service
public class RideServiceImpl implements RideService{

  @Autowired
  RideRepository rideRepository;

  @Autowired
  PersonRepository personRepository;
  
  public Ride save(Ride ride) {
    return rideRepository.save(ride);
  }
  
  public Ride findById(Long rideId) {
    Optional<Ride> optionalRide = rideRepository.findById(rideId);
    if (optionalRide != null && optionalRide.isPresent()) {
      return optionalRide.get();
    }else return null;
  }
  
  @Override
  public List<TopDriverDTO> getTopDrivers(Long count, LocalDateTime startRide, LocalDateTime endRide) {
	  
	List<TopDriverDTO> topDrivers =  new ArrayList<>();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	Set<Ride> rides = StreamSupport.stream(rideRepository.findAll().spliterator(), false)
			.filter(r -> LocalDateTime.parse(r.getStartTime(), formatter).compareTo(startRide) >= 0
			&& LocalDateTime.parse(r.getStartTime(), formatter).compareTo(endRide) <= 0
			&& LocalDateTime.parse(r.getEndTime(), formatter).compareTo(startRide) >= 0
			&& LocalDateTime.parse(r.getEndTime(), formatter).compareTo(endRide) <= 0)
			.collect(Collectors.toSet());
	Set<Person> drivers = StreamSupport.stream(personRepository.findAll().spliterator(), false)
			.collect(Collectors.toSet());
	
	for (Person driver : drivers) {
		Set<Ride> thisDriverRides = rides.stream()
				.filter(r -> r.getDriver().getId().equals(driver.getId()))
				.collect(Collectors.toSet());
		if (thisDriverRides.isEmpty())
			continue;
		
		Set<Ride> thisSharedRides = new HashSet<>();
		LocalDateTime start1, start2, end1, end2;
		
		for (Ride ride1 : thisDriverRides)
			for (Ride ride2 : thisDriverRides)
				if (ride1 != ride2) {
					
					start1 = LocalDateTime.parse(ride1.getStartTime(), formatter);
					end1 = LocalDateTime.parse(ride1.getEndTime(), formatter);
					start2 = LocalDateTime.parse(ride2.getStartTime(), formatter);
					end2 = LocalDateTime.parse(ride2.getEndTime(), formatter);
					
					if ((start1.isAfter(start2) && start1.isBefore(end2))
							|| (start2.isAfter(start1) && start2.isBefore(end1))
							|| (end1.isAfter(start2) && end1.isBefore(end2))
							|| (end2.isAfter(start1) && end2.isBefore(end1))) {
						thisSharedRides.add(ride1);
						thisSharedRides.add(ride2);
					}
				}
		if (thisSharedRides.isEmpty())
			continue;
		
		TopDriverDTO topDriver = new TopDriverDTO();
		topDriver.setName(driver.getName());
		topDriver.setEmail(driver.getEmail());
		double avgDistance = 0;
		long totalDuration = 0, maxDuration = 0, currentDuration = 0;
		
		for (Ride ride : thisSharedRides) {
			avgDistance += ride.getDistance();
			currentDuration = ChronoUnit.SECONDS.between(
					LocalDateTime.parse(ride.getStartTime(), formatter),
					LocalDateTime.parse(ride.getEndTime(), formatter));
			if (currentDuration > maxDuration)
				maxDuration = currentDuration;
			totalDuration += currentDuration;
		}
		avgDistance /= thisSharedRides.size();
		topDriver.setAverageDistance(avgDistance);
		topDriver.setMaxRideDurationInSecods(maxDuration);
		topDriver.setTotalRideDurationInSeconds(totalDuration);
		topDrivers.add(topDriver);
	}
	
	topDrivers.sort(new Comparator<TopDriverDTO>() {
		@Override
	    public int compare(TopDriverDTO d1, TopDriverDTO d2) {
	        return d1.getTotalRideDurationInSeconds()
	        		.compareTo(d2.getTotalRideDurationInSeconds());
	    }
	});
	
  	return topDrivers.subList(0, Math.min(topDrivers.size(), Math.toIntExact(count)));
  }

}
