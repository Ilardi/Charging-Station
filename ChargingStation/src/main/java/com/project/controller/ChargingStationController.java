package com.project.controller;

import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.model.ChargingStation;
import com.project.model.ChargingStationMarker;
import com.project.model.Connection;
import com.project.repository.ChargingStationRepository;
import com.project.repository.PrenotazioneRepository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;


@RestController
public class ChargingStationController {
	
	@Autowired
	private ChargingStationRepository repo;
	
	@Autowired
	private PrenotazioneRepository prenotazioneRepository;
	
	@Autowired
	private Keycloak keycloak;
	
    @Autowired
    private JavaMailSender mailSender;
		
	/*@GetMapping("/ChargingStations")
	public List<ChargingStation> getAllChargingStation(){
		return repo.findAll();
	}*/
	
	@GetMapping("/ChargingStation/{id}")
	public Optional<ChargingStation> getChargingStationById(@PathVariable Integer id){
		return repo.findById(id);
	}
	
	/*@GetMapping("/ChargingStations/{province}")
	public List<ChargingStation> getChargingStationsProvince(@PathVariable String province) {
		return repo.findByAddressInfoStateOrProvince(province);
	}*/
	
    @GetMapping("/ChargingStation/Welcome")
    public ResponseEntity<String> getChargingStationHTML() throws IOException {
        // Carica il contenuto del file HTML
        Resource resource = new ClassPathResource("templates/Welcome.html");
        InputStream inputStream = resource.getInputStream();
        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        // Restituisce il contenuto del file HTML come ResponseEntity
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }
    
    @GetMapping("/ChargingStation/Welcome/Logged")
    public ResponseEntity<String> getChargingStationHTMLLogged() throws IOException {
        // Carica il contenuto del file HTML
        Resource resource = new ClassPathResource("templates/WelcomeUser.html");
        InputStream inputStream = resource.getInputStream();
        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        // Restituisce il contenuto del file HTML come ResponseEntity
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }
    
    @GetMapping("/ChargingStation/Welcome/Admin")
    public ResponseEntity<String> getChargingStationHTMLAdmin() throws IOException {
        // Carica il contenuto del file HTML
        Resource resource = new ClassPathResource("templates/WelcomeAdmin.html");
        InputStream inputStream = resource.getInputStream();
        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        // Restituisce il contenuto del file HTML come ResponseEntity
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }
	        
    @GetMapping("/ChargingStations/markers")
    public List<ChargingStationMarker> getAllChargingStationMarkers() {
        List<ChargingStation> chargingStations = repo.findAll();
                
        return chargingStations.stream()
                .map(station -> new ChargingStationMarker(station.getId(),station.getAddressInfo(), station.getConnectionInfo(), station.getNameOperatorInfo(), station.getURLOperatorInfo(), station.getUsageCost(), station.isMembershipRequired(), station.isStatusType(), station.getMediaItemURL(), station.isRecentlyVerified(), station.getNumberOfStation() ))
                .collect(Collectors.toList());
    }
    
    @PostMapping("/ChargingStation/modStatusStation/{id}")
    public ResponseEntity<String> modStatusStation(@PathVariable Integer id) {    
    	Optional<ChargingStation> optionalStation = repo.findById(id);
    	if(optionalStation.isPresent()) {
    		ChargingStation chargingStation = optionalStation.get();
    		Boolean Status = chargingStation.isStatusType();
    		if(Status == true) {
    			chargingStation.setStatusType(false);
                // Invia una mail agli utenti con prenotazioni
                sendCancellationEmail(id);    
    			prenotazioneRepository.deleteByidColonnina(id);			
    		}
    		else
    			chargingStation.setStatusType(true);
    		repo.save(chargingStation);
    		return new ResponseEntity<>("Stato della colonnina con id : " +chargingStation.getId()+ " cambiato correttamente", HttpStatus.OK);
    	}
    	else
    		return new ResponseEntity<>("Errore colonnina di ricarica con id : " + id + " non esiste.", HttpStatus.BAD_REQUEST);
    	
    }
    
