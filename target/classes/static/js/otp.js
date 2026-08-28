// Guard clause — if there's no pending email, user shouldn't be on this page
const email = sessionStorage.getItem("pendingEmail");

if (!email) {
    window.location.href = "signup.html";
}

document.getElementById("otpForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const otp = document.getElementById("otp").value;
    const errorMsg = document.getElementById("errorMsg");
    const successMsg = document.getElementById("successMsg");

    errorMsg.textContent = "";
    successMsg.textContent = "";

    try {
        const response = await fetch("/viewspace/auth/verify-otp", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, otp })
        });

        const resultText = await response.text();

        if (response.ok) {
            successMsg.textContent = resultText;
            sessionStorage.removeItem("pendingEmail"); // cleanup — no longer needed

            // small delay so user can read the success message before redirect
            setTimeout(() => {
                window.location.href = "login.html";
            }, 1500);
        } else {
            errorMsg.textContent = resultText;
        }
    } catch (err) {
        errorMsg.textContent = "Something went wrong. Please try again.";
        console.error(err);
    }
});







const resendLink = document.getElementById("resendLink");

resendLink.addEventListener("click", async (e) => {
    e.preventDefault(); // prevent the "#" link from jumping the page

    const errorMsg = document.getElementById("errorMsg");
    const successMsg = document.getElementById("successMsg");

    errorMsg.textContent = "";
    successMsg.textContent = "";

    // disable the link temporarily to prevent accidental spam-clicking
    resendLink.style.pointerEvents = "none";
    resendLink.textContent = "Sending...";

    try {
        const response = await fetch("/viewspace/auth/resend-otp", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email })
        });

        const resultText = await response.text();

        if (response.ok) {
            successMsg.textContent = resultText;
        } else {
            errorMsg.textContent = resultText;
        }
    } catch (err) {
        errorMsg.textContent = "Something went wrong. Please try again.";
        console.error(err);
    } finally {
        // re-enable after a short cooldown, regardless of success/failure
		resendLink.textContent = "Resend OTP";
		setTimeout(() => {
            resendLink.style.pointerEvents = "auto";
            resendLink.textContent = "Resend OTP";
        }, 15000); // 15 second cooldown before allowing another click
    }
});