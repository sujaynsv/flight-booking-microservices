package com.flightapp.bookings.dto;

import com.flightapp.bookings.model.Passenger;

import java.util.List;

public class BookingRequest {
    private String name;
    private String email;
    private Integer numberOfSeats;
    private List<Passenger> passengers;
    private List<String> seatNumbers;
    
    public BookingRequest() {
    }
    
    public BookingRequest(String name, String email, Integer numberOfSeats, List<Passenger> passengers, List<String> seatNumbers) {
        this.name = name;
        this.email = email;
        this.numberOfSeats = numberOfSeats;
        this.passengers = passengers;
        this.seatNumbers = seatNumbers;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
}
