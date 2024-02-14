package it.aboutbits;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jboss.logging.Logger;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

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

    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

        if ((OperationType.CREATE.equals(adminEvent.getOperationType()) ||
                OperationType.UPDATE.equals(adminEvent.getOperationType()) ||
                OperationType.DELETE.equals(adminEvent.getOperationType())) &&
               "USER".equals(adminEvent.getResourceTypeAsString())) {
        	
            log.infof("## NEW ADMIN %s EVENT", adminEvent.getOperationType());
            log.info("-----------------------------------------------------------");

            String emailPlainContent = "Admin - Account operation\n\n" +
                    "Operation: " + adminEvent.getOperationType() + "\n" +
                    "Resource Path: " + adminEvent.getResourcePath() + "\n" +
                    "Resource Type: " + adminEvent.getResourceTypeAsString();

            String emailHtmlContent = "<h1>Admin - Account operation</h1>" +
                    "<ul>" +
                    "<li>Operation: " + adminEvent.getOperationType() + "</li>" +
                    "<li>Resource Path: " + adminEvent.getResourcePath() + "</li>" +
                    "<li>Resource Type: " + adminEvent.getResourceTypeAsString() + "</li>" +
                    "</ul>";

            // Ottieni la lista degli utenti amministratori
            List<UserModel> adminUsers = getAdminUsers();
            
            for(UserModel adminUser : adminUsers) {
            
	            DefaultEmailSenderProvider senderProvider = new DefaultEmailSenderProvider(session);
	
	            try {
	                senderProvider.send(session.getContext().getRealm().getSmtpConfig(), adminUser, "Keycloak - Account Operation", emailPlainContent, emailHtmlContent);
	            } catch (EmailException e) {
	                log.error("Failed to send email", e);
	            }
            }
            log.info("-----------------------------------------------------------");
        }
    }
    
    private List<UserModel> getAdminUsers() {
        RealmModel realm = session.getContext().getRealm();
        RoleModel adminRole = realm.getRole("admin");

        // Ottieni uno stream di UserModel per gli utenti con il ruolo "admin"
        Stream<UserModel> adminUsersStream = session.users().getRoleMembersStream(realm, adminRole);

        // Converti lo stream in una lista
        List<UserModel> adminUsers = adminUsersStream.collect(Collectors.toList());

        return adminUsers;
    }


    @Override
    public void close() {

    }
}
