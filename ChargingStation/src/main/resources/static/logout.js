
var modal = document.getElementById('LogoutModal');
var confirmButton = document.getElementById('confirmLogout');
var cancelButton = document.getElementById('cancelLogout');
var logoutLink = document.getElementById('logoutLink');

logoutLink.addEventListener('click', function(e) {
    e.preventDefault();
    modal.style.display = 'block';
});

confirmButton.addEventListener('click', function() {
    window.location.href = "/logout";
});

cancelButton.addEventListener('click', function() {
    modal.style.display = 'none';
});

// Chiude il modal se l'utente fa clic al di fuori di esso
window.addEventListener('click', function(event) {
    if (event.target == modal) {
        modal.style.display = 'none';
    }
});
