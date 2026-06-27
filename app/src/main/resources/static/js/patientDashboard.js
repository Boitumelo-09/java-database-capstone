// patientDashboard.js

import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { openModal } from "./components/modals.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { patientSignup, patientLogin } from "./services/patientServices.js";

/**
 * Global state (keeps filtering consistent)
 */
let doctorsCache = [];

/**
 * Initialize dashboard when DOM is ready
 */
document.addEventListener("DOMContentLoaded", initPatientDashboard);

function initPatientDashboard() {
  loadDoctorCards();
  bindAuthButtons();
  bindFilterEvents();
}

/**
 * Load all doctors on page load
 */
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    doctorsCache = doctors || [];

    renderDoctorCards(doctorsCache);
  } catch (error) {
    console.error("Failed to load doctors:", error);
    showMessage("❌ Failed to load doctors.");
  }
}

/**
 * Render doctor cards
 */
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors available.</p>";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

/**
 * Bind login/signup modal buttons
 */
function bindAuthButtons() {
  const signupBtn = document.getElementById("patientSignup");
  const loginBtn = document.getElementById("patientLogin");

  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }
}

/**
 * Bind search + filter events (debounced)
 */
function bindFilterEvents() {
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", debounce(filterDoctorsOnChange, 300));
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
}

/**
 * Handle filtering logic
 */
async function filterDoctorsOnChange() {
  try {
    const searchBar = document.getElementById("searchBar")?.value.trim();
    const filterTime = document.getElementById("filterTime")?.value;
    const filterSpecialty = document.getElementById("filterSpecialty")?.value;

    const name = searchBar ? searchBar : null;
    const time = filterTime ? filterTime : null;
    const specialty = filterSpecialty ? filterSpecialty : null;

    const response = await filterDoctors(name, time, specialty);

    const doctors = response?.doctors || [];

    if (doctors.length === 0) {
      showMessage("No doctors found with the given filters.");
      return;
    }

    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Filter error:", error);
    showMessage("❌ Error filtering doctors.");
  }
}

/**
 * Patient Signup
 */
window.signupPatient = async function () {
  try {
    const data = {
      name: document.getElementById("name")?.value,
      email: document.getElementById("email")?.value,
      password: document.getElementById("password")?.value,
      phone: document.getElementById("phone")?.value,
      address: document.getElementById("address")?.value,
    };

    const { success, message } = await patientSignup(data);

    alert(message);

    if (success) {
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    }
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ Signup failed.");
  }
};

/**
 * Patient Login
 */
window.loginPatient = async function () {
  try {
    const data = {
      email: document.getElementById("email")?.value,
      password: document.getElementById("password")?.value,
    };

    const response = await patientLogin(data);

    if (!response.ok) {
      alert("❌ Invalid credentials!");
      return;
    }

    const result = await response.json();

    localStorage.setItem("token", result.token);
    localStorage.setItem("userRole", "loggedPatient");

    if (typeof selectRole === "function") {
      selectRole("loggedPatient");
    }

    window.location.href = "/pages/loggedPatientDashboard.html";
  } catch (error) {
    console.error("Login failed:", error);
    alert("❌ Login error occurred.");
  }
};

/**
 * Utility: Show message in UI
 */
function showMessage(message) {
  const contentDiv = document.getElementById("content");
  if (contentDiv) {
    contentDiv.innerHTML = `<p>${message}</p>`;
  }
}

/**
 * Utility: Debounce function (prevents spam API calls)
 */
function debounce(func, delay) {
  let timeout;
  return (...args) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), delay);
  };
}