// doctorCard.js

import { showBookingOverlay } from "../services/loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

/**
 * Creates and returns a doctor card element.
 *
 * @param {Object} doctor
 * @returns {HTMLDivElement}
 */
export function createDoctorCard(doctor) {

    // ==========================
    // Main Card
    // ==========================

    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

    // ==========================
    // Doctor Information
    // ==========================

    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.innerHTML = `<strong>Specialization:</strong> ${doctor.specialization}`;

    const email = document.createElement("p");
    email.innerHTML = `<strong>Email:</strong> ${doctor.email}`;

    const availability = document.createElement("p");
    availability.innerHTML = `
        <strong>Available:</strong>
        ${Array.isArray(doctor.availability)
            ? doctor.availability.join(", ")
            : doctor.availability}
    `;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // ==========================
    // Action Buttons
    // ==========================

    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // ==========================
    // Admin Actions
    // ==========================

    if (role === "admin") {

        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "Delete";

        deleteBtn.addEventListener("click", async () => {

            const confirmed = confirm(
                `Delete Dr. ${doctor.name}?`
            );

            if (!confirmed) {
                return;
            }

            const token = localStorage.getItem("token");

            if (!token) {

                alert("Session expired.");

                window.location.href = "/";

                return;
            }

            try {

                await deleteDoctor(doctor.id, token);

                alert("Doctor deleted successfully.");

                card.remove();

            } catch (error) {

                console.error(error);

                alert("Unable to delete doctor.");

            }

        });

        actionsDiv.appendChild(deleteBtn);

    }

    // ==========================
    // Patient (Not Logged In)
    // ==========================

    else if (role === "patient") {

        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";

        bookNow.addEventListener("click", () => {

            alert("Please log in before booking an appointment.");

        });

        actionsDiv.appendChild(bookNow);

    }

    // ==========================
    // Logged-in Patient
    // ==========================

    else if (role === "loggedPatient") {

        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";

        bookNow.addEventListener("click", async (event) => {

            const token = localStorage.getItem("token");

            if (!token) {

                alert("Session expired.");

                window.location.href = "/";

                return;

            }

            try {

                const patientData = await getPatientData(token);

                showBookingOverlay(
                    event,
                    doctor,
                    patientData
                );

            } catch (error) {

                console.error(error);

                alert("Unable to load patient information.");

            }

        });

        actionsDiv.appendChild(bookNow);

    }

    // ==========================
    // Assemble Card
    // ==========================

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;

}