
// Imposta il token CSRF per tutte le richieste Ajax globalmente
$.ajaxSetup({
  beforeSend: function(xhr) {
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);
  }
});

let connectionCount = 1; // Inizialmente c'è un campo di connessione

function resetForm() {
    // Resetta i campi del modulo aggiunta colonnina
    document.getElementById('IdColonnina').value = '';
    document.getElementById('NameDataProvider').value = '';
    document.getElementById('URLDataProvider').value = '';
    document.getElementById('NameOperatorInfo').value = '';
    document.getElementById('URLOperatorInfo').value = '';
    document.getElementById('UsageType').value = '';
    document.getElementById('MembershipRequired').checked = false;
    document.getElementById('StatusType').checked = false;
    document.getElementById('MediaItemURL').value = '';
    document.getElementById('RecentlyVerified').checked = false;
    document.getElementById('uuid').value = '';
    document.getElementById('UsageCost').value = '';
    document.getElementById('NumberOfStation').value = '';
    document.getElementById('DateLastStatusUpdate').value = '';
    document.getElementById('DateCreated').value = '';
    document.getElementById('addressTitle').value = '';
    document.getElementById('addressLine').value = '';
    document.getElementById('town').value = '';
    document.getElementById('stateProvince').value = '';
    document.getElementById('postCode').value = '';
    document.getElementById('latitude').value = '';
    document.getElementById('longitude').value = '';
    document.getElementById('countryIsoCode').value = '';
    document.getElementById('continentCode').value = '';

    // Resetta i campi delle connessioni
    for (let i = 1; i <= connectionCount; i++) {
        document.getElementById('powerKW' + i).value = '';
        document.getElementById('connectionType' + i).value = '';
        document.getElementById('FastChargeCapable' + i).checked = false;
        document.getElementById('NumberOfConnections' + i).value = '';
    }
    // Array di ID dei campi obbligatori
    const requiredFields = ['IdColonnina', 'NameOperatorInfo', 'NumberOfStation', 'DateLastStatusUpdate', 'DateCreated', 'addressTitle', 'addressLine', 'town', 'stateProvince', 'latitude', 'longitude'];
    // Resetta i campi del modulo aggiunta colonnina
    requiredFields.forEach(fieldId => {
        const fieldElement = document.getElementById(fieldId);
        // Rimuove la classe CSS 'campo-obbligatorio-vuoto'
        fieldElement.classList.remove('campo-obbligatorio-vuoto');
    });
    
    for(let i=1;i<=connectionCount;i++){
    	const fieldElement = document.getElementById('NumberOfConnections' + i);
   		fieldElement.classList.remove('campo-obbligatorio-vuoto');
    }
    
}

function apriModaleAggiuntaColonnina() {
    const moduloAggiuntaColonnina = document.getElementById('aggiuntaColonninaForm');
    moduloAggiuntaColonnina.style.display = 'block';
}

function chiudiModaleAggiuntaColonnina() {
	resetForm();
    const moduloAggiuntaColonnina = document.getElementById('aggiuntaColonninaForm');
    moduloAggiuntaColonnina.style.display = 'none';
}

