// footer.js

/**
 * Renders the application footer.
 */
function renderFooter() {

    const footer = document.getElementById("footer");

    if (!footer) {
        return;
    }

    footer.innerHTML = `
        <footer class="footer">

            <div class="footer-container">

                <!-- Branding -->
                <div class="footer-brand">

                    <h3>Hospital CMS</h3>

                    <p>
                        Providing reliable healthcare management solutions
                        for hospitals, doctors, and patients.
                    </p>

                    <p class="copyright">
                        &copy; ${new Date().getFullYear()} Hospital CMS.
                        All Rights Reserved.
                    </p>

                </div>

                <!-- Company -->
                <div class="footer-column">

                    <h4>Company</h4>

                    <a href="#">About</a>
                    <a href="#">Careers</a>
                    <a href="#">Press</a>

                </div>

                <!-- Support -->
                <div class="footer-column">

                    <h4>Support</h4>

                    <a href="#">Account</a>
                    <a href="#">Help Center</a>
                    <a href="#">Contact</a>

                </div>

                <!-- Legal -->
                <div class="footer-column">

                    <h4>Legal</h4>

                    <a href="#">Terms of Service</a>
                    <a href="#">Privacy Policy</a>
                    <a href="#">Licensing</a>

                </div>

            </div>

        </footer>
    `;

}

// Automatically render the footer
renderFooter();