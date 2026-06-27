package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final AppointmentService appointmentService;
    private final Service generalService;

    // Constructor Injection
    public PrescriptionController(PrescriptionService prescriptionService,
                                  AppointmentService appointmentService,
                                  Service generalService) {
        this.prescriptionService = prescriptionService;
        this.appointmentService = appointmentService;
        this.generalService = generalService;
    }

    // ====================== SAVE / CREATE PRESCRIPTION ======================
    @PostMapping("/save/{token}")
    public ResponseEntity<Map<String, Object>> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable String token) {

        // Validate doctor token
        ResponseEntity<Map<String, Object>> validation = generalService.validateToken(token, "doctor");
        if (validation != null) {
            return validation;
        }

        try {
            // Update appointment status to indicate prescription issued (e.g., status = 1 for completed)
            if (prescription.getAppointmentId() != null) {
                appointmentService.changeStatus(prescription.getAppointmentId(), 1);
            }

            // Save prescription via service
            ResponseEntity<Map<String, Object>> result = prescriptionService.savePrescription(prescription);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error saving prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ====================== GET PRESCRIPTION BY APPOINTMENT ======================
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        // Validate doctor token
        ResponseEntity<Map<String, Object>> validation = generalService.validateToken(token, "doctor");
        if (validation != null) {
            return validation;
        }

        try {
            return prescriptionService.getPrescription(appointmentId);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error fetching prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}