function aggiungiColonnina() {
	
    // Validazione dei campi required
    const requiredFields = ['IdColonnina', 'NameOperatorInfo', 'NumberOfStation', 'DateLastStatusUpdate', 'DateCreated', 'addressTitle', 'addressLine', 'town', 'stateProvince', 'latitude', 'longitude'];
    const fieldNames = {
            'IdColonnina': 'Id Colonnina',
            'NameOperatorInfo': 'Nome Operatore',
            'NumberOfStation': 'Numero Di Colonnine',
            'DateLastStatusUpdate': 'Data Ultimo Update',
            'DateCreated': 'Data Creazione',
            'addressTitle': 'Titolo Indirizzo',
            'addressLine': 'Indirizzo',
            'town': 'Città',
            'stateProvince': 'Stato/Provincia',
            'latitude': 'Latitudine',
            'longitude': 'Longitudine'};
    let isValid = true;

    requiredFields.forEach(fieldId => {
    	const fieldElement = document.getElementById(fieldId);
        const fieldValue = document.getElementById(fieldId).value.trim();
        if (fieldValue === '') {
            isValid = false;             
            fieldElement.classList.add('campo-obbligatorio-vuoto');
        }else{
        	fieldElement.classList.remove('campo-obbligatorio-vuoto');}
    });
    
    for(let i=1;i<=connectionCount;i++){
    	const fieldElement = document.getElementById('NumberOfConnections' + i);
    	fieldValue = document.getElementById('NumberOfConnections' + i).value.trim();
    	if(fieldValue === ''){
    		isValid = false;
    		fieldElement.classList.add('campo-obbligatorio-vuoto');
    	}else{
    		fieldElement.classList.remove('campo-obbligatorio-vuoto');}
    }

    if (!isValid) {
    	alert(`Inserire i campi obbligatori colorati in rosso.`);
        return; // Non procedere se la validazione fallisce
    }
	
    const IdColonnina = document.getElementById('IdColonnina').value;
    const NameDataProvider = document.getElementById('NameDataProvider').value;
    
    const URLDataProvider = document.getElementById('URLDataProvider').value;
    const NameOperatorInfo = document.getElementById('NameOperatorInfo').value;
    const URLOperatorInfo = document.getElementById('URLOperatorInfo').value;
    const UsageType = document.getElementById('UsageType').value;
    const MembershipRequiredElement = document.getElementById('MembershipRequired');
    const MembershipRequired = MembershipRequiredElement ? MembershipRequiredElement.checked : false;
    const StatusTypeElement = document.getElementById('StatusType');
    const StatusType = StatusTypeElement ? StatusTypeElement.checked : false;
    const MediaItemURL = document.getElementById('MediaItemURL').value;
    const RecentlyVerifiedElement = document.getElementById('RecentlyVerified');
    const RecentlyVerified = RecentlyVerifiedElement ? RecentlyVerifiedElement.checked : false;
    const uuid = document.getElementById('uuid').value;
    const UsageCost = document.getElementById('UsageCost').value;
    const NumberOfStation = document.getElementById('NumberOfStation').value;
    const DateLastStatusUpdate = document.getElementById('DateLastStatusUpdate').value;
    const DateCreated = document.getElementById('DateCreated').value;
    const addressTitle = document.getElementById('addressTitle').value;
    const addressLine = document.getElementById('addressLine').value;
    const town = document.getElementById('town').value;
    const stateProvince = document.getElementById('stateProvince').value;
    const postCode = document.getElementById('postCode').value;
    const latitude = document.getElementById('latitude').value;
    const longitude = document.getElementById('longitude').value;
    const countryIsoCode = document.getElementById('countryIsoCode').value;
    const continentCode = document.getElementById('continentCode').value;
    
    const connections = [];

    for (let i = 1; i <= connectionCount; i++) {
        const powerKW = document.getElementById('powerKW' + i).value;
        const connectionType = document.getElementById('connectionType' + i).value;      
        const fastChargeCapableElement = document.getElementById('FastChargeCapable' + i);
        const fastChargeCapable = fastChargeCapableElement ? fastChargeCapableElement.checked : false;
        const numberOfConnections = document.getElementById('NumberOfConnections' + i).value;


        const connection = {
            powerKW: powerKW,
            connectionType: connectionType,
            fastChargeCapable: fastChargeCapable,
            numberOfConnections: numberOfConnections
        };

        connections.push(connection);
    }
    
    const AddressInfo = {
    	addressTitle: addressTitle,
    	addressLine: addressLine,
    	town: town,
    	stateOrProvince: stateProvince,
    	postCode: postCode,
    	latitude: latitude,
    	longitude: longitude,
    	countryIsoCode: countryIsoCode,
    	continentCode: continentCode
    };
    
    const newStation = {
    	
    	id: IdColonnina,
    	nameDataProvider: NameDataProvider,
    	urldataProvider: URLDataProvider,
    	nameOperatorInfo: NameOperatorInfo,
    	urloperatorInfo: URLOperatorInfo,
    	usageType: UsageType,
    	membershipRequired: MembershipRequired,
    	statusType: StatusType,
    	mediaItemURL: MediaItemURL,
    	recentlyVerified: RecentlyVerified,
    	uuid: uuid,
    	usageCost: UsageCost,
    	addressInfo: AddressInfo,
    	connectionInfo: connections,
    	numberOfStation: NumberOfStation,
    	dateLastStatusUpdate: DateLastStatusUpdate,
    	dateCreated: DateCreated
    };

    console.log('Station: ',newStation);
    // Recupera altri dettagli della colonnina dal modulo

    // Effettua una richiesta AJAX al backend per aggiungere la nuova colonnina
    $.ajax({
        type: 'POST',
        url: '/addChargingStation',
        contentType: 'application/json',
        data: JSON.stringify(newStation),
        success: function (response) {
            // Chiudi il modulo
    		chiudiModaleAggiuntaColonnina();
            alert(response);
            location.reload(); // Ricarica la pagina HTML dell'admin
        },
        error: function (error) {
            console.error('Errore durante l\'aggiunta della colonnina:', error);
            //alert('Errore durante l\'aggiunta della colonnina. Riprovare più tardi.');
            alert(error.responseText);
        }
    });
    
}


