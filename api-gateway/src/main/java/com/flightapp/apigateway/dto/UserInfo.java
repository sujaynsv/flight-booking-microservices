package com.flightapp.apigateway.dto;

public class UserInfo {
    private String email;
    private String firstName;
    private String lastName;
    private String token;
    private String role;
    
    public UserInfo() {
    }
    
    public UserInfo(String email, String firstName, String lastName, String token, String role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.token = token;
        this.role = role;
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
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
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
        private String email;
        private String firstName;
        private String lastName;
        private String token;
        private String role;
        
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
        
        public Builder token(String token) {
            this.token = token;
            return this;
        }
        
        public Builder role(String role) {
            this.role = role;
            return this;
        }
        
        public UserInfo build() {
            return new UserInfo(email, firstName, lastName, token, role);
        }
    }
}