    private void sendCancellationEmail(Integer id) {
        // Ottiene la lista degli indirizzi email degli utenti con prenotazioni per questa colonnina
        Set<String> bookedUserEmailsSet = new HashSet<>(prenotazioneRepository.findEmailUtenteByidColonnina(id));
        
        List<String> bookedUserEmails = new ArrayList<>(bookedUserEmailsSet);
        
        for (String userEmail : bookedUserEmails) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("ssd2023@virgilio.it");
                message.setTo(userEmail);
                message.setSubject("Annullamento prenotazione colonnina di ricarica");
                message.setText("Le tue prenotazioni per la colonnina di ricarica con id " + id + " sono state annullate. Ci scusiamo per l'inconveniente.");

                mailSender.send(message);
            } catch (MailException e) {
                // Gestisci l'eccezione
                System.err.println("Errore durante l'invio dell'email a " + userEmail + ": " + e.getMessage());
                
            }
        }
    }
 
    
	@PostMapping("/addChargingStation")
	public ResponseEntity<String> saveStation(@RequestBody ChargingStation station) {
		
	    if (!isValidChargingStation(station)) {
	        return new ResponseEntity<>("Alcuni campi obbligatori sono mancanti o non validi.", HttpStatus.BAD_REQUEST);
	    }
	    
		if(repo.existsById(station.getId()))
			return new ResponseEntity<>("Una colonnina con questo ID esiste già!", HttpStatus.BAD_REQUEST);
		
	    
	    String msg = ValidationInputChargingStation(station);
	    if(msg!=null) {
	    	System.out.println(msg);
	    	return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
	    	}
	    
		repo.save(station);
	    return new ResponseEntity<>("Aggiunta colonnina di ricarica con id : " + station.getId(), HttpStatus.OK);
	}
	
	private boolean isValidChargingStation(ChargingStation station) {
	    // Validazione dei campi required
	    if (station.getId() == null
	    		||station.getId() <= 0	    		
	            || StringUtils.isBlank(station.getNameOperatorInfo())
	            || station.getNumberOfStation() == null
	            || station.getNumberOfStation() <= 0	            
	            || StringUtils.isBlank(station.getDateLastStatusUpdate())
	            || StringUtils.isBlank(station.getDateCreated())
	            || StringUtils.isBlank(station.getAddressInfo().getAddressTitle())
	            || StringUtils.isBlank(station.getAddressInfo().getAddressLine())
	            || StringUtils.isBlank(station.getAddressInfo().getTown())
	            || StringUtils.isBlank(station.getAddressInfo().getStateOrProvince())
	            || StringUtils.isBlank(String.valueOf(station.getAddressInfo().getLatitude()))
	            || StringUtils.isBlank(String.valueOf(station.getAddressInfo().getLongitude()))) {
	        return false;
	    }

        List<Connection> connections = station.getConnectionInfo();

        if (connections != null && !connections.isEmpty()) {
            for (int i = 0; i < connections.size(); i++) {
                Connection connection = connections.get(i);
                if (connection != null && (connection.getNumberOfConnections() == null || connection.getNumberOfConnections() <= 0)) {
                    return  false;
                }
            }
        }
        
	    return true;
	}
	
	private String ValidationInputChargingStation(ChargingStation station) {
		String regex_Indirizzo= "^[a-zA-Z0-9/, ]+$";
		String regex= "^[a-zA-Z0-9 ]+$";
		String regex_URL= "^[a-zA-Z0-9/:.]+$";
		String regex_UUID= "^[a-zA-Z0-9-]+$";
		String regex_Costo= "^[0-9-,.]+$";
		String regex_Data= "^[0-9-/]+$";
		String regex_Citta= "^[a-zA-Z ]+$";
		String regex_CAP= "^[0-9]+$";
		String regex_Connessione= "^[a-zA-Z0-9() ]+$";
		
	    if (!(station.getId() instanceof Integer))
	        return "Il campo Id Colonnina non è di tipo Integer!";

	    else if (!Pattern.matches(regex, station.getNameDataProvider()))
	        return "Il campo Nome Provider non è valido!";
	    
	    else if (!Pattern.matches(regex_URL, station.getURLDataProvider()))
	        return "Il campo URL Provider non è valido!";
	    
	    else if (!Pattern.matches(regex, station.getNameOperatorInfo()))
	        return "Il campo Nome Operatore non è valido!";
	    
	    else if (!Pattern.matches(regex_URL, station.getURLOperatorInfo()))
	        return "Il campo URL Operatore non è valido!";
	    
	    else if (!Pattern.matches(regex, station.getUsageType()))
	        return "Il campo Tipologia Utilizzo non è valido!";
	    
	    else if (!(station.isMembershipRequired() instanceof Boolean))
	        return "Il campo Iscrizione Richiesta non è di tipo booleano!";
	    
	    else if (!(station.isStatusType() instanceof Boolean))
	        return "Il campo Stato non è di tipo booleano!";
	    
	    else if (!Pattern.matches(regex_URL, station.getMediaItemURL()))
	        return "Il campo Media URL non è valido!";
	    
	    else if (!(station.isRecentlyVerified() instanceof Boolean))
	        return "Il campo Recentemente Verificata non è di tipo booleano!";
	    
	    else if (!Pattern.matches(regex_UUID, station.getUuid()))
	        return "Il campo Uuid non è valido!";
	    
	    else if (!Pattern.matches(regex_Costo, station.getUsageCost()))
	        return "Il campo Costo non è valido!";
	    
	    else if (!(station.getNumberOfStation() instanceof Integer))
	        return "Il campo Numero Di Colonnine non è di tipo Integer!";
	    
	    else if (!Pattern.matches(regex_Data, station.getDateLastStatusUpdate()))
	        return "Il campo Data Ultimo Update non è valido!";
	    
	    else if (!Pattern.matches(regex_Data, station.getDateCreated()))
	        return "Il campo Data Creazione non è valido!";
	    
	    else if (!Pattern.matches(regex_Indirizzo, station.getAddressInfo().getAddressTitle()))
	        return "Il campo Titolo Indirizzo non è valido!";
	    
	    else if (!Pattern.matches(regex_Indirizzo, station.getAddressInfo().getAddressLine()))
	        return "Il campo Indirizzo non è valido!";
	    
	    else if (!Pattern.matches(regex_Citta, station.getAddressInfo().getTown()))
	        return "Il campo Città non è valido!";
	    
	    else if (!Pattern.matches(regex_Citta, station.getAddressInfo().getStateOrProvince()))
	        return "Il campo Stato/Provincia non è valido!";
	    
	    else if (!Pattern.matches(regex_CAP, station.getAddressInfo().getPostCode()))
	        return "Il campo CAP non è valido!";
	    	    
	    else if (!Pattern.matches(regex_Citta, station.getAddressInfo().getCountryIsoCode()))
	        return "Il campo Codice Iso del Paese non è valido!";
	    
	    else if (!Pattern.matches(regex_Citta, station.getAddressInfo().getContinentCode()))
	        return "Il campo Codice del Continente non è valido!";
	    
	    
        List<Connection> connections = station.getConnectionInfo();

        for (int i = 0; i < connections.size(); i++) {
            Connection connection = connections.get(i);
    	    if (!(connection.getPowerKW() instanceof Integer))
    	        return "Il campo Potenza KW non è di tipo Integer!";    	  
    	    else if (!Pattern.matches(regex_Connessione, connection.getConnectionType()))
    	        return "Il campo Tipo Connessione "+ i +" non è valido!";
    	    else if (!(connection.isFastChargeCapable() instanceof Boolean))
    	        return "Il campo Ricarica Veloce non è di tipo booleano!";
    	    else if (!(connection.getNumberOfConnections() instanceof Integer))
    	        return "Il Numero Connessioni non è di tipo Integer!";  
    	    
        }
	    
	    return null;
	}	    
	
	@DeleteMapping("/deleteChargingStation/{id}")
	public ResponseEntity<String> deleteStation(@PathVariable Integer id) {
		if(repo.findById(id).orElse(null)!= null) {
			sendCancellationEmail(id);
			prenotazioneRepository.deleteByidColonnina(id);			
			repo.deleteById(id);
			return new ResponseEntity<>("Eliminata colonnina di ricarica con id : " +id, HttpStatus.OK);}
		else
			return new ResponseEntity<>("Errore colonnina di ricarica con id : " + id + " non esiste.", HttpStatus.BAD_REQUEST);
	}
	
    @GetMapping("/ChargingStation/Welcome/Admin/InfoUtenti")
    public ResponseEntity<String> getInfoUtentiHTML() throws IOException {
        // Carica il contenuto del file HTML
        Resource resource = new ClassPathResource("templates/Utenti.html");
        InputStream inputStream = resource.getInputStream();
        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        // Restituisce il contenuto del file HTML come ResponseEntity
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }	
	
	@GetMapping("/ChargingStation/Welcome/Admin/users")
    public List<UserRepresentation> getUsers() {
        return keycloak.realm("ssd").users().list();
    }
	
    @GetMapping("/ChargingStation/Welcome/Admin/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam("id") String userId) {
        try {                      
            // Verifica se l'utente ha il ruolo "admin"
            if (userHasAdminRole(userId)) {
                // Gestisce il caso in cui l'utente è un amministratore inviando un messaggio di errore
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Non puoi eliminare un utente Admin");
            }

            // Per eliminare l'utente da Keycloak        
            keycloak.realm("ssd").users().delete(userId);

            // Ritorna una risposta di successo
            return ResponseEntity.ok("Utente eliminato con successo");
        } catch (Exception e) {
            // Gestione dell'errore restituendo un messaggio di errore
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore nella eliminazione dell' utente");
        }
    }
    
    @GetMapping("/ChargingStation/Welcome/Admin/statusUser")
    public ResponseEntity<String> StatusUser(@RequestParam("id") String userId) {
        try {                      
            // Verifica se l'utente ha il ruolo "admin"
            if (userHasAdminRole(userId)) {
                // Gestisce il caso in cui l'utente è un amministratore un messaggio di errore
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Non puoi modificare lo stato di un utente Admin");
            }

         // Ottenere l'oggetto UserRepresentation per l'utente specifico
            UserRepresentation user = keycloak.realm("ssd").users().get(userId).toRepresentation();

            // Ottenere lo stato corrente di attivazione
            boolean isEnabled = user.isEnabled();

            // Inverte lo stato di attivazione
            isEnabled = !isEnabled;

            // Imposta il nuovo stato di attivazione
            user.setEnabled(isEnabled);

            // Aggiorna l'utente utilizzando il metodo update
            keycloak.realm("ssd").users().get(userId).update(user);
            
            // Ritorna una risposta di successo
            return ResponseEntity.ok("Stato dell'utente modificato con successo");
        } catch (Exception e) {
            // Gestione dell'errore restituendo un messaggio di errore
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore nella modifica dello stato dell' utente");
        }
    }
    
    // Funzione per verificare se un utente ha il ruolo "admin"
    private boolean userHasAdminRole(String userId) {
        List<RoleRepresentation> userRoles = keycloak.realm("ssd").users().get(userId).roles().realmLevel().listEffective();
        return userRoles.stream().anyMatch(role -> role.getName().equals("admin"));
    }
    
    @GetMapping("/ChargingStation/Welcome/Admin/checkNewEvents")
    @ResponseBody
    public Map<String, Object> checkNewEvents(@RequestParam(name = "lastChecked", required = false) Long lastChecked) {
        List<AdminEventRepresentation> adminEvents = filterAdminEvents();
        // Trova la data dell'evento più recente
        OptionalLong latestTimestamp = adminEvents.stream().mapToLong(AdminEventRepresentation::getTime).max();
        
        boolean hasNewEvents = false;
        if (latestTimestamp.isPresent() && (lastChecked == null || latestTimestamp.getAsLong() > lastChecked)) {
            hasNewEvents = true;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("hasNewEvents", hasNewEvents);
        if (latestTimestamp.isPresent()) {
            response.put("latestTimestamp", latestTimestamp.getAsLong());
        }
        return response;
    }

    
    @GetMapping("/ChargingStation/Welcome/Admin/events")
    public String eventsList() {
        List<AdminEventRepresentation> adminEvents = filterAdminEvents();

        StringBuilder result = new StringBuilder();
        
        result.append("<h2>Keycloak Events</h2>\n");
        result.append("<p><a href=\"/ChargingStation/Welcome/Admin\">Torna alla Home</a></p><br>\n");  // Link Home

        
        result.append("<table border=\"1\">\n");
        result.append("<tr><th>Operazione</th><th>Tipo Risorsa</th><th>Path Risorsa</th><th>Data</th></tr>\n");
        
        // Creazione di un oggetto SimpleDateFormat per formattare la data
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // Impostazione del fuso orario italiano
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        
        for (AdminEventRepresentation event : adminEvents) {      
            result.append("<tr>");
            result.append("<td>").append(event.getOperationType()).append("</td>");
            result.append("<td>").append(event.getResourceType()).append("</td>");
            result.append("<td>").append(event.getResourcePath()).append("</td>");
            result.append("<td>").append(sdf.format(event.getTime())).append("</td>");
            result.append("</tr>\n");
        }

        result.append("</table>");

        return result.toString();
    }
    
    private List<AdminEventRepresentation> filterAdminEvents() {
        List<AdminEventRepresentation> adminEvents = keycloak.realm("ssd").getAdminEvents();
        List<AdminEventRepresentation> filteredEvents = new ArrayList<>();

        for (AdminEventRepresentation adminEvent : adminEvents) {
            if (("CREATE".equals(adminEvent.getOperationType()) ||
                 "UPDATE".equals(adminEvent.getOperationType()) ||
                 "DELETE".equals(adminEvent.getOperationType())) &&
                "USER".equals(adminEvent.getResourceType())) {
                filteredEvents.add(adminEvent);
            }
        }
        return filteredEvents;
    }

    private String formatDate(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
	
}
