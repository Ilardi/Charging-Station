	const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

   // Riferimento alla tabella HTML
    const tableBody = document.querySelector('#prenotazioniTable tbody');
    const idColonninaElement = document.getElementById('idColonnina');
	const idColonnina = idColonninaElement.textContent;

    // Effettua una richiesta GET all'endpoint del tuo controller
    fetch(`/ChargingStation/Welcome/prenotazioni/colonnina/${idColonnina}`)
        .then(response => response.json())
        .then(jsonData => {
            // Popola la tabella con i dati JSON ricevuti
            jsonData.forEach(prenotazione => {
                const row = tableBody.insertRow();
                row.insertCell(0).textContent = prenotazione.id;
                row.insertCell(1).textContent = prenotazione.idColonnina;
                row.insertCell(2).textContent = prenotazione.emailUtente;
                row.insertCell(3).textContent = prenotazione.colonnina;
                row.insertCell(4).textContent = prenotazione.connessione;                
                row.insertCell(5).textContent = prenotazione.inizio;
                row.insertCell(6).textContent = prenotazione.fine;
                
                // Aggiunta di una colonna per il pulsante "Elimina"
                const deleteCell = row.insertCell(7);
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Elimina';
                deleteButton.addEventListener('click', () => deletePrenotazione(prenotazione.id));
                deleteCell.appendChild(deleteButton);
                
            });
        })
        .catch(error => console.error('Errore nella richiesta:', error));
    
    // Funzione per gestire la richiesta di eliminazione di una prenotazione
    function deletePrenotazione(prenotazioneId) {
        // Invia una richiesta DELETE all'endpoint appropriato
        fetch(`/ChargingStation/Welcome/prenotazioni/delete/${prenotazioneId}`, {
            method: 'DELETE',
            headers: {
      			'X-XSRF-TOKEN': csrfToken,
    		},
        })
        .then(response => {
            if (response.ok) {
                // Ricarica la pagina per visualizzare le modifiche
                location.reload();
            } else {
                console.error('Errore durante l\'eliminazione della prenotazione:', response.statusText);
            }
        })
        .catch(error => console.error('Errore durante la richiesta di eliminazione:', error));
    }