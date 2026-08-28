const token = localStorage.getItem("token");
const username = localStorage.getItem("username");

if (!token) {
    // no token means not logged in — send back to login
    window.location.href = "login.html";
}

document.getElementById("welcomeMsg").textContent = "Logged in as: " + username;

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    window.location.href = "login.html";
});