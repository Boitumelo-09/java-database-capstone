package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @Entity annotation:
 * - Marks the class as a JPA entity, meaning it represents a table in the database.
 */
@Entity
public class Appointment {

    // 1. 'id' field
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. 'doctor' field
    @NotNull(message = "Doctor cannot be null")
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // 3. 'patient' field
    @NotNull(message = "Patient cannot be null")
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // 4. 'appointmentTime' field
    @NotNull(message = "Appointment time cannot be null")
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

    // 5. 'status' field (0 = Scheduled, 1 = Completed)
    @NotNull
    private int status;

    // Constructors
    public Appointment() {
    }

    public Appointment(Long id, Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // 6. 'getEndTime' method
    // @Transient ensures this dynamic calculation isn't mapped to a DB table column
    @Transient
    public LocalDateTime getEndTime() {
        if (this.appointmentTime == null) {
            return null;
        }
        return this.appointmentTime.plusHours(1);
    }

    // 7. 'getAppointmentDate' method
    // @Transient ensures this date extraction helper isn't persisted in the database
    @Transient
    public LocalDate getAppointmentDate() {
        if (this.appointmentTime == null) {
            return null;
        }
        return this.appointmentTime.toLocalDate();
    }

    // 8. 'getAppointmentTimeOnly' method
    // @Transient ensures this time extraction helper isn't persisted in the database
    @Transient
    public LocalTime getAppointmentTimeOnly() {
        if (this.appointmentTime == null) {
            return null;
        }
        return this.appointmentTime.toLocalTime();
    }

    // Standard Getters and Setters for fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}