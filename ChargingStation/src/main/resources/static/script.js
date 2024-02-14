const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

// Imposta il token CSRF per tutte le richieste Ajax globalmente
$.ajaxSetup({
  beforeSend: function(xhr) {
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);
  }
});

// Inizializza la mappa
const map = L.map('map').setView([41.8719, 12.5674], 6);

// Aggiunge una mappa di sfondo
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

// Inizializza un gruppo di marker cluster con opzioni
const markerClusterGroup = L.markerClusterGroup({
    disableClusteringAtZoom: 12, // Imposta il livello di zoom a cui disabilitare i cluster
});

// Inizializza una variabile per conservare l'elenco completo delle colonnine
let allChargingStations = [];
// Inizializza una variabile per conservare le colonnine suddivise per provincia
let chargingStationsByProvince = {};

// Funzione per gestire la prenotazione
function prenota(id, Inizio, Fine,colonnina,connessione) {
    // Verifica che le date e gli orari siano validi
    const now = new Date();
    const startDateTime = new Date(Inizio);
    const endDateTime = new Date(Fine);

    if (startDateTime <= now) {
        alert("Seleziona una data e un'ora di inizio futura.");
        return;
    }

    if (endDateTime <= now || endDateTime <= startDateTime) {
        alert("Seleziona una data e un'ora di fine successiva a quella di inizio.");
        return;
    }
    
    // Verifica che la durata della prenotazione non superi le 6 ore
    const durataPrenotazione = endDateTime - startDateTime;
    const durataInOre = durataPrenotazione / (1000 * 60 * 60); // Conversione da millisecondi a ore

    if (durataInOre > 6) {
        alert("La durata della prenotazione non può superare le 6 ore.");
        return;
    }

    console.log('Prenotazione per la stazione con ID:', id);
    console.log('Prenotazione con orario inizio:', Inizio);
    console.log('Prenotazione con orario fine:', Fine);
    
    // Dati della prenotazione
    const prenotazioneData = {
        idColonnina: id,
        idUtente: 0, 
	    inizio: Inizio, 
	    fine: Fine, 
	    colonnina: colonnina,
	    connessione: connessione
    };

    // Effettua una richiesta AJAX al tuo backend
    $.ajax({
        type: 'POST',
        url: 'prenotazioni',
        contentType: 'application/json',
        data: JSON.stringify(prenotazioneData),
        success: function (response) {
            console.log('Prenotazione effettuata con successo:', response);

			alert('Prenotazione avvenuta con successo');
            // Chiude il popup di prenotazione
            closePrenotaPopup();
        },
        error: function (error) {
            console.error('Errore durante la prenotazione:', error);
    		console.log('Dettagli dell\'errore:', error.responseText);
    		if(error.responseText)
    			alert(error.responseText);
    		else
    			 alert('Errore durante la prenotazione. Riprovare più tardi');       
        }
    });
}


function openPrenotaPopup(id, numberOfStation, maxConnections) {
    const prenotaPopup = document.getElementById('prenotaPopup');
	const prenotaBtn = document.getElementById('prenotaBtn');
    if (prenotaPopup) {
        prenotaPopup.style.display = 'block';
        
        // Fa diventare invisibile il pulsante Prenota
        prenotaBtn.style.display = 'none';
        
        // Popola la dropdown delle colonnine
        const colonnineDropdown = document.getElementById('colonnineDropdown');
        colonnineDropdown.innerHTML = ''; // Pulisce le opzioni precedenti

        for (let i = 1; i <= numberOfStation; i++) {
            const option = document.createElement('option');
            option.value = i;
            option.text = `Colonnina ${i}`;
            colonnineDropdown.add(option);
        }
        
        // Popola la dropdown delle connessioni
        const connessioniDropdown = document.getElementById('connessioniDropdown');
        connessioniDropdown.innerHTML = ''; // Pulisce le opzioni precedenti

        for (let i = 1; i <= maxConnections; i++) {
            const option = document.createElement('option');
            option.value = i;
            option.text = `Connessione ${i}`;
            connessioniDropdown.add(option);
        }

        // Imposta la data e l'ora minima per la prenotazione sulla data e ora correnti
        const now = new Date();
        const formattedNow = now.toISOString().slice(0, 16); // Formato "YYYY-MM-DDTHH:mm"
        document.getElementById('startDateTime').min = formattedNow;
        document.getElementById('endDateTime').min = formattedNow;

    }
}

