package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service generalService;   // Shared Service

    // Constructor Injection
    public DoctorController(DoctorService doctorService, Service generalService) {
        this.doctorService = doctorService;
        this.generalService = generalService;
    }

    // ====================== GET DOCTOR AVAILABILITY ======================
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validation = generalService.validateToken(token, user);
        if (validation != null) {
            return validation;
        }

        try {
            Map<String, Object> response = new HashMap<>();
            // Delegate to DoctorService (assumes it returns availability slots)
            var availability = doctorService.getDoctorAvailability(doctorId, date);
            response.put("availability", availability);
            response.put("message", "Availability fetched successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching availability");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ====================== GET ALL DOCTORS ======================
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        try {
            List<Doctor> doctors = doctorService.getDoctors();
            Map<String, Object> response = new HashMap<>();
            response.put("doctors", doctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching doctors");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ====================== REGISTER NEW DOCTOR ======================
    @PostMapping("/register/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validation = generalService.validateToken(token, "admin");
        if (validation != null) {
            return validation;
        }

        int result = doctorService.saveDoctor(doctor);

        Map<String, Object> response = new HashMap<>();
        if (result == 1) {
            response.put("message", "Doctor registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (result == -1) {
            response.put("message", "Doctor with this email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            response.put("message", "Error registering doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ====================== DOCTOR LOGIN ======================
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@RequestBody com.project.back_end.DTO.Login login) {
        return doctorService.validateDoctor(login.getIdentifier(), login.getPassword());
    }

    // ====================== UPDATE DOCTOR ======================
    @PutMapping("/update/{token}")
    public ResponseEntity<Map<String, Object>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validation = generalService.validateToken(token, "admin");
        if (validation != null) {
            return validation;
        }

        String result = doctorService.updateDoctor(doctor);

        Map<String, Object> response = new HashMap<>();
        response.put("message", result);
        return result.contains("successfully") 
                ? ResponseEntity.ok(response) 
                : ResponseEntity.badRequest().body(response);
    }

    // ====================== DELETE DOCTOR ======================
    @DeleteMapping("/delete/{doctorId}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(
            @PathVariable Long doctorId,
            @PathVariable String token) {

        ResponseEntity<Map<String, Object>> validation = generalService.validateToken(token, "admin");
        if (validation != null) {
            return validation;
        }

        String result = doctorService.deleteDoctor(doctorId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", result);
        return result.contains("successfully") 
                ? ResponseEntity.ok(response) 
                : ResponseEntity.badRequest().body(response);
    }

    // ====================== FILTER DOCTORS ======================
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        try {
            List<Doctor> filteredDoctors = generalService.filterDoctor(name, time, speciality);

            Map<String, Object> response = new HashMap<>();
            response.put("doctors", filteredDoctors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error filtering doctors");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}