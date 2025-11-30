package com.flightapp.bookings.controller;

import com.flightapp.bookings.dto.BookingRequest;
import com.flightapp.bookings.model.Booking;
import com.flightapp.bookings.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
public class BookingController {
    
    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @PostMapping("/booking/{flightId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Booking> bookTicket(@PathVariable String flightId, 
                                     @RequestBody BookingRequest request) {
        return bookingService.createBooking(flightId, request);
    }
    
    @GetMapping("/ticket/{pnr}")
    public Mono<Booking> getTicketByPnr(@PathVariable String pnr) {
        return bookingService.getBookingByPnr(pnr);
    }
    
    @GetMapping("/booking/history/{emailId}")
    public Flux<Booking> getBookingHistory(@PathVariable String emailId) {
        return bookingService.getBookingHistory(emailId);
    }
    
    @DeleteMapping("/booking/cancel/{pnr}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> cancelBooking(@PathVariable String pnr) {
        return bookingService.cancelBooking(pnr);
    }
}
