package com.project.back_end.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Assuming these are your correct packages, adjust if necessary:
import com.project.back_end.model.Appointment;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.GeneralService; // Assuming 'Service' refers to a general validation service

/**
 * REST Controller for managing Appointment entities.
 * Accessible via the base path "/appointments".
 */
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final GeneralService generalService; // Handles general tasks like token validation

    /**
     * Constructor injection for required dependencies.
     */
    public AppointmentController(AppointmentService appointmentService, GeneralService generalService) {
        this.appointmentService = appointmentService;
        this.generalService = generalService;
    }

    /**
     * 3. Get Appointments Method
     * Handles HTTP GET requests to fetch appointments based on date and patient name for a doctor.
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token) {

        // Validate token for "doctor" role
        if (!generalService.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired doctor token."));
        }

        // Extracted logic to fetch appointments via the service layer
        Long doctorId = generalService.extractIdFromToken(token);
        List<AppointmentDTO> appointments = appointmentService.getAppointments(doctorId, date, patientName);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 4. Book Appointment Method
     * Handles HTTP POST requests to create a new appointment for a patient.
     */
    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        // Validate token for "patient" role
        if (!generalService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired patient token."));
        }

        // Delegate to booking service
        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Appointment booked successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to book appointment. Invalid doctor ID or time conflict."));
        }
    }

    /**
     * 5. Update Appointment Method
     * Handles HTTP PUT requests to modify an existing appointment.
     */
    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        // Validate token for "patient" role
        if (!generalService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired patient token."));
        }

        // Delegate update logic to AppointmentService
        String resultMessage = appointmentService.updateAppointment(appointment);
        if (resultMessage.contains("successfully")) {
            return ResponseEntity.ok(Map.of("message", resultMessage));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", resultMessage));
        }
    }

    /**
     * 6. Cancel Appointment Method
     * Handles HTTP DELETE requests to cancel a specific appointment.
     */
    @DeleteMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        // Validate token for "patient" role
        if (!generalService.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid or expired patient token."));
        }

        Long patientId = generalService.extractIdFromToken(token);
        boolean isCanceled = appointmentService.cancelAppointment(appointmentId, patientId);
        
        if (isCanceled) {
            return ResponseEntity.ok(Map.of("message", "Appointment successfully canceled."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Unable to cancel appointment. Verification failed."));
        }
    }
}