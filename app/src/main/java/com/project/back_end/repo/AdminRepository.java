package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// Assuming your Admin entity is located in this package, adjust if necessary:
import com.project.back_end.model.Admin; 

/**
 * Repository interface for Admin entity operations.
 * Extends JpaRepository to inherit standard CRUD, pagination, and sorting capabilities.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Custom query method to find an Admin by their username.
     * * @param username the username of the admin to look up
     * @return the Admin entity matching the provided username, or null if not found
     */
    Admin findByUsername(String username);
}