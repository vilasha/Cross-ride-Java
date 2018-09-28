/**
 * 
 */
package com.crossover.techtrial.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.exceptions.GlobalExceptionHandler;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.service.PersonService;
import com.crossover.techtrial.service.RideService;

@RestController
public class RideController {
  
  @Autowired
  RideService rideService;

  @Autowired
  PersonService personService;

  @Autowired
  GlobalExceptionHandler exceptionHandler;

  @PostMapping(path ="/api/ride")
  public ResponseEntity<Ride> createNewRide(@RequestBody Ride ride) {
	  Long driver_id = ride.getDriver().getId();
	    Long rider_id = ride.getRider().getId();
	    if (personService.findById(driver_id) == null
	            || personService.findById(rider_id) == null) {
		      exceptionHandler.handle(
		              new Exception("Cross-Ride should only accept the data from registered drivers and riders only"));
		      return null;
	    }
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	    try {
	      Date startTime = formatter.parse(ride.getStartTime());
	      Date endTime = formatter.parse(ride.getEndTime());
	      if (startTime.compareTo(endTime) >= 0) {
		        exceptionHandler.handle(
		                new Exception("Not allowed to add a ride with end time less than or equal to start time"));
		        return null;
	      }
	    } catch (Exception ex) {
	      exceptionHandler.handle(ex);
	      return null;
	    }
    return ResponseEntity.ok(rideService.save(ride));
  }
  
  @GetMapping(path = "/api/ride/{ride-id}")
  public ResponseEntity<Ride> getRideById(@PathVariable(name="ride-id",required=true)Long rideId){
    Ride ride = rideService.findById(rideId);
    if (ride!=null)
      return ResponseEntity.ok(ride);
    return ResponseEntity.notFound().build();
  }
  
  /**
   * This API returns the top 5 drivers with their email,name, total minutes, maximum ride duration in minutes.
   * Only rides that starts and ends within the mentioned durations should be counted.
   * Any rides where either start or endtime is outside the search, should not be considered.
   * 
   * DONT CHANGE METHOD SIGNATURE AND RETURN TYPES
   * @return
   */
  @GetMapping(path = "/api/top-rides")
  public ResponseEntity<List<TopDriverDTO>> getTopDriver(
      @RequestParam(value="max", defaultValue="5") Long count,
      @RequestParam(value="startTime", required=true) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
      @RequestParam(value="endTime", required=true) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime){
    
	List<TopDriverDTO> topDrivers = rideService.getTopDrivers(count, startTime, endTime);
    return ResponseEntity.ok(topDrivers);
    
  }
  
}
