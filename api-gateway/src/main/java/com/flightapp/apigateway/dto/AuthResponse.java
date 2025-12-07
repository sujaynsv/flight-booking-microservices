package com.flightapp.apigateway.dto;

public class AuthResponse {
    
    private String message;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    
    public AuthResponse() {
    }
    
    public AuthResponse(String message, String email, String firstName, String lastName, String role) {
        this.message = message;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String message;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        
        public Builder role(String role) {
            this.role = role;
            return this;
        }
        
        public AuthResponse build() {
            return new AuthResponse(message, email, firstName, lastName, role);
        }
    }
}
