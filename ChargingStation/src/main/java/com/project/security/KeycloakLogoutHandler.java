package com.project.security;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class KeycloakLogoutHandler implements LogoutHandler {

	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
	private Keycloak keycloakAdminCli;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, 
      Authentication auth) {
    	
    	if(auth==null) return;
    	
    	//Logout dell'admin cli nel caso questa sia la sessione di un admin
    	//(e la sessione sia stata effettivamente aperta). L'admin cli è il
    	//client che viene utilizzato quando si accede alle funzionalità
    	//dell'admin su keycloak come ad esempio visualizzare la lista degli utenti. 
    	//NOTA: La sessione viene aperta sul realm "master".
    	if(auth.getAuthorities().toString().contains("ROLE_admin"))
    		try{
    			keycloakAdminCli.tokenManager().logout();
    		}
    		catch(NullPointerException ex) {
    			// 
    		}
    	
    	//Logout sessione normale (sessioni nel realm "ssd")
    	logoutFromKeycloak((OidcUser) auth.getPrincipal());
    }

    private void logoutFromKeycloak(OidcUser user) {
        String endSessionEndpoint = user.getIssuer() + "/protocol/openid-connect/logout";
        UriComponentsBuilder builder = UriComponentsBuilder
          .fromUriString(endSessionEndpoint)
          .queryParam("id_token_hint", user.getIdToken().getTokenValue());
        
        restTemplate.getForEntity(builder.toUriString(), String.class);
    }
    
}