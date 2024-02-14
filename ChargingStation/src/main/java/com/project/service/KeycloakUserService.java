package com.project.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class KeycloakUserService {

    @Autowired
    private Keycloak keycloak;

    public void checkUserInactivity() {
        List<UserRepresentation> allUsers = keycloak.realm("ssd").users().list();

        for (UserRepresentation user : allUsers) {
            // Verifica se l'utente ha il ruolo "admin"
            if (userHasAdminRole(user)) {
                continue;  // Se ha il ruolo "admin", passa all'utente successivo senza eseguire alcuna azione
            }
            
            Instant lastAccess = getLastAccess(user);
            Duration inactiveDuration = Duration.between(lastAccess, Instant.now());
            
            // Assumendo un periodo di inattività massimo di 30 giorni
            if (inactiveDuration.toDays() >= 30 && user.isEnabled()) {
                // Disabilita l'account su Keycloak
            	System.out.println("Disabilito User con email: " + user.getEmail());
                user.setEnabled(false);
                keycloak.realm("ssd").users().get(user.getId()).update(user);
            }
        }
    }

    private Instant getLastAccess(UserRepresentation user) {
        String lastLogin = user.firstAttribute("Last_Login");

        // Se la lista non è vuota, prende il primo elemento
        if (lastLogin != null && !"null".equals(lastLogin)) {
            // Utilizza un DateTimeFormatter per convertire la stringa nel formato specificato
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(lastLogin, formatter);

            // Converte direttamente in Instant senza considerare il fuso orario
            return localDateTime.toInstant(java.time.ZoneOffset.UTC);
        } else {
            // Se la lista è vuota, utilizza il timestamp di creazione dell'account
            return Instant.ofEpochMilli(user.getCreatedTimestamp());
        }
    }

    private boolean userHasAdminRole(UserRepresentation user) {
        // Verifica se l'utente ha il ruolo "admin"
    	String userId = user.getId();
    	List<RoleRepresentation> userRoles = keycloak.realm("ssd").users().get(userId).roles().realmLevel().listEffective();
    	boolean hasAdminRole = userRoles.stream().anyMatch(role -> role.getName().equals("admin"));
        return hasAdminRole;
    }
}