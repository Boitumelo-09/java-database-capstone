package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 1. Find doctor by email (login / validation)
    Doctor findByEmail(String email);

    // 2. Search doctors by partial name (case-insensitive)
    List<Doctor> findByNameContainingIgnoreCase(String name);

    // 3. Filter by specialty (case-insensitive)
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

    // 4. Combined search (cleaner alternative using @Query)
    @Query("""
        SELECT d FROM Doctor d
        WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
        AND LOWER(d.specialty) = LOWER(:specialty)
    """)
    List<Doctor> searchByNameAndSpecialty(String name, String specialty);
}