// Funzione per chiudere il popup della prenotazione
function closePrenotaPopup() {
    const prenotaPopup = document.getElementById('prenotaPopup');
    const prenotaBtn = document.getElementById('prenotaBtn');
    if (prenotaPopup) {
        prenotaPopup.style.display = 'none';
        prenotaBtn.style.display = 'block';
    }
}

function navigateToPrenotazioni(idColonnina) {
    // Costruisci l'URL desiderato
    const url = `/ChargingStation/Welcome/prenotazioni/colonnina/${idColonnina}/show`;

    // Naviga alla nuova pagina
    window.location.href = url;
}

// Funzione per mostrare il popup per la modifica dello stato
function modificaStatoColonnina(idColonnina) {
    const confermaModificaStato = confirm('Vuoi davvero modificare lo stato di questa stazione di ricarica?');

    if (confermaModificaStato) {
        // Effettua una richiesta AJAX al tuo backend per modificare lo stato della stazione
        $.ajax({
            type: 'POST',
            url: `/ChargingStation/modStatusStation/${idColonnina}`,
            success: function (response) {
                alert(response);
                location.reload(); // Ricarica la pagina HTML dell'admin
            },
            error: function (error) {
                console.error('Errore durante la modifica dello stato:', error);
                alert(error.responseText);
            }
        });
    }
}

// Funzione per eliminare una colonnina
function eliminaColonnina(id) {
    const confermaEliminazione = confirm('Vuoi davvero eliminare questa stazione di ricarica?');

    if (confermaEliminazione) {
        // Effettua una richiesta AJAX al tuo backend per eliminare la stazione
        $.ajax({
            type: 'DELETE',
            url: `/deleteChargingStation/${id}`,
            success: function (response) {
                alert(response);
                location.reload(); // Ricarica la pagina HTML dell'admin
            },
            error: function (error) {
                console.error('Errore durante l\'eliminazione della stazione:', error);
                alert(error.responseText);
            }
        });
    }
}

