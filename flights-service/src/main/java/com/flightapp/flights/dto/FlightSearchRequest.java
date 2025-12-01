package com.flightapp.flights.dto;

import java.time.LocalDate;

public class FlightSearchRequest {
    
    private String fromPlace;
    private String toPlace;
    private LocalDate departureDate;
    private String tripType; 
    
    public FlightSearchRequest() {
    }
    
    // Getters and Setters
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
    
    public LocalDate getDepartureDate() {
        return departureDate;
    }
    
    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }
    
    public String getTripType() {
        return tripType;
    }
    
    public void setTripType(String tripType) {
        this.tripType = tripType;
    }
}
