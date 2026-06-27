package com.project.back_end.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
// Assuming your Prescription entity is located in this package, adjust if necessary:
import com.project.back_end.model.Prescription;

/**
 * Repository interface for Prescription document operations in MongoDB.
 * Extends MongoRepository to inherit standard NoSQL CRUD capabilities.
 */
@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Custom query method to find a list of prescriptions by their associated appointment ID.
     * Spring Data MongoDB automatically derives the query from the method name.
     * * @param appointmentId the ID of the appointment
     * @return a list of Prescription documents matching the provided appointment ID
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}