function addMarkers(chargingStations) {
    chargingStations.forEach(station => {
        const { addressInfo, connectionInfo, id, nameOperatorInfo, urloperatorInfo, usageCost, membershipRequired, statusType, mediaItemURL, recentlyVerified, numberOfStation } = station;
        const { latitude, longitude } = addressInfo;

        if (latitude !== undefined && longitude !== undefined) {
            const locationTitle = addressInfo.addressTitle || 'Colonnina Elettrica';

            const maxMediaItemURLLength = 36;
            const abbreviatedMediaItemURL = mediaItemURL && mediaItemURL.length > maxMediaItemURLLength
                ? mediaItemURL.substring(0, maxMediaItemURLLength) + '...'
                : mediaItemURL;

            const maxOperatorInfoURLLength = 36;
            const abbreviatedurloperatorInfo = urloperatorInfo && urloperatorInfo.length > maxOperatorInfoURLLength
                ? urloperatorInfo.substring(0, maxOperatorInfoURLLength) + '...'
                : urloperatorInfo;

            const pageType = document.documentElement.getAttribute('data-page-type');
            const maxConnections = connectionInfo.reduce((sum, conn) => sum + conn.numberOfConnections, 0);
            const popupContent = `
                <b>${locationTitle}</b><br>
                ${addressInfo.addressLine}, ${addressInfo.town}, ${addressInfo.stateOrProvince}<br>
                ${connectionInfo.some(conn => conn.powerKW !== null || conn.connectionType !== "Unknown") ?
                    `Connessioni: ${connectionInfo.map(conn => (
                        conn.powerKW !== null || conn.connectionType !== "Unknown" ?
                        `${conn.powerKW ?? '-'} kW ${conn.connectionType ?? ''} Fast: ${conn.fastChargeCapable}` :
                        ''
                    )).join('; ')}<br>` : ''}
                Numero connessioni: ${connectionInfo.map(conn => `${conn.numberOfConnections}`)}<br>
                Costo KW: ${usageCost !== null ? usageCost : 'Non Disponibile'}, Abbonamento: ${membershipRequired}<br>
                Stato: ${statusType ? 'Funzionante' : 'Non Funzionante'}, Verificata: ${recentlyVerified} <br>
                Numero colonnine: ${numberOfStation}<br>
                Nome Operatore: ${nameOperatorInfo !== "(Unknown Operator)" ? nameOperatorInfo : 'Non Disponibile'}<br>
                URL Operatore: ${urloperatorInfo ? `<a href="${urloperatorInfo}" target="_blank">${abbreviatedurloperatorInfo}</a>` : 'Non disponibile'}<br>				
                Media: ${mediaItemURL ? `<a href="${mediaItemURL}" target="_blank">${abbreviatedMediaItemURL}</a>` : 'Non disponibile'}<br>
                ID: ${id}<br>
                ${pageType === 'welcome-user' ? `
                    <button id="prenotaBtn">Prenota</button>										
                    <div id="prenotaPopup" style="display: none;">
                        <label for="colonnineDropdown">Seleziona Colonnina:</label>
                        <select id="colonnineDropdown">
                            <!-- Le opzioni della dropdown verranno aggiunte dinamicamente in openPrenotaPopup -->
                        </select>
                        <label for="connessioniDropdown">Seleziona Connessione:</label>
                        <select id="connessioniDropdown">
                            <!-- Le opzioni della dropdown verranno aggiunte dinamicamente in openPrenotaPopup -->
                        </select>
                        <label for="startDateTime">Data e Ora di inizio:</label>
                        <input type="datetime-local" id="startDateTime" name="startDateTime">
                        <br>
                        <label for="endDateTime">Data e Ora di fine:</label>
                        <input type="datetime-local" id="endDateTime" name="endDateTime">
                        <br>
                        <button id="confirmBtn">Conferma Prenotazione</button>
                    </div>
                ` : ''}                
                ${pageType === 'welcome-admin' ? `
                    <button id="PrenotazioniBtn">Visualizza Prenotazioni</button>
                    <button id="ModificaStatoBtn">Modifica Stato</button>
                    <button id="EliminaBtn">Elimina</button>
                ` : ''}
            `;

            const marker = L.marker([latitude, longitude]);
            marker.bindPopup(popupContent);

            marker.on('popupopen', function (event) {
                const popup = event.popup;

                const prenotaButton = popup._container.querySelector('#prenotaBtn');
                if (prenotaButton) {
                    prenotaButton.addEventListener('click', function () {
                        openPrenotaPopup(id, numberOfStation, maxConnections);
                    });
                }
                
                const confirmButton = popup._container.querySelector('#confirmBtn');
                if (confirmButton) {
                    confirmButton.addEventListener('click', function () {
                        prenota(id, $('#startDateTime').val(), $('#endDateTime').val(), $('#colonnineDropdown').val(), $('#connessioniDropdown').val());
                    });
                }
                
                const PrenotazioniBtn = popup._container.querySelector('#PrenotazioniBtn');
                if (PrenotazioniBtn) {
                    PrenotazioniBtn.addEventListener('click', function () {
                        navigateToPrenotazioni(id);
                    });
                }
                
                const ModificaStatoBtn = popup._container.querySelector('#ModificaStatoBtn');
                if (ModificaStatoBtn) {
                    ModificaStatoBtn.addEventListener('click', function () {
                        modificaStatoColonnina(id);
                    });
                }
                
                const EliminaBtn = popup._container.querySelector('#EliminaBtn');
                if (EliminaBtn) {
                    EliminaBtn.addEventListener('click', function () {
                        eliminaColonnina(id);
                    });
                }                 
                
            });

            markerClusterGroup.addLayer(marker);
        }
    });

    map.addLayer(markerClusterGroup);
}


