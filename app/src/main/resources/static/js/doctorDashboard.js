import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

/**
 * Global state
 */
let selectedDate = new Date().toISOString().split("T")[0];
let patientName = null;
const token = localStorage.getItem("token");

/**
 * DOM references
 */
const tableBody = document.getElementById("patientTableBody");

/**
 * Search bar
 */
document.addEventListener("DOMContentLoaded", () => {

    const search = document.getElementById("searchBar");

    if (search) {
        search.addEventListener("input", (e) => {

            const value = e.target.value.trim();

            patientName = value.length > 0 ? value : null;

            loadAppointments();
        });
    }

    const todayBtn = document.getElementById("todayButton");
    const datePicker = document.getElementById("datePicker");

    if (todayBtn) {
        todayBtn.addEventListener("click", () => {

            selectedDate = new Date().toISOString().split("T")[0];

            if (datePicker) datePicker.value = selectedDate;

            loadAppointments();
        });
    }

    if (datePicker) {
        datePicker.addEventListener("change", (e) => {

            selectedDate = e.target.value;

            loadAppointments();
        });
    }

    loadAppointments();
});

/**
 * Load appointments
 */
async function loadAppointments() {

    try {

        const appointments = await getAllAppointments(
            selectedDate,
            patientName,
            token
        );

        tableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="5">No Appointments found for today.</td>
                </tr>
            `;

            return;
        }

        appointments.forEach(app => {

            const patient = {
                id: app.patientId,
                name: app.patientName,
                phone: app.phone,
                email: app.email
            };

            const row = createPatientRow(app, patient);

            tableBody.appendChild(row);

        });

    } catch (error) {

        console.error(error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="5">Error loading appointments. Try again later.</td>
            </tr>
        `;
    }
}