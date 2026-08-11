document.getElementById("signupForm").addEventListener("submit", async (e) => {
    e.preventDefault(); // stop traditional form submission/page reload

    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const errorMsg = document.getElementById("errorMsg");

    errorMsg.textContent = ""; // clear previous error, if any

    try {
        const response = await fetch("/viewspace/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email, password })
        });

        if (response.ok) {
            // success — move to OTP verification page, passing email via query param
            //wrong:->//window.location.href = "otpverification.html?email=" + encodeURIComponent(email);
			// store email temporarily so otpverification.html can read it
			           // without exposing it in the URL
			           //sessionStorage.setItem("pendingEmail", email);
			           //window.location.href = "otpverification.html";
					   window.location.href = "login.html";
        } else {
            const errorText = await response.text();
            errorMsg.textContent = errorText;
        }
    } catch (err) {
        errorMsg.textContent = "Something went wrong. Please try again.";
        console.error(err);
    }
});