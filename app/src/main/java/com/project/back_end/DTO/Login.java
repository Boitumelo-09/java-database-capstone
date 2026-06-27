package com.project.back_end.DTO;

/**
 * Data Transfer Object representing login request data.
 * This class captures the user's login credentials from the client side.
 */
public class Login {
    
    // 1. 'identifier' field (e.g., email for Doctor/Patient, username for Admin)
    private String identifier;
    
    // 2. 'password' field
    private String password;

    // 3. Constructor
    // Relies on the default no-argument constructor implicitly provided by Java.

    // 4. Getters and Setters

    /**
     * Gets the unique identifier (email or username).
     * @return the login identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the unique identifier (email or username).
     * @param identifier the login identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the login password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the login password.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}