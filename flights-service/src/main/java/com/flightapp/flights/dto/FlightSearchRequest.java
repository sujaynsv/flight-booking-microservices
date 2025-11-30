package com.flightapp.flights.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class FlightSearchRequest {
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

	public FlightSearchRequest(@NotBlank(message = "From place is required") String fromPlace,
			@NotBlank(message = "To place is required") String toPlace,
			@NotNull(message = "Departure date is required") LocalDate departureDate) {
		super();
		this.fromPlace = fromPlace;
		this.toPlace = toPlace;
		this.departureDate = departureDate;
	}

	@NotBlank(message = "From place is required")
    private String fromPlace;
    
    @NotBlank(message = "To place is required")
    private String toPlace;
    
    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;
}
