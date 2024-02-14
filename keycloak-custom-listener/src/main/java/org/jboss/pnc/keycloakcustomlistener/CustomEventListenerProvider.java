package org.jboss.pnc.keycloakcustomlistener;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
import org.keycloak.storage.user.UserLookupProvider;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * Custom ListenerProvider to print to stdout events required for events of interest efforts
 * The #{onEvent} method contains all the logic
 */
public class CustomEventListenerProvider implements EventListenerProvider {

    private static final Logger log = Logger.getLogger(CustomEventListenerProvider.class);

    private final KeycloakSession session;
    private final RealmProvider model;
 
    public CustomEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.model = session.realms();

    }

	@Override
    public void onEvent(Event event) {
        if (EventType.LOGIN.equals(event.getType())) {
            String userId = event.getUserId();
            String last_login;         
            RealmModel realm = session.getContext().getRealm();

            // Ottieni il timestamp dall'evento
            long timestampMillis = event.getTime();

            // Converti il timestamp in un oggetto LocalDateTime
            LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.of("Europe/Rome"));

            // Formatta la data e l'ora secondo le tue esigenze
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            last_login = now.format(formatter);

            UserModel user = session.users().getUserById(realm, userId);

            user.setAttribute("Last_Login", Collections.singletonList(last_login));                                               
        }
    }



    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {

    }

}