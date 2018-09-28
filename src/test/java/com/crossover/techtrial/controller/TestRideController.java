package com.crossover.techtrial.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.crossover.techtrial.exceptions.GlobalExceptionHandler;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.service.PersonService;
import com.crossover.techtrial.service.RideService;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TestRideController {

	@Mock
	private PersonService personService;

	@Mock
	private RideService rideService;
	
	@Mock
	private GlobalExceptionHandler exceptionHandler;
	
	@InjectMocks
	static RideController controller;
	
	Person mockDriver, mockRider;
	Ride mockRide;
	DateTimeFormatter formatter;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

		mockDriver = new Person();
		mockDriver.setId(1L);
		mockDriver.setName("John");
		mockDriver.setEmail("john@gmail.com");
		mockDriver.setRegistrationNumber("E246XV");
		
		mockRider = new Person();
		mockRider.setId(2L);
		mockRider.setName("Olly");
		mockRider.setEmail("oliver@gmail.com");
		
		mockRide = new Ride();
		mockRide.setId(3L);
		mockRide.setDriver(mockDriver);
		mockRide.setRider(mockRider);
		mockRide.setStartTime(formatter.format(LocalDateTime.of(2018, 9, 12, 12, 0, 0)));
		mockRide.setEndTime(formatter.format(LocalDateTime.of(2018, 9, 12, 14, 30, 0)));
		mockRide.setDistance(50L);
	}
	
	@Test
	public void createNewRideCorrect() {
		when(personService.findById(Mockito.any(Long.class))).thenReturn(mockDriver);
		when(rideService.save(Mockito.any(Ride.class))).thenReturn(mockRide);
		
		ResponseEntity<Ride> response = controller.createNewRide(mockRide);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void createNewRideUnregisteredDriver() {
		when(personService.findById(Mockito.any(Long.class))).thenReturn(null);
		ResponseEntity<Ride> response = controller.createNewRide(mockRide);
		assertEquals(null, response);
	}

	@Test
	public void createNewRideEndTimeLessThanStartTime() {
		when(personService.findById(Mockito.any(Long.class))).thenReturn(mockDriver);
		mockRide.setEndTime(formatter.format(LocalDateTime.of(2018, 9, 12, 10, 30, 0)));
		ResponseEntity<Ride> response = controller.createNewRide(mockRide);
		assertEquals(null, response);
	}
}
