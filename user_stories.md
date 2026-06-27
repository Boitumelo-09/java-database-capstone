# User Stories: Healthcare & Appointment Management System

This document outlines the user stories for the core personas interacting with the system: **Patients**, **Doctors**, and **Administrators**.

---

## 1. Patient User Stories
*Persona: Individuals seeking to schedule, manage, and attend medical consultations.*

### US-101: Account Creation and Profile Management
* **As a** new patient  
* **I want to** register an account with my basic personal and medical details  
* **So that** I can securely log in and manage my appointments.
* **Acceptance Criteria:**
  * Must require a unique email, secure password, full name, and contact number.
  * Patients can view and edit their profile details (e.g., updating phone number or address).
  * Data must be encrypted and stored securely in compliance with healthcare data regulations.

### US-102: Book an Appointment
* **As a** registered patient  
* **I want to** view available doctors by specialty and book an open time slot  
* **So that** I can secure a consultation for my health needs.
* **Acceptance Criteria:**
  * Patients can filter doctors by specialty, availability, or name.
  * Real-time validation prevents double-booking the same time slot.
  * A confirmation notification (email/SMS) is triggered upon successful booking.

### US-103: View and Manage Appointments
* **As a** patient  
* **I want to** view a dashboard of my upcoming and past appointments, with the option to reschedule or cancel  
* **So that** I can keep track of my medical schedule and make changes when necessary.
* **Acceptance Criteria:**
  * Upcoming appointments must display the doctor’s name, specialty, date, and time.
  * Cancellations must be allowed up to 24 hours before the scheduled time.
  * Rescheduling updates the doctor’s availability matrix instantly.

---

## 2. Doctor User Stories
*Persona: Medical professionals managing their daily schedules, availability, and patient consultations.*

### US-201: Manage Availability Schedule
* **As a** doctor  
* **I want to** define and update my weekly working hours and available time slots  
* **So that** patients can only book appointments during my designated working hours.
* **Acceptance Criteria:**
  * The doctor can set recurring weekly hours or block out specific dates for leave/holidays.
  * Slots already booked by patients cannot be marked as available unless first cancelled.
  * Minimum slot durations (e.g., 30 minutes) are strictly enforced.

### US-202: View Daily Consultation Schedule
* **As a** doctor  
* **I want to** view a chronological list of my scheduled appointments for the day or week  
* **So that** I can efficiently prepare for each patient visit.
* **Acceptance Criteria:**
  * The view must list the patient’s name, appointment time, and basic reason for the visit.
  * The dashboard must update dynamically if a patient cancels or reschedules on short notice.

### US-203: Update Patient Appointment Status
* **As a** doctor  
* **I want to** mark an appointment as "Completed", "No-show", or add brief medical notes  
* **So that** the system maintains an accurate history of the patient's visit.
* **Acceptance Criteria:**
  * Status updates must update the database immediately.
  * Medical notes text field must support standard alphanumeric characters and be securely saved to the patient record.

---

## 3. Admin User Stories
*Persona: System administrators responsible for user management, system configurations, and operational integrity.*

### US-301: User Role and Access Management
* **As an** administrator  
* **I want to** create, update, or deactivate user accounts (Patients, Doctors, Admins) and assign roles  
* **So that** only authorized individuals can access specific areas of the system.
* **Acceptance Criteria:**
  * Only users with the `ADMIN` role can access the administration console.
  * Deactivating a doctor profile automatically flags or reassigns their outstanding appointments.
  * Audit logs must record which admin modified user access levels.

### US-302: System Configuration and Global Settings
* **As an** administrator  
* **I want to** configure system-wide settings, such as global clinic operational hours and appointment cancellation lock-out windows  
* **So that** system logic matches the actual physical clinic policies.
* **Acceptance Criteria:**
  * Changes to global settings apply instantly across all user interfaces.
  * Validation rules prevent conflicting rules (e.g., setting clinic closing hours earlier than active doctor schedules).

### US-303: Appointment Activity Reporting
* **As an** administrator  
* **I want to** view metrics on total bookings, cancellations, and doctor utilization rates  
* **So that** I can monitor clinic efficiency and system performance.
* **Acceptance Criteria:**
  * Admin dashboard displays high-level analytics (e.g., total active appointments for the month).
  * Data can be filtered by date ranges and specific doctor departments.
