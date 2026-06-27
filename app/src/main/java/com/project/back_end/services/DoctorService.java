package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1. Get all doctors
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // 2. Save doctor (register)
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // conflict
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0; // error
        }
    }

    // 3. Update doctor
    public int updateDoctor(Doctor doctor) {
        Optional<Doctor> existing = doctorRepository.findById(doctor.getId());

        if (existing.isEmpty()) {
            return -1;
        }

        doctorRepository.save(doctor);
        return 1;
    }

    // 4. Delete doctor + appointments
    public int deleteDoctor(Long doctorId) {
        try {
            if (!doctorRepository.existsById(doctorId)) {
                return -1;
            }

            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);

            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 5. Validate doctor login
    public String validateDoctor(String email, String password) {
        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            return null;
        }

        if (!doctor.getPassword().equals(password)) {
            return null;
        }

        return tokenService.generateToken(doctor.getEmail());
    }

    // 6. Get availability (core scheduling logic)
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);

        // Example logic placeholder (you will refine this)
        List<String> allSlots = List.of(
                "09:00", "10:00", "11:00",
                "14:00", "15:00", "16:00"
        );

        List<String> bookedSlots = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .toList();

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .toList();
    }

    // 7. Find doctors by name
    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameContainingIgnoreCase(name);
    }

    // 8. Filter by specialty
    @Transactional(readOnly = true)
    public List<Doctor> filterBySpecialty(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    // 9. Name + specialty filter
    @Transactional(readOnly = true)
    public List<Doctor> filterByNameAndSpecialty(String name, String specialty) {
        return doctorRepository.searchByNameAndSpecialty(name, specialty);
    }
}