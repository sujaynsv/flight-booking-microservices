package com.flightapp.bookings.dto;

import java.time.LocalDateTime;

public class FlightDTO {
    
    private String id;
    private String airlineId;
    private String airlineName;
    private String flightNumber;
    private String fromPlace;
    private String toPlace;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private Double price;
    private Integer totalSeats;
    private Integer availableSeats;
    private String tripType;
    
    public FlightDTO() {
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getAirlineId() {
        return airlineId;
    }
    
    public void setAirlineId(String airlineId) {
        this.airlineId = airlineId;
    }
    
    public String getAirlineName() {
        return airlineName;
    }
    
    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public String getFromPlace() {
        return fromPlace;
    }
    
    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }
    
    public String getToPlace() {
        return toPlace;
    }
    
    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }
    
    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }
    
    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }
    
    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }
    
    public void setArrivalDateTime(LocalDateTime arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Integer getTotalSeats() {
        return totalSeats;
    }
    
    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
    
    public Integer getAvailableSeats() {
        return availableSeats;
    }
    
    public void setAvailableSeats(Integer availableSeats) {  // FIXED: Changed from String to Integer
        this.availableSeats = availableSeats;
    }
    
    public String getTripType() {
        return tripType;
    }
    
    public void setTripType(String tripType) {
        this.tripType = tripType;
    }
}
