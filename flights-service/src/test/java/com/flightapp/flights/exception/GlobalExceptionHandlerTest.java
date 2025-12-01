package com.flightapp.flights.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Test error");
        
        ResponseEntity<String> response = handler.handleRuntimeException(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test error", response.getBody());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Generic error");
        
        ResponseEntity<String> response = handler.handleException(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Generic error"));
    }
}
