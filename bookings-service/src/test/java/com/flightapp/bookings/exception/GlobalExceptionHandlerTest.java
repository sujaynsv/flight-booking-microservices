package com.flightapp.bookings.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleBookingNotFound() {
        BookingNotFoundException ex = new BookingNotFoundException("Not found");
        ResponseEntity<Map<String, Object>> response = handler.handleBookingNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().get("message"));
    }

    @Test
    void testHandleCancellationNotAllowed() {
        CancellationNotAllowedException ex =
                new CancellationNotAllowedException("Not allowed");
        ResponseEntity<Map<String, Object>> response = handler.handleCancellationNotAllowed(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Not allowed", response.getBody().get("message"));
    }

    @Test
    void testHandleValidationErrors() {
        BeanPropertyBindingResult result =
                new BeanPropertyBindingResult(new Object(), "bookingRequest");
        result.addError(new FieldError("bookingRequest", "email", "must not be blank"));

        WebExchangeBindException ex =
                new WebExchangeBindException(null, result);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be blank", response.getBody().get("email"));
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("boom");
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(((String) response.getBody().get("message")).contains("boom"));
    }
}
