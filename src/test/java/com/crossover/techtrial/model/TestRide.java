package com.crossover.techtrial.model;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestRide {
    public static Ride ride;
    public static Person person;

    @BeforeClass
    public static void init(){
        ride = new Ride();
        person = new Person();
    }

    @Test
    public void testGetId(){
        Long id = new Random().nextLong();
        ride.setId(id);
        assertEquals(ride.getId(), id);
    }

    @Test
    public void testDistance(){
        Long distance = new Random().nextLong();
        ride.setDistance(distance);
        assertEquals(ride.getDistance(), distance);
    }

    @Test
    public void testDriver(){
        ride.setDriver(person);
        assertEquals(ride.getDriver(), person);
    }

    @Test
    public void testRider(){
        ride.setRider(person);
        assertEquals(ride.getRider(), person);
    }
}
