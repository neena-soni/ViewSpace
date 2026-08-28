document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const errorMsg = document.getElementById("errorMsg");

    errorMsg.textContent = "";

    try {
        const response = await fetch("/viewspace/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const data = await response.json(); // JwtResponse: {token, type, username, email, roles}
            
            // store the token — this is what makes the user "logged in" going forward
            localStorage.setItem("token", data.token);
            localStorage.setItem("username", data.username);

            window.location.href = "index.html";
        } else {
            const errorText = await response.text();
            errorMsg.textContent = errorText;
        }
    } catch (err) {
        errorMsg.textContent = "Something went wrong. Please try again.";
        console.error(err);
    }
});