package com.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import com.project.model.ChargingStation;
import com.project.model.Prenotazione;
import com.project.repository.PrenotazioneRepository;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
@RestController
@RequestMapping("/ChargingStation/Welcome/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;
        
    @Autowired
    private ChargingStationController chargingStationController;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @PostMapping
    public ResponseEntity<?> createPrenotazione(@RequestBody Prenotazione prenotazione,HttpServletRequest request) {
    	    	
    	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	 String email=null;
    	 
         if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
             OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
             email=oidcUser.getEmail();
         }
    	 
    	 if(!email.isBlank())
    		 prenotazione.setemailUtente(email);
    	 else
    		 return new ResponseEntity<>("Problemi con l'autenticazione. Riprovare", HttpStatus.BAD_REQUEST);
    	 
    	 String msg = ValidationInput(prenotazione);
    	 if(msg!=null)
    		 return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);

        // Esegue la validazione della data e dell'ora
        Date now = new Date();
        Date inizio = prenotazione.getinizio();
        Date fine = prenotazione.getfine();
        
        if(inizio == null)
        	return new ResponseEntity<>("Seleziona una data e un'ora di inizio per la prenotazione.", HttpStatus.BAD_REQUEST);
        
        if(fine == null)
        	return new ResponseEntity<>("Seleziona una data e un'ora di fine per la prenotazione.", HttpStatus.BAD_REQUEST);
        
        // Converte le date in oggetti Instant
        Instant startInstant = inizio.toInstant();
        Instant endInstant = fine.toInstant();
        
        Duration durataPrenotazione = Duration.between(startInstant, endInstant);
        
        Integer idColonnina = prenotazione.getIdColonnina();
        
        Optional<ChargingStation> optionalStation = chargingStationController.getChargingStationById(idColonnina);
        
        
        if (optionalStation.isPresent()) {
            ChargingStation chargingStation = optionalStation.get();
            
            Boolean statusStation = chargingStation.isStatusType();
                                
            if(statusStation == false)
            	return new ResponseEntity<>("Colonnina selezionata non funzionante. Si prega di selezionarne un'altra!", HttpStatus.BAD_REQUEST);  
        }
        else
        	return new ResponseEntity<>("Problema con la colonnina selezionata!", HttpStatus.BAD_REQUEST);
        
        
        if (inizio != null && inizio.before(now)) {
            return new ResponseEntity<>("Seleziona una data e un'ora di inizio futura.", HttpStatus.BAD_REQUEST);
        }

        if (fine != null && (fine.before(now) || fine.before(inizio))) {
            return new ResponseEntity<>("Seleziona una data e un'ora di fine successiva a quella di inizio.", HttpStatus.BAD_REQUEST);
        }
                
        if (durataPrenotazione.toHours() > 6) {
            return new ResponseEntity<>("La durata della prenotazione non può superare le 6 ore.", HttpStatus.BAD_REQUEST);
        }
        
        // Verifica sovrapposizione di prenotazioni
        List<Prenotazione> prenotazioniSovrapposte = prenotazioneRepository
                .findOverlappingReservations(prenotazione.getIdColonnina(), inizio, fine, prenotazione.getColonnina(),prenotazione.getConnessione());

        if (!prenotazioniSovrapposte.isEmpty()) {
            return new ResponseEntity<>("Sovrapposizione con altre prenotazioni per la stessa colonnina.", HttpStatus.BAD_REQUEST);
        }

        // Esegue il salvataggio solo se la validazione è superata
        Prenotazione savedPrenotazione = prenotazioneRepository.save(prenotazione);
        
        sendConfirmationEmail(prenotazione.getIdColonnina(),email, prenotazione.getId());

        // Restituisce la prenotazione salvata
        return new ResponseEntity<>(savedPrenotazione, HttpStatus.CREATED);
    }
    
	private String ValidationInput(Prenotazione prenotazione) {
		String regex_Email= "^[a-zA-Z0-9@. ]+$";
		
	    if (!(prenotazione.getIdColonnina() instanceof Integer))
	        return "L' Id Colonnina della prenotazione non è valida!";

	    else if (!Pattern.matches(regex_Email, prenotazione.getemailUtente()))
	        return "Il campo Email Utente non è valido!";
	    
	    else if (!(prenotazione.getinizio() instanceof Date))
	        return "La Data di Inizio della prenotazione non è valida!";
	    
	    else if (!(prenotazione.getfine() instanceof Date))
	        return "La Data di Fine della prenotazione non è valida!";
	    
	    else if (!(prenotazione.getColonnina() instanceof Integer))
	        return "La Colonnina della prenotazione non è valida!";

	    else if (!(prenotazione.getConnessione() instanceof Integer))
	        return "La Connessione della prenotazione non è valida!";
	    
	    return null;
	}	  
    
    private void sendConfirmationEmail(Integer IdColonnina, String UserEmail, String prenotazioneId) {
        // Ottiene la lista degli indirizzi email degli utenti con prenotazioni per questa colonnina
       
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ssd2023@virgilio.it");
            message.setTo(UserEmail);
            message.setSubject("Conferma prenotazione colonnina di ricarica");
            message.setText("La tua prenotazione  con id " + prenotazioneId + " per la colonnina con id " + IdColonnina + "è andata a buon fine.");

            mailSender.send(message);
        } catch (MailException e) {
            // Gestisce l'eccezione
            System.err.println("Errore durante l'invio dell'email a " + UserEmail + ": " + e.getMessage());
            
        }
    }

    @GetMapping("/colonnina/{idColonnina}")
    public List<Prenotazione> getPrenotazioniByColonnina(@PathVariable Integer idColonnina) {
        return prenotazioneRepository.findByidColonnina(idColonnina);
    }
    
    @GetMapping("/utente/{EmailUtente}")
    public List<Prenotazione> getPrenotazioniByUtente(@PathVariable String EmailUtente) {
        return prenotazioneRepository.findByEmailUtente(EmailUtente);
    }
    @GetMapping("/utente/show")
    public ResponseEntity<String> showPrenotazioniUtente() throws IOException {
    	
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 String email=null;
   	    	 
	    if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
	        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
	        email=oidcUser.getEmail();
	    }
	    
	    if(email.isBlank())
	    	return new ResponseEntity<>("Problemi con l'autenticazione. Riprovare", HttpStatus.BAD_REQUEST);   		 
                           	
        // Carica il contenuto del file HTML
        Resource resource = new ClassPathResource("templates/PrenotazioniUtenti.html");
        InputStream inputStream = resource.getInputStream();
        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        // Sostituisce il placeholder {{userId}} con il valore reale dell'userId nel contenuto HTML
        htmlContent = htmlContent.replace("{{UserEmail}}", email);

        // Restituisce il contenuto del file HTML come ResponseEntity
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }
    
    @GetMapping("/colonnina/{idColonnina}/show")
    public ResponseEntity<String> showPrenotazioniColonnina(@PathVariable String idColonnina) throws IOException {
    	   	
        // Carica il contenuto del file HTML
        Resource resource = new ClassPathResource("templates/PrenotazioniColonnina.html");
        InputStream inputStream = resource.getInputStream();
        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        htmlContent = htmlContent.replace("{{idColonnina}}", idColonnina);

        // Restituisce il contenuto del file HTML come ResponseEntity
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }
    
    @DeleteMapping("/delete/{prenotazioneId}")
    public ResponseEntity<?> deletePrenotazioneById(@PathVariable String prenotazioneId) {
        // Verifica se la prenotazione esiste
        if (prenotazioneRepository.existsById(prenotazioneId)) {
        	sendCancellationEmail(prenotazioneId);
            // Elimina la prenotazione
            prenotazioneRepository.deleteById(prenotazioneId);
            return ResponseEntity.ok("Prenotazione eliminata con successo.");
        } else {
            // Restituisce una risposta in caso di prenotazione non trovata
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prenotazione non trovata.");
        }
    }
    
    private void sendCancellationEmail(String prenotazioneId) {
        // Ottiene la lista degli indirizzi email degli utenti con prenotazioni per questa colonnina
        String UserEmail = prenotazioneRepository.findEmailUtenteById(prenotazioneId);
        Optional<Prenotazione> prenotazioneOptional = prenotazioneRepository.findById(prenotazioneId);
        Integer idColonnina = null;
        if (prenotazioneOptional.isPresent()) {
            Prenotazione prenotazione = prenotazioneOptional.get();
            idColonnina = prenotazione.getIdColonnina();
        }
               
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("ssd2023@virgilio.it");
                message.setTo(UserEmail);
                message.setSubject("Annullamento prenotazione colonnina di ricarica");
                message.setText("La tua prenotazione  con id " + prenotazioneId + " per la colonnina con id " + idColonnina + "è stata annullata. Ci scusiamo per l'inconveniente.");

                mailSender.send(message);
            } catch (MailException e) {
                // Gestisce l'eccezione
                System.err.println("Errore durante l'invio dell'email a " + UserEmail + ": " + e.getMessage());
            }
    }

       
}