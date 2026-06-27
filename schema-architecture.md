# System Architecture & Data Flow Documentation

## Section 1: Architecture Summary

This healthcare management system is built on a robust Spring Boot foundation, employing a hybrid architectural model that integrates both traditional Model-View-Controller (MVC) paradigms and modern Representational State Transfer (REST) APIs. To deliver a tailored experience for different roles, the system leverages Thymeleaf templates to render the comprehensive, server-side Admin and Doctor dashboards, ensuring strict security and session integrity. Conversely, the high-frequency Patient operations, appointment booking engines, and cross-module communications are served via a decoupled REST API framework. 

At the data tier, the application employs a polyglot persistence strategy designed to match database capabilities with business domain characteristics. Structured, relational domain models—including administrative details, doctor registries, patient profiles, and appointment schedules—are handled by a MySQL database using the Java Persistence API (JPA) for robust transaction management and relational integrity. Unstructured, high-volume data such as medical records and prescriptions are routed to a MongoDB instance, utilizing dynamic document models for maximum flexibility. All incoming HTTP requests travel through a unified, centralized service layer where business rules, access validations, and validation logic are strictly enforced before delegating operations down to their respective MySQL JPA repositories or MongoDB document repositories.

---

## Section 2: Numbered Flow of Data and Control

1. **User Interaction Initations:** An end-user (Admin, Doctor, or Patient) interacts with the system interface by accessing either the server-rendered Thymeleaf dashboards or firing client-side actions from an appointment portal.
2. **Controller Routing:** The HTTP request arrives at the Spring Boot application context, where the internal dispatcher routes it to the designated Thymeleaf MVC Controller (for dashboard rendering) or the REST API Controller (for data payloads).
3. **Service Layer Delegation:** The controller validates the incoming web payload and hands off execution to the centralized Service Layer, where cross-cutting concerns like business rules, data formatting, and transaction boundaries are handled.
4. **Data Repository Dispatch:** Based on the entity model required, the Service Layer invokes the appropriate persistence abstraction, delegating to either the MySQL Spring Data JPA interfaces or the MongoDB Document Repository interfaces.
5. **Database Execution:** The respective database engines process the queried action—MySQL executes structured relational updates or joins across core entity tables, while MongoDB writes or retrieves BSON-based prescription documents.
6. **Data Assembly and Return:** The database engines return the raw result back to the repository layer, which map them into domain entities or data transfer objects (DTOs), bubbling them back up through the service layer to the original controller.
7. **View/Response Generation:** The controller processes the payload; the Thymeleaf controller binds the object attributes to HTML templates to compile a complete page on the server, while the REST controller serializes the response directly to JSON/XML to send back to the user client.

# Database Schema Design: Smart Clinic System

This document outlines the polyglot persistence architecture for the Smart Clinic System, leveraging both a relational MySQL database and a document-oriented MongoDB collection to optimize transactional integrity and storage flexibility.

---

## 1. MySQL Relational Database Design

The relational database handles core operational data that requires strict transactional consistency (ACID guarantees), strict relationship enforcement, and predictable structures.

### Entity Relationship & Table Definition

#### 1. Table: `doctors`
Stores professional profiles and system credentials for medical practitioners.
* **Columns:**
    * `doctor_id` (INT, AUTO_INCREMENT, PRIMARY KEY): Unique identifier for each doctor.
    * `first_name` (VARCHAR(50), NOT NULL): Doctor's given name.
    * `last_name` (VARCHAR(50), NOT NULL): Doctor's family name.
    * `email` (VARCHAR(100), UNIQUE, NOT NULL): Professional email address used as login username.
    * `specialty` (VARCHAR(100), NOT NULL): Medical specialization (e.g., Cardiology, Pediatrics).
    * `phone_number` (VARCHAR(15), UNIQUE, NOT NULL): Contact number.
    * `is_active` (BOOLEAN, DEFAULT TRUE): Soft-delete flag to deactivate accounts without losing historical data integrity.

