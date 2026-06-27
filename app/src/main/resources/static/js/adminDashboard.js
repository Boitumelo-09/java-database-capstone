import { openModal } from "./components/modals.js";
import {
    getDoctors,
    filterDoctors,
    saveDoctor
} from "./services/doctorServices.js";

import { createDoctorCard } from "./components/doctorCard.js";

/**
 * Open Add Doctor Modal
 */
document.addEventListener("DOMContentLoaded", () => {

    const addBtn = document.getElementById("addDocBtn");

    if (addBtn) {
        addBtn.addEventListener("click", () => {
            openModal("addDoctor");
        });
    }

    loadDoctorCards();
});

/**
 * Load all doctors
 */
async function loadDoctorCards() {

    try {

        const doctors = await getDoctors();

        const contentDiv = document.getElementById("content");

        contentDiv.innerHTML = "";

        doctors.forEach(doc => {
            contentDiv.appendChild(createDoctorCard(doc));
        });

    } catch (error) {
        console.error("Error loading doctors:", error);
    }
}

/**
 * Filter doctors dynamically
 */
async function filterDoctorsOnChange() {

    try {

        const name = document.getElementById("searchBar").value.trim() || null;
        const time = document.getElementById("filterTime")?.value || null;
        const specialty = document.getElementById("filterSpecialty")?.value || null;

        const response = await filterDoctors(name, time, specialty);

        const doctors = response.doctors || [];

        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";

        if (doctors.length === 0) {
            contentDiv.innerHTML = `<p>No doctors found with the given filters.</p>`;
            return;
        }

        doctors.forEach(doc => {
            contentDiv.appendChild(createDoctorCard(doc));
        });

    } catch (error) {
        console.error(error);
        alert("❌ Error filtering doctors");
    }
}

/**
 * Event bindings for search + filters
 */
document.addEventListener("DOMContentLoaded", () => {

    const search = document.getElementById("searchBar");
    const time = document.getElementById("filterTime");
    const specialty = document.getElementById("filterSpecialty");

    if (search) search.addEventListener("input", filterDoctorsOnChange);
    if (time) time.addEventListener("change", filterDoctorsOnChange);
    if (specialty) specialty.addEventListener("change", filterDoctorsOnChange);

});