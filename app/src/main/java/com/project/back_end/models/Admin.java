package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Admin {

    // 1. 'id' field: Primary Key with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. 'username' field: Required username for login
    @NotNull(message = "username cannot be null")

    private String username;

    // 3. 'password' field: Write-only for security, ensuring it is hidden in JSON outputs
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // 4. Constructor(s):
    // No-argument constructor required by JPA
    public Admin() {}

    // Parameterized constructor for easy entity instantiations
    public Admin(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // 5. Getters and Setters:
    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id;
    }

    public String getUsername() { 
        return username; 
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword(){ 
        return password; 
    }

    public void setPassword(String password) {
        this.password = password;
    }
}