#### 2. Table: `patients`
Tracks administrative and core contact records for individuals receiving care.
* **Columns:**
    * `patient_id` (INT, AUTO_INCREMENT, PRIMARY KEY): Unique identifier for each patient.
    * `first_name` (VARCHAR(50), NOT NULL): Patient's given name.
    * `last_name` (VARCHAR(50), NOT NULL): Patient's family name.
    * `email` (VARCHAR(100), UNIQUE, NOT NULL): Personal email address for communication/login.
    * `date_of_birth` (DATE, NOT NULL): Date of birth for identification and medical validation.
    * `phone_number` (VARCHAR(15), NOT NULL): Primary contact number.
    * `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Audit field tracking registration date.

#### 3. Table: `appointments`
Manages scheduling parameters, connecting patients with doctors while preventing scheduling overruns.
* **Columns:**
    * `appointment_id` (INT, AUTO_INCREMENT, PRIMARY KEY): Unique appointment record ID.
    * `patient_id` (INT, NOT NULL): Foreign key referencing `patients(patient_id)`.
    * `doctor_id` (INT, NOT NULL): Foreign key referencing `doctors(doctor_id)`.
    * `appointment_timestamp` (DATETIME, NOT NULL): Scheduled date and precise start time.
    * `status` (ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED'), DEFAULT 'PENDING'): Life-cycle tracker of the appointment.
* **Constraints / Keys:**
    * `FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE RESTRICT` *(Prevents deleting a patient if historical appointment data relies on it).*
    * `FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE RESTRICT`
    * `UNIQUE (doctor_id, appointment_timestamp)` *(Database-level guard checking that a doctor cannot be double-booked for the exact same block time).*

#### 4. Table: `admins`
Maintains operational access configurations for managers monitoring system integrity.
* **Columns:**
    * `admin_id` (INT, AUTO_INCREMENT, PRIMARY KEY): Unique administrative account ID.
    * `username` (VARCHAR(50), UNIQUE, NOT NULL): Access credential identity.
    * `email` (VARCHAR(100), UNIQUE, NOT NULL): Enterprise email.
    * `password_hash` (VARCHAR(255), NOT NULL): Securely salted cryptographically hashed credential.
    * `role_level` (ENUM('SUPER_ADMIN', 'CLINIC_STAFF'), NOT NULL): Granular Role-Based Access Control (RBAC) indicator.

---

## 2. MongoDB Document Collection Design

### Selected Collection: `prescriptions`

**Design Decision Justification:** Prescriptions are inherently polymorphic and non-relational; different conditions demand wildly variable line-item details, dosages, and fulfillment cycles. Storing this in a traditional rigid SQL environment results in sparse tables or cumbersome many-to-many lookup logic. MongoDB’s document hierarchy natively groups a complex prescription record—including nested metadata and variable-length item arrays—into a single, highly performant document mapped to the MySQL IDs.

### Realistic JSON Document Example

```json
{
  "_id": {"$oid": "65f8c3e21a2b3c4d5e6f7a8b"},
  "appointment_id": 10524, 
  "patient_id": 482,
  "doctor_id": 14,
  "issued_date": "2026-03-24T14:30:00Z",
  "diagnosis": {
    "primary": "Type 2 Diabetes Mellitus",
    "icd_10_code": "E11.9",
    "notes": "Patient presents high fasting blood glucose levels. Commencing standard therapeutic trial."
  },
  "medications": [
    {
      "drug_name": "Metformin Hydrochloride",
      "dosage": "500mg",
      "frequency": "Twice daily",
      "duration_days": 90,
      "instructions": "Take orally with meals (breakfast and dinner)."
    },
    {
      "drug_name": "Atorvastatin",
      "dosage": "10mg",
      "frequency": "Once daily",
      "duration_days": 30,
      "instructions": "Take at bedtime."
    }
  ],
  "refill_policy": {
    "allowed_refills": 3,
    "refills_remaining": 3,
    "expiration_date": "2027-03-24"
  },
  "digital_signature": "sig_doc_14_hash_8891bcfa32"
}
