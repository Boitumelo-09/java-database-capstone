package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;   // If needed for filtering

    // Constructor Injection
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   PatientService patientService,
                   DoctorService doctorService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    // ====================== TOKEN VALIDATION ======================
    public ResponseEntity<Map<String, Object>> validateToken(String token, String role) {
        try {
            boolean isValid = tokenService.validateToken(token, role);
            if (!isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            return null; // Token is valid - continue processing
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Token validation error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // ====================== ADMIN LOGIN ======================
    public ResponseEntity<Map<String, Object>> validateAdmin(Admin admin) {
        try {
            Admin foundAdmin = adminRepository.findByUsername(admin.getUsername());
            if (foundAdmin == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Admin not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!foundAdmin.getPassword().equals(admin.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(foundAdmin.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("role", "admin");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ====================== DOCTOR FILTERING ======================
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctor(String name, String time, String speciality) {
        // Delegate to DoctorService based on provided filters
        if ((name == null || name.trim().isEmpty()) &&
            (time == null || time.trim().isEmpty()) &&
            (speciality == null || speciality.trim().isEmpty())) {
            return doctorService.getDoctors();
        }

        // Complex filtering logic can be extended here
        return doctorService.filterDoctorsByNameSpecilityandTime(name, speciality, time);
    }

    // ====================== VALIDATE APPOINTMENT TIME ======================
    public int validateAppointment(Long doctorId, LocalDateTime appointmentTime) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
            if (doctor == null) {
                return -1; // Doctor not found
            }

            // This logic should ideally be in DoctorService.getDoctorAvailability()
            // Simplified version:
            return 1; // Assume valid for now (extend with real availability check)
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== VALIDATE PATIENT UNIQUENESS ======================
    public boolean validatePatient(String email, String phone) {
        try {
            Patient existing = patientRepository.findByEmailOrPhone(email, phone);
            return existing == null; // true = valid (no duplicate)
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== PATIENT LOGIN ======================
    public ResponseEntity<Map<String, Object>> validatePatientLogin(String email, String password) {
        try {
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!patient.getPassword().equals(password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("role", "patient");
            response.put("patientId", patient.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ====================== FILTER PATIENT APPOINTMENTS ======================
    public List<AppointmentDTO> filterPatient(String token, String condition, String doctorName) {
        try {
            // Extract email from token
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                return List.of();
            }

            // Delegate to PatientService
            return patientService.filterByConditionAndDoctor(patient.getId(), condition, doctorName);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}