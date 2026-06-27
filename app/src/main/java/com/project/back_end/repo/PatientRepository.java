package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// Assuming your Patient entity is located in this package, adjust if necessary:
import com.project.back_end.model.Patient;

/**
 * Repository interface for Patient entity operations.
 * Extends JpaRepository to inherit standard CRUD, pagination, and sorting capabilities.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Custom query method to find a Patient by their email address.
     * * @param email the email address of the patient
     * @return the Patient entity matching the provided email, or null if not found
     */
    Patient findByEmail(String email);

    /**
     * Custom query method to find a Patient by either their email address or phone number.
     * * @param email the email address of the patient
     * @param phone the phone number of the patient
     * @return the Patient entity matching either criteria, or null if not found
     */
    Patient findByEmailOrPhone(String email, String phone);
}