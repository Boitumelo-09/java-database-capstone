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
