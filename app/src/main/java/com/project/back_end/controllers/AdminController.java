package com.project.back_end.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

// Assuming these are your correct model and service packages, adjust if necessary:
import com.project.back_end.model.Admin;
import com.project.back_end.services.AdminService; 

/**
 * REST Controller for managing Admin authentication and administrative operations.
 * Base path dynamically resolves using the application property placeholder.
 */
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * Constructor injection for AdminService.
     * Spring automatically autowires this dependency when instantiating the controller.
     * * @param adminService the business logic service layer for Admin operations
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Handles HTTP POST requests for admin login functionality.
     * * @param admin the Admin entity containing login credentials in the request body
     * @return a ResponseEntity containing a map with the login status, token, or error messages
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody Admin admin) {
        // Delegate authentication logic to the service layer
        Map<String, Object> response = adminService.validateAdmin(admin);
        
        // Return the map wrapped in a ResponseEntity
        // Note: You can customize the status code based on service response if needed (e.g., HttpStatus.UNAUTHORIZED)
        return ResponseEntity.ok(response);
    }
}