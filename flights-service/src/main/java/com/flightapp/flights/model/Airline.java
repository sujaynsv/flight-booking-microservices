package com.flightapp.flights.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "airlines")
public class Airline {
    
    @Id
    private String id;
    private String airlineName;
    private String airlineCode;
    private String contactEmail;
    private String contactPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Airline() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getAirlineName() {
        return airlineName;
    }
    
    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }
    
    public String getAirlineCode() {
        return airlineCode;
    }
    
    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
