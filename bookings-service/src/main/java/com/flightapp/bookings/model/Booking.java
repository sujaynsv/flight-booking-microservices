package com.flightapp.bookings.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;
    private String pnr;
    private String flightId;
//    public ZonedDateTime getJourneyDateTime() {
//		return journeyDateTime;
//	}
//
//	public void setJourneyDateTime(ZonedDateTime journeyDateTime) {
//		this.journeyDateTime = journeyDateTime;
//	}

//	private ZonedDateTime journeyDateTime;
    
    private String email;
    private String name;
    private Integer numberOfSeats;
    private List<Passenger> passengers;
    private List<String> seatNumbers;
    private Double totalPrice;
    private BookingStatus status;
    private LocalDateTime bookingDateTime;
    private LocalDateTime journeyDate;
    
    public enum BookingStatus {
        CONFIRMED, CANCELLED
    }
    
    // Constructors
    public Booking() {
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPnr() {
        return pnr;
    }
    
    public void setPnr(String pnr) {
        this.pnr = pnr;
    }
    
    public String getFlightId() {
        return flightId;
    }
    
    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }
    
    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }
    
    public List<Passenger> getPassengers() {
        return passengers;
    }
    
    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
    
    public List<String> getSeatNumbers() {
        return seatNumbers;
    }
    
    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }
    
	public void setBookingDateTime(LocalDateTime bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }
    
    public LocalDateTime getJourneyDate() {
        return journeyDate;
    }
    
    public void setJourneyDate(LocalDateTime journeyDate) {
        this.journeyDate = journeyDate;
    }
}
