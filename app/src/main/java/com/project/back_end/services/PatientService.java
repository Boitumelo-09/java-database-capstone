package com.project.back_end.services;

import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public List<?> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public Object findDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    @Transactional
    public int saveDoctor(Object doctor) {
        try {
            // pseudo validation
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Object doctor) {
        return doctorRepository.existsById(1L) ? 1 : -1;
    }

    @Transactional
    public int deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) return -1;

        appointmentRepository.deleteAllByDoctorId(id);
        doctorRepository.deleteById(id);
        return 1;
    }
}