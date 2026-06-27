// header.js

/**
 * Renders the page header based on the user's role and session.
 */
function renderHeader() {

    const headerDiv = document.getElementById("header");

    if (!headerDiv) {
        return;
    }

    // Homepage - clear any previous session
    if (window.location.pathname.endsWith("/")) {

        localStorage.removeItem("userRole");
        localStorage.removeItem("token");

        headerDiv.innerHTML = `
            <header class="header">

                <div class="logo-section">

                    <img src="../assets/images/logo/logo.png"
                         alt="Hospital CMS Logo"
                         class="logo-img">

                    <span class="logo-title">
                        Hospital CMS
                    </span>

                </div>

            </header>
        `;

        return;
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Session validation
    if (
        (role === "loggedPatient" ||
            role === "admin" ||
            role === "doctor") &&
        !token
    ) {

        localStorage.removeItem("userRole");

        alert("Session expired or invalid login. Please log in again.");

        window.location.href = "/";

        return;
    }

    let headerContent = `
        <header class="header">

            <div class="logo-section">

                <img src="../assets/images/logo/logo.png"
                     alt="Hospital CMS Logo"
                     class="logo-img">

                <span class="logo-title">
                    Hospital CMS
                </span>

            </div>

            <nav class="nav-links">
    `;

    switch (role) {

        case "admin":

            headerContent += `
                <button
                    id="addDocBtn"
                    class="adminBtn">
                    Add Doctor
                </button>

                <a href="#" id="logoutBtn">
                    Logout
                </a>
            `;

            break;

        case "doctor":

            headerContent += `
                <button
                    id="doctorHomeBtn"
                    class="adminBtn">
                    Home
                </button>

                <a href="#" id="logoutBtn">
                    Logout
                </a>
            `;

            break;

        case "patient":

            headerContent += `
                <button
                    id="patientLogin"
                    class="adminBtn">
                    Login
                </button>

                <button
                    id="patientSignup"
                    class="adminBtn">
                    Sign Up
                </button>
            `;

            break;

        case "loggedPatient":

            headerContent += `
                <button
                    id="home"
                    class="adminBtn">
                    Home
                </button>

                <button
                    id="patientAppointments"
                    class="adminBtn">
                    Appointments
                </button>

                <a href="#"
                   id="patientLogout">
                    Logout
                </a>
            `;

            break;

        default:

            headerContent += ``;

    }

    headerContent += `
            </nav>

        </header>
    `;

    headerDiv.innerHTML = headerContent;

    attachHeaderButtonListeners();

}

/**
 * Attach listeners after rendering.
 */
function attachHeaderButtonListeners() {

    const addDoctorBtn = document.getElementById("addDocBtn");

    if (addDoctorBtn) {

        addDoctorBtn.addEventListener("click", () => {

            if (typeof openModal === "function") {
                openModal("addDoctor");
            }

        });

    }

    const doctorHomeBtn = document.getElementById("doctorHomeBtn");

    if (doctorHomeBtn) {

        doctorHomeBtn.addEventListener("click", () => {

            window.location.href = "/doctorDashboard";

        });

    }

    const patientLogin = document.getElementById("patientLogin");

    if (patientLogin) {

        patientLogin.addEventListener("click", () => {

            if (typeof openModal === "function") {
                openModal("patientLogin");
            }

        });

    }

    const patientSignup = document.getElementById("patientSignup");

    if (patientSignup) {

        patientSignup.addEventListener("click", () => {

            if (typeof openModal === "function") {
                openModal("patientSignup");
            }

        });

    }

    const homeBtn = document.getElementById("home");

    if (homeBtn) {

        homeBtn.addEventListener("click", () => {

            window.location.href = "/loggedPatientDashboard";

        });

    }

    const appointmentsBtn = document.getElementById("patientAppointments");

    if (appointmentsBtn) {

        appointmentsBtn.addEventListener("click", () => {

            window.location.href = "/patientAppointments";

        });

    }

    const logoutBtn = document.getElementById("logoutBtn");

    if (logoutBtn) {

        logoutBtn.addEventListener("click", (event) => {

            event.preventDefault();

            logout();

        });

    }

    const patientLogout = document.getElementById("patientLogout");

    if (patientLogout) {

        patientLogout.addEventListener("click", (event) => {

            event.preventDefault();

            logoutPatient();

        });

    }

}

/**
 * Logout for Admin and Doctor.
 */
function logout() {

    localStorage.removeItem("token");
    localStorage.removeItem("userRole");

    window.location.href = "/";

}

/**
 * Logout for Patient.
 */
function logoutPatient() {

    localStorage.removeItem("token");

    localStorage.setItem("userRole", "patient");

    window.location.href = "/patientDashboard";

}

// Render immediately when loaded
renderHeader();