package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final Service generalService;   // Shared Service class

    // Constructor Injection
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService,
                              Service generalService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.generalService = generalService;
    }

    // ====================== BOOK APPOINTMENT ======================
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            // Validate doctor and patient exist
            if (appointment.getDoctor() == null || appointment.getPatient() == null) {
                return 0;
            }

            // Optional: Validate time slot availability via generalService
            int validation = generalService.validateAppointment(appointment.getDoctor().getId(), 
                                                              appointment.getAppointmentTime());
            if (validation != 1) {
                return 0;
            }

            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ====================== UPDATE APPOINTMENT ======================
    @Transactional
    public String updateAppointment(Appointment updatedAppointment, Long patientId) {
        try {
            Appointment existing = appointmentRepository.findById(updatedAppointment.getId()).orElse(null);
            if (existing == null) {
                return "Appointment not found";
            }

            // Security check: Ensure patient owns this appointment
            if (!existing.getPatient().getId().equals(patientId)) {
                return "Unauthorized: Patient ID mismatch";
            }

            // Update fields
            existing.setAppointmentTime(updatedAppointment.getAppointmentTime());
            existing.setStatus(updatedAppointment.getStatus());

            appointmentRepository.save(existing);
            return "Appointment updated successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error updating appointment";
        }
    }

    // ====================== CANCEL APPOINTMENT ======================
    @Transactional
    public String cancelAppointment(Long appointmentId, Long patientId) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
            if (appointment == null) {
                return "Appointment not found";
            }

            // Security check
            if (!appointment.getPatient().getId().equals(patientId)) {
                return "Unauthorized: Patient ID mismatch";
            }

            appointmentRepository.deleteById(appointmentId);
            return "Appointment cancelled successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error cancelling appointment";
        }
    }

    // ====================== GET APPOINTMENTS ======================
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointments(Long doctorId, String date, String patientName) {
        try {
            LocalDateTime start = LocalDateTime.parse(date + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(date + "T23:59:59");

            List<Appointment> appointments;

            if (patientName != null && !patientName.trim().isEmpty()) {
                appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                        doctorId, patientName, start, end);
            } else {
                appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
            }

            // Convert to DTO
            return appointments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ====================== CHANGE STATUS ======================
    @Transactional
    public void changeStatus(Long appointmentId, int status) {
        try {
            appointmentRepository.updateStatus(status, appointmentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ====================== HELPER METHOD ======================
    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
            appointment.getId(),
            appointment.getDoctor().getId(),
            appointment.getDoctor().getName(),
            appointment.getPatient().getId(),
            appointment.getPatient().getName(),
            appointment.getPatient().getEmail(),
            appointment.getPatient().getPhone(),
            appointment.getPatient().getAddress(),
            appointment.getAppointmentTime(),
            appointment.getStatus()
        );
    }
}