function aggiungiConnessione() {
    connectionCount++;

    const container = document.getElementById('connectionFields');

    const newConnectionDiv = document.createElement('div');
    newConnectionDiv.id = 'connection' + connectionCount;
    newConnectionDiv.className = 'form-column'; // Aggiunge la classe form-column

    newConnectionDiv.innerHTML = `
        <label for="powerKW${connectionCount}">Potenza KW:</label>
        <input type="number" id="powerKW${connectionCount}">
        
        <label for="connectionType${connectionCount}">Tipo Connessione:</label>
        <input type="text" id="connectionType${connectionCount}">                                                           
        
        <label for="FastChargeCapable${connectionCount}">Ricarica Veloce:</label>
        <input type="checkbox" id="FastChargeCapable${connectionCount}">
        
        <label for="NumberOfConnections${connectionCount}">Numero Connessioni:</label>
        <input type="number" id="NumberOfConnections${connectionCount}" required>  

        <button class="rimuoviConnessioneButton">Rimuovi Connessione</button>
    `;

    container.appendChild(newConnectionDiv);
}

function rimuoviConnessione(connectionNumber) {
    const container = document.getElementById('connectionFields');
    const connectionToRemove = document.getElementById('connection' + connectionNumber);
    connectionCount--;
    container.removeChild(connectionToRemove);
}

$(document).ready(function () {
    // Inizializza l'ultimo timestamp verificato come null
    var lastCheckedTimestamp = localStorage.getItem('lastCheckedTimestamp');
   	
    if(lastCheckedTimestamp === "undefined" || lastCheckedTimestamp === "null") {
        lastCheckedTimestamp = 0;
    }
    // Funzione per controllare i nuovi eventi
    function checkForNewEvents() {
        $.ajax({
            url: "/ChargingStation/Welcome/Admin/checkNewEvents",
            type: "GET",
            data: { lastChecked: lastCheckedTimestamp},
            success: function (data) {
                if (data.hasNewEvents) {
                    // Ci sono nuovi eventi, aggiorna lo stile del link					
                    $("#eventsLink").css("color", "red");
                }

                // Aggiorna l'ultimo timestamp verificato
                lastCheckedTimestamp = data.latestTimestamp;
             	
            },
            complete: function () {
                // Ripete il controllo ogni 1 minuto (60000 millisecondi)
                setTimeout(checkForNewEvents, 60000);
            }
        });
    }

    // Avvia la funzione per il controllo dei nuovi eventi
    checkForNewEvents();
    
    // Rimuove lo stile rosso quando l'utente fa clic sul link
    $("#eventsLink").click(function () {
    	// Salva l'ultimo timestamp verificato in localStorage
        localStorage.setItem('lastCheckedTimestamp', lastCheckedTimestamp);
        $(this).css("color", "#b2d3de");  // Rimuove il colore specifico, torna al valore predefinito
    });        
});
    
    
var aggiungiColonninaBtn = document.getElementById('aggiungiColonninaBtn');	
aggiungiColonninaBtn.addEventListener('click', apriModaleAggiuntaColonnina);

// Aggiunge un event listener al container per gestire i clic sui bottoni "Rimuovi Connessione"
const container = document.getElementById('connectionFields');
container.addEventListener('click', function (event) {
    const clickedElement = event.target;

    // Verifica se l'elemento cliccato è un bottone "Rimuovi Connessione"
    if (clickedElement.classList.contains('rimuoviConnessioneButton')) {
        // Ottiene il padre (div) dell'elemento cliccato e lo rimuove dal container
        rimuoviConnessione(connectionCount);
    }
});

var AddConnectionBtn = document.getElementById('AddConnectionBtn');	
AddConnectionBtn.addEventListener('click', aggiungiConnessione);

var ConfirmAddBtn = document.getElementById('ConfirmAddBtn');	
ConfirmAddBtn.addEventListener('click', aggiungiColonnina);

var CancelBtn = document.getElementById('CancelBtn');	
CancelBtn.addEventListener('click', chiudiModaleAggiuntaColonnina);

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