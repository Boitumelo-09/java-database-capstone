package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service generalService;

    // Constructor Injection
    public PatientController(PatientService patientService, Service generalService) {
        this.patientService = patientService;
        this.generalService = generalService;
    }

    // ====================== GET PATIENT DETAILS ======================
    @GetMapping("/me/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, Object>> validation = generalService.validateToken(token, "patient");
        if (validation != null) {
            return validation;
        }

        try {
            Map<String, Object> response = patientService.getPatientDetails(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching patient details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ====================== REGISTER NEW PATIENT ======================
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> createPatient(@RequestBody Patient patient) {
        try {
            // Check if patient already exists
            boolean isValid = generalService.validatePatient(patient.getEmail(), patient.getPhone());
            if (!isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Patient with this email or phone already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            int result = patientService.createPatient(patient);

            Map<String, Object> response = new HashMap<>();
            if (result == 1) {
                response.put("message", "Patient registered successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("message", "Error registering patient");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ====================== PATIENT LOGIN ======================
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Login login) {
        return generalService.validatePatientLogin(login.getIdentifier(), login.getPassword());
    }

    // ====================== GET PATIENT APPOINTMENTS ======================
    @GetMapping("/appointments/{patientId}/{token}/{user}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long patientId,
            @PathVariable String token,
            @PathVariable String user) {

        ResponseEntity<Map<String, Object>> validation = generalService.validateToken(token, user);
        if (validation != null) {
            return validation;
        }

        try {
            List<AppointmentDTO> appointments = patientService.getPatientAppointment(patientId);

            Map<String, Object> response = new HashMap<>();
            response.put("appointments", appointments);
            response.put("message", "Appointments fetched successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ====================== FILTER PATIENT APPOINTMENTS ======================
    @GetMapping("/appointments/filter")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @RequestParam String condition,
            @RequestParam(required = false) String name,
            @RequestParam String token) {

        ResponseEntity<Map<String, Object>> validation = generalService.validateToken(token, "patient");
        if (validation != null) {
            return validation;
        }

        try {
            List<AppointmentDTO> filteredAppointments = generalService.filterPatient(token, condition, name);

            Map<String, Object> response = new HashMap<>();
            response.put("appointments", filteredAppointments);
            response.put("message", "Filtered appointments returned successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}