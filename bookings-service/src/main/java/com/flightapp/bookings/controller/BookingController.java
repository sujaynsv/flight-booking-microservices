package com.flightapp.bookings.controller;

import com.flightapp.bookings.dto.BookingRequest;
import com.flightapp.bookings.model.Booking;
import com.flightapp.bookings.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/flight")
public class BookingController {
    
    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @PostMapping("/booking/{flightId}")
    public Mono<ResponseEntity<Map<String, String>>> bookTicket(
            @PathVariable String flightId, 
            @RequestBody BookingRequest request) {
        return bookingService.createBooking(flightId, request)
                .map(booking -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Map.of("pnr", booking.getPnr())));
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
    public Mono<ResponseEntity<Void>> cancelBooking(@PathVariable String pnr) {
        return bookingService.cancelBooking(pnr)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }
}
