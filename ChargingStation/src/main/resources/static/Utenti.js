// Riferimento alla tabella HTML
const tableBody = document.querySelector('#utentiTable tbody');

// Verifica se la tabella esiste prima di procedere
if (tableBody) {
    // Effettua una richiesta GET all'endpoint del tuo controller per ottenere la lista degli utenti
    fetch('/ChargingStation/Welcome/Admin/users')
        .then(response => response.json())
        .then(jsonData => {
            // Popola la tabella con i dati JSON ricevuti
            jsonData.forEach(user => {
                // Creazione di una nuova riga nella tabella
                const row = tableBody.insertRow();

                // Aggiunta di colonne alla riga
                row.insertCell(0).textContent = user.email;
                row.insertCell(1).textContent = user.firstName;
                row.insertCell(2).textContent = user.lastName;

                // Aggiunta di una colonna per il pulsante "Elimina"
                const deleteCell = row.insertCell(3);
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Elimina';
                deleteButton.addEventListener('click', () => deleteUser(user.id));
                deleteCell.appendChild(deleteButton);

                // Aggiunta di una colonna per il pulsante "Abilita/Disabilita"
                const statusCell = row.insertCell(4);
                const statusButton = document.createElement('button');
                statusButton.textContent = user.enabled ? 'Disabilita' : 'Abilita';
                statusButton.addEventListener('click', () => modifyUserStatus(user.id, user.enabled));
                statusCell.appendChild(statusButton);
            });
        })
        .catch(error => console.error('Errore nella richiesta:', error));
}

// Funzione per gestire la richiesta di eliminazione di un utente
function deleteUser(userId) {
    // Invia una richiesta GET all'endpoint appropriato per eliminare l'utente
    fetch(`/ChargingStation/Welcome/Admin/deleteUser?id=${userId}`)
        .then(response => {
            if (response.ok) {
                response.text().then(Message => alert(Message));
                // Ricarica la pagina per visualizzare le modifiche
                location.reload();
            } else {
                console.error('Errore durante l\'eliminazione dell\'utente');
                response.text().then(errorMessage => alert(errorMessage));
            }
        })
        .catch(error => console.error('Errore durante la richiesta di eliminazione:', error));
}

// Funzione per gestire la richiesta di modifica dello stato di un utente
function modifyUserStatus(userId, isEnabled) {
    // Invia una richiesta GET all'endpoint appropriato per modificare lo stato dell'utente
    fetch(`/ChargingStation/Welcome/Admin/statusUser?id=${userId}`)
        .then(response => {
            if (response.ok) {
                response.text().then(Message => alert(Message));
                // Ricarica la pagina per visualizzare le modifiche
                location.reload();
            } else {
                console.error('Errore durante la modifica dello stato dell\'utente');
                response.text().then(errorMessage => alert(errorMessage));
            }
        })
        .catch(error => console.error('Errore durante la richiesta di modifica dello stato:', error));
}