// Funzione per popolare la dropdown con le province uniche dai dati
function populateProvinceDropdown(chargingStations) {
    const provinceSelect = document.getElementById('provinceSelect');
	
	if (!provinceSelect) {
        return;
    }
    // Filtra solo le province
    const provinces = chargingStations.filter(station => station.addressInfo && station.addressInfo.stateOrProvince && station.addressInfo.stateOrProvince.length === 2);

    // Ottiene un elenco unico di codici di province
    const uniqueProvinceCodes = [...new Set(provinces.map(station => station.addressInfo.stateOrProvince))];

    // Aggiunge opzioni alla dropdown
    uniqueProvinceCodes.forEach(provinceCode => {
    	
    	// Ottiene il nome completo della provincia
        const provinceFullName = getProvinceFullName(provinceCode.toUpperCase());
        
        const option = document.createElement('option');
        option.value = provinceCode;
        option.text = `${provinceCode} - ${provinceFullName}`;
        provinceSelect.add(option);
    });
}

// Funzione per ottenere il nome completo della provincia dato il codice
function getProvinceFullName(provinceCode) {
    const provinceNameMap = {
        'AG': 'Agrigento',
        'AL': 'Alessandria',
        'AN': 'Ancona',
        'AO': 'Aosta',
        'AQ': 'L\'Aquila',
        'AR': 'Arezzo',
        'AP': 'Ascoli Piceno',
        'AT': 'Asti',
        'AV': 'Avellino',
        'BA': 'Bari',
        'BT': 'Barletta-Andria-Trani',
        'BL': 'Belluno',
        'BN': 'Benevento',
        'BG': 'Bergamo',
        'BI': 'Biella',
        'BO': 'Bologna',
        'BZ': 'Bolzano',
        'BS': 'Brescia',
        'BR': 'Brindisi',
        'CA': 'Cagliari',
        'CL': 'Caltanissetta',
        'CB': 'Campobasso',
        'CI': 'Carbonia-Iglesias',
        'CE': 'Caserta',
        'CT': 'Catania',
        'CZ': 'Catanzaro',
        'CH': 'Chieti',
        'CO': 'Como',
        'CS': 'Cosenza',
        'CR': 'Cremona',
        'KR': 'Crotone',
        'CN': 'Cuneo',
        'EN': 'Enna',
        'FM': 'Fermo',
        'FE': 'Ferrara',
        'FI': 'Firenze',
        'FG': 'Foggia',
        'FC': 'Forlì-Cesena',
        'FR': 'Frosinone',
        'GE': 'Genova',
        'GO': 'Gorizia',
        'GR': 'Grosseto',
        'IM': 'Imperia',
        'IS': 'Isernia',
        'SP': 'La Spezia',
        'LT': 'Latina',
        'LE': 'Lecce',
        'LC': 'Lecco',
        'LI': 'Livorno',
        'LO': 'Lodi',
        'LU': 'Lucca',
        'MC': 'Macerata',
        'MN': 'Mantova',
        'MS': 'Massa-Carrara',
        'MT': 'Matera',
        'ME': 'Messina',
        'MI': 'Milano',
        'MO': 'Modena',
        'MB': 'Monza e della Brianza',
        'NA': 'Napoli',
        'NO': 'Novara',
        'NU': 'Nuoro',
        'OG': 'Ogliastra',
        'OT': 'Olbia-Tempio',
        'OR': 'Oristano',
        'PD': 'Padova',
        'PA': 'Palermo',
        'PR': 'Parma',
        'PV': 'Pavia',
        'PG': 'Perugia',
        'PU': 'Pesaro e Urbino',
        'PE': 'Pescara',
        'PC': 'Piacenza',
        'PI': 'Pisa',
        'PT': 'Pistoia',
        'PN': 'Pordenone',
        'PZ': 'Potenza',
        'PO': 'Prato',
        'RG': 'Ragusa',
        'RA': 'Ravenna',
        'RC': 'Reggio Calabria',
        'RE': 'Reggio Emilia',
        'RI': 'Rieti',
        'RN': 'Rimini',
        'RM': 'Roma',
        'RO': 'Rovigo',
        'SA': 'Salerno',
        'VS': 'Medio Campidano',
        'SS': 'Sassari',
        'SV': 'Savona',
        'SI': 'Siena',
        'SR': 'Siracusa',
        'SO': 'Sondrio',
        'TA': 'Taranto',
        'TE': 'Teramo',
        'TR': 'Terni',
        'TO': 'Torino',
        'OG': 'Ogliastra',
        'TP': 'Trapani',
        'TN': 'Trento',
        'TV': 'Treviso',
        'TS': 'Trieste',
        'UD': 'Udine',
        'VA': 'Varese',
        'VE': 'Venezia',
        'VB': 'Verbano-Cusio-Ossola',
        'VC': 'Vercelli',
        'VR': 'Verona',
        'VV': 'Vibo Valentia',
        'VI': 'Vicenza',
        'VT': 'Viterbo'
    };

    // Restituisce il nome completo dalla mappatura, o il codice se non è presente
    return provinceNameMap[provinceCode] || provinceCode;
}

