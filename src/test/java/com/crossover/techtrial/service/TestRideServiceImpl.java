package com.crossover.techtrial.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.PersonRepository;
import com.crossover.techtrial.repositories.RideRepository;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TestRideServiceImpl {
	
	@Mock
	private RideRepository mockRepoRide;
	
	@Mock
	private PersonRepository mockRepoPerson;
	
	@InjectMocks
	static RideServiceImpl service;
	
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
	public void findExistingRideById() {
		when(mockRepoRide.findById(3L)).thenReturn(Optional.of(mockRide));
		Ride ride = service.findById(3L);
		assertNotNull(ride);
	}
	
	@Test
	public void findInexistingRideById() {
		when(mockRepoRide.findById(99L)).thenReturn(null);
		Ride ride = service.findById(99L);
		assertNull(ride);
	}
	
	@Test
	public void saveRide() {
		when(mockRepoRide.save(Mockito.any(Ride.class))).thenReturn(mockRide);
		Ride ride = service.save(mockRide);
		assertEquals("John", ride.getDriver().getName());
	}
	
	@Test
	public void getTopDriversWithSharingRides() {
		List<Person> drivers = new ArrayList<>();
		List<TopDriverDTO> topDrivers = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Person driver = new Person();
			driver.setId(Long.valueOf(i));
			driver.setName(generateString(5));
			drivers.add(driver);
			TopDriverDTO topDriver = new TopDriverDTO();
			topDriver.setName(driver.getName());
			topDriver.setTotalRideDurationInSeconds(Long.valueOf(2*2*60*60));
			topDriver.setMaxRideDurationInSecods(Long.valueOf(2*60*60));
			topDriver.setAverageDistance(0d);
			topDrivers.add(topDriver);
		}
		
		List<Ride> rides = new ArrayList<>();
		Random random = new Random();
				
		for (int i = 0; i < 10; i++) {
			Ride ride = new Ride();
			ride.setId(Long.valueOf(i));
			ride.setDriver(drivers.get(i / 2));
			ride.setDistance(random.nextLong());
			topDrivers.get(i / 2).setAverageDistance(
					topDrivers.get(i / 2).getAverageDistance() + 
					ride.getDistance() / 2);
			ride.setStartTime(formatter.format(LocalDateTime.of(2018, 9, 12, i, 0, 0)));
			ride.setEndTime(formatter.format(LocalDateTime.of(2018, 9, 12, i+2, 0, 0)));
			rides.add(ride);
		}
		when(mockRepoRide.findAll()).thenReturn(rides);
		when(mockRepoPerson.findAll()).thenReturn(drivers);
		
		List<TopDriverDTO> result = service.getTopDrivers(5L,
				LocalDateTime.of(2018, 8, 1, 0, 0, 0),
				LocalDateTime.of(2019, 8, 1, 0, 0, 0));
		assertEquals(topDrivers.size(), result.size());
		assertEquals(topDrivers.get(0).getMaxRideDurationInSecods(),
				result.get(0).getMaxRideDurationInSecods());
	}
	
	@Test
	public void getTopDriversAllRidesUnique() {
		List<Person> drivers = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Person driver = new Person();
			driver.setId(Long.valueOf(i));
			driver.setName(generateString(5));
			drivers.add(driver);
		}
		
		List<Ride> rides = new ArrayList<>();
		Random random = new Random();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
				
		for (int i = 0; i < 10; i++) {
			Ride ride = new Ride();
			ride.setId(Long.valueOf(i));
			ride.setDriver(drivers.get(i / 2));
			ride.setDistance(random.nextLong());
			ride.setStartTime(formatter.format(LocalDateTime.of(2018, 9, 12, i, 0, 0)));
			ride.setEndTime(formatter.format(LocalDateTime.of(2018, 9, 12, i, 30, 0)));
			rides.add(ride);
		}
		when(mockRepoRide.findAll()).thenReturn(rides);
		when(mockRepoPerson.findAll()).thenReturn(drivers);
		
		List<TopDriverDTO> result = service.getTopDrivers(5L,
				LocalDateTime.of(2018, 8, 1, 0, 0, 0),
				LocalDateTime.of(2019, 8, 1, 0, 0, 0));
		assertEquals(0, result.size());
	}
	
	private String generateString(int len) {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(len);
	    for (int i = 0; i < len; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    return buffer.toString();
	}
}
