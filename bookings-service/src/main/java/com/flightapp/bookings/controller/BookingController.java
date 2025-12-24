package com.flightapp.bookings.controller;

import com.flightapp.bookings.dto.BookingRequest;
import com.flightapp.bookings.model.Booking;
import com.flightapp.bookings.service.BookingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flight")
public class BookingController {
    
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @PostMapping("/booking/{flightId}")
    public Mono<ResponseEntity<Map<String, String>>> bookTicket(
            @PathVariable String flightId, 
            @RequestBody BookingRequest request) {

                
        // return bookingService.createBooking(flightId, request)
        //         .map(booking -> ResponseEntity
        //                 .status(HttpStatus.CREATED)
        //                 .body(Map.of("pnr", booking.getPnr())));

            if(request.hasDuplicateSeats()){
                return Mono.just(ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Duplicate Seat numbers are not allowed."))
                );
            }

            if(request.getSeatNumbers()!=null && request.getSeatNumbers().size()!=request.getNumberOfSeats()){
                return Mono.just(ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Number of seat Numbers must match number of seats"))
                );
            }

            return bookingService.createBooking(flightId, request)
                    .map(booking->ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(Map.of("pnr", booking.getPnr()))
                    )
                .onErrorResume(e->{
                    return Mono.just(ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", e.getMessage()))
                    );
                });
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
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    // Add this endpoint to your existing BookingController

    @GetMapping("/booked-seats/{flightId}")
    public Mono<List<String>> getBookedSeats(@PathVariable String flightId) {
        log.info("Request to get booked seats for flight: {}", flightId);
        return bookingService.getBookedSeatsByFlight(flightId);
    }

}