// Funzione per filtrare le colonnine per provincia
function filterByProvince() {
    const selectedProvince = document.getElementById('provinceSelect').value;

    // Ottiene le colonnine della provincia selezionata dalla variabile
    const chargingStationsInProvince = chargingStationsByProvince[selectedProvince] || [];

    // Svuota il gruppo di cluster
    markerClusterGroup.clearLayers();

    // Aggiunge i nuovi marker filtrati
    addMarkers(chargingStationsInProvince);

    // Aggiorna la mappa con il nuovo gruppo di marker
    map.addLayer(markerClusterGroup);
}

// Funzione per ripristinare la visualizzazione di tutte le colonnine sulla mappa
function resetMap() {
    // Svuota il gruppo di cluster
    markerClusterGroup.clearLayers();

    // Aggiunge tutti i marker iniziali utilizzando l'elenco conservato
    addMarkers(allChargingStations);

    // Aggiorna la mappa con il nuovo gruppo di marker
    map.addLayer(markerClusterGroup);
}

var filterButton = document.getElementById('filterButton');
if(filterButton)
	filterButton.addEventListener('click', filterByProvince);
	
var resetButton = document.getElementById('resetButton');
if(resetButton)	
	resetButton.addEventListener('click', resetMap);
	
	
// Ottiene i dati dalla tua API utilizzando Axios
axios.get('/ChargingStations/markers')
    .then(response => {
        const chargingStations = response.data;

        // Conserva l'elenco completo delle colonnine
        allChargingStations = chargingStations;

        // Organizza le colonnine per provincia
        chargingStationsByProvince = chargingStations.reduce((acc, station) => {
            const provinceCode = station.addressInfo.stateOrProvince;
            acc[provinceCode] = acc[provinceCode] || [];
            acc[provinceCode].push(station);
            return acc;
        }, {});

        // Popola la dropdown con le province uniche
        populateProvinceDropdown(chargingStations);

        // Aggiunge tutti i marker iniziali
        addMarkers(chargingStations);
    })
    .catch(error => {
        console.error('Errore nel recupero dei dati dalla tua API:', error);
    });
