package com.flightapp.flights.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "flights")
public class Flight {
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

	public String getAirlineLogo() {
		return airlineLogo;
	}

	public void setAirlineLogo(String airlineLogo) {
		this.airlineLogo = airlineLogo;
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

	public void setAvailableSeats(Integer availableSeats) {
		this.availableSeats = availableSeats;
	}

	public List<String> getBookedSeats() {
		return bookedSeats;
	}

	public void setBookedSeats(List<String> bookedSeats) {
		this.bookedSeats = bookedSeats;
	}

	public TripType getTripType() {
		return tripType;
	}

	public void setTripType(TripType tripType) {
		this.tripType = tripType;
	}
	
	public Flight() {
		
	}
	
	public Flight(String id, @NotBlank(message = "Airline name is required") String airlineName, String airlineLogo,
			@NotBlank(message = "From place is required") String fromPlace,
			@NotBlank(message = "To place is required") String toPlace,
			@NotNull(message = "Departure date time is required") LocalDateTime departureDateTime,
			@NotNull(message = "Arrival date time is required") LocalDateTime arrivalDateTime,
			@NotNull(message = "Price is required") @Positive(message = "Price must be positive") Double price,
			@NotNull(message = "Total seats is required") @Positive(message = "Total seats must be positive") Integer totalSeats,
			Integer availableSeats, List<String> bookedSeats,
			@NotNull(message = "Trip type is required") TripType tripType) {
		super();
		this.id = id;
		this.airlineName = airlineName;
		this.airlineLogo = airlineLogo;
		this.fromPlace = fromPlace;
		this.toPlace = toPlace;
		this.departureDateTime = departureDateTime;
		this.arrivalDateTime = arrivalDateTime;
		this.price = price;
		this.totalSeats = totalSeats;
		this.availableSeats = availableSeats;
		this.bookedSeats = bookedSeats;
		this.tripType = tripType;
	}

	@Id
    private String id;
    
    @NotBlank(message = "Airline name is required")
    private String airlineName;
    
    private String airlineLogo;
    
    @NotBlank(message = "From place is required")
    private String fromPlace;
    
    @NotBlank(message = "To place is required")
    private String toPlace;
    
    @NotNull(message = "Departure date time is required")
    private LocalDateTime departureDateTime;
    
    @NotNull(message = "Arrival date time is required")
    private LocalDateTime arrivalDateTime;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
    
    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be positive")
    private Integer totalSeats;
    
    private Integer availableSeats;
    
    private List<String> bookedSeats = new ArrayList<>();
    
    @NotNull(message = "Trip type is required")
    private TripType tripType;
    
    public enum TripType {
        ONE_WAY, ROUND_TRIP
    }
}
