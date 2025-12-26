package com.flightapp.apigateway.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastPasswordChange;
    
    // Account lockout fields
    private Integer failedLoginAttempts = 0;
    private LocalDateTime accountLockedUntil;
    private Boolean accountLocked = false;

    // Constructors
    public User() {
    }
    
    public User(String id, String email, String password, String firstName, 
                String lastName, String role, LocalDateTime createdAt, 
                LocalDateTime updatedAt, LocalDateTime lastPasswordChange,
                Integer failedLoginAttempts, LocalDateTime accountLockedUntil, 
                Boolean accountLocked) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastPasswordChange = lastPasswordChange;
        this.failedLoginAttempts = failedLoginAttempts;
        this.accountLockedUntil = accountLockedUntil;
        this.accountLocked = accountLocked;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public LocalDateTime getLastPasswordChange() {
        return lastPasswordChange;
    }
    
    public void setLastPasswordChange(LocalDateTime lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }
    
    // Account lockout getters and setters
    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    public LocalDateTime getAccountLockedUntil() {
        return accountLockedUntil;
    }
    
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) {
        this.accountLockedUntil = accountLockedUntil;
    }
    
    public Boolean getAccountLocked() {
        return accountLocked;
    }
    
    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }
    
    // Builder
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastPasswordChange;
        private Integer failedLoginAttempts = 0;
        private LocalDateTime accountLockedUntil;
        private Boolean accountLocked = false;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
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
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public Builder lastPasswordChange(LocalDateTime lastPasswordChange) {
            this.lastPasswordChange = lastPasswordChange;
            return this;
        }
        
        public Builder failedLoginAttempts(Integer failedLoginAttempts) {
            this.failedLoginAttempts = failedLoginAttempts;
            return this;
        }
        
        public Builder accountLockedUntil(LocalDateTime accountLockedUntil) {
            this.accountLockedUntil = accountLockedUntil;
            return this;
        }
        
        public Builder accountLocked(Boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }
        
        public User build() {
            return new User(
                id, 
                email, 
                password, 
                firstName, 
                lastName, 
                role, 
                createdAt, 
                updatedAt,
                lastPasswordChange,
                failedLoginAttempts,
                accountLockedUntil,
                accountLocked
            );
        }
    }
}
