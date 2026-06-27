package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // ==============================
    // SAVE PRESCRIPTION
    // ==============================
    public ResponseEntity<?> savePrescription(Prescription prescription) {
        try {

            // 1. Check if prescription already exists for appointment
            List<Prescription> existing =
                    prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());

            if (!existing.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Prescription already exists for this appointment");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 2. Save prescription
            Prescription saved = prescriptionRepository.save(prescription);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Prescription created successfully");
            response.put("data", saved);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error while saving prescription");

            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==============================
    // GET PRESCRIPTION BY APPOINTMENT
    // ==============================
    public ResponseEntity<?> getPrescription(Long appointmentId) {
        try {

            List<Prescription> prescriptions =
                    prescriptionRepository.findByAppointmentId(appointmentId);

            if (prescriptions.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No prescription found for this appointment");

                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", prescriptions);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error retrieving prescription");

            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}