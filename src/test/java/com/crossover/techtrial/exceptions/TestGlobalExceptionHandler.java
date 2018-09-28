package com.crossover.techtrial.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.AbstractMap;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TestGlobalExceptionHandler {
	
	@Test
	public void testException() {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();
		try {
			ResponseEntity<AbstractMap.SimpleEntry<String, String>> response =
					handler.handle(new NullPointerException("Test exception"));
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		} catch(Exception ex) {
			fail("Didn't catch the exception");
		}
	}

}
