# Database Schema Design: Smart Clinic System

This document outlines the polyglot persistence architecture for the Smart Clinic System, specifying the structured relational boundaries in MySQL and the flexible, document-oriented storage in MongoDB.

---

## MySQL Database Design

The relational database manages core operational data that requires absolute transactional consistency, strict relationship enforcement, and predictable, strongly typed structural schemas.

### Table: patients
- patient_id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Unique, Not Null
- phone_number: VARCHAR(15), Not Null
- date_of_birth: DATE, Not Null
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP
*Design Justification:* Email is set to `UNIQUE` to prevent duplicate account registration. A patient's data must be retained long-term for legal medical compliance; thus, deleting a patient should be highly restricted or handled via a soft-delete column rather than hard erasure.

### Table: doctors
- doctor_id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Unique, Not Null
- specialty: VARCHAR(100), Not Null
- phone_number: VARCHAR(15), Unique, Not Null
- is_active: BOOLEAN, Default True
*Design Justification:* The `is_active` flag handles deactivations. If a doctor leaves the clinic, we switch this to `False` rather than deleting the row, preserving all historical data linked via foreign keys.

### Table: appointments
- appointment_id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(doctor_id)
- patient_id: INT, Foreign Key → patients(patient_id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)
*Design Justification:* Foreign key constraints are set to `ON DELETE RESTRICT`. If a patient or doctor is deleted, the engine blocks the deletion until dependencies are resolved, preventing orphaned records. Overlapping appointments are blocked at the application/service level or via a composite database unique index `UNIQUE(doctor_id, appointment_time)`.

### Table: admin
- admin_id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Unique, Not Null
- password_hash: VARCHAR(255), Not Null
- email: VARCHAR(100), Unique, Not Null
- role_level: INT (0 = Super Admin, 1 = Support Staff)

---

## MongoDB Collection Design

Data that is highly polymorphic, semi-structured, or subject to dynamic schema evolution (such as complex prescription items or custom notes) is delegated to MongoDB to avoid bulky join tables or empty column sets.

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 10524,
  "patientId": 482,
  "doctorId": 14,
  "issuedDate": "2026-03-24T14:30:00Z",
  "diagnosis": {
    "primary": "Type 2 Diabetes Mellitus",
    "icd10Code": "E11.9",
    "doctorNotes": "Patient reports minor fatigue. Commencing standard therapeutic protocol."
  },
  "medications": [
    {
      "drugName": "Metformin Hydrochloride",
      "dosage": "500mg",
      "frequency": "Twice daily",
      "durationDays": 90,
      "instructions": "Take orally with meals (breakfast and dinner)."
    },
    {
      "drugName": "Atorvastatin",
      "dosage": "10mg",
      "frequency": "Once daily",
      "durationDays": 30,
      "instructions": "Take at bedtime."
    }
  ],
  "refillCount": 3,
  "pharmacyDetails": {
    "name": "Walgreens Pharmacy",
    "location": "Market Street",
    "electronicFulfillmentCode": "RX-99210-A"
  }
}
