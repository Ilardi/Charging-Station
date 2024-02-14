package com.project.security;


import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAdminCli {

    @Value("${myapp.keycloak.serverUrl}")
    private String keycloakServerUrl;
    
    @Value("${myapp.keycloak.username}")
    private String keycloakUsername;
    
    @Value("${myapp.keycloak.password}")
    private String keycloakPassword;
    
	@Bean
    public Keycloak keycloak() {

       return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .username(keycloakUsername)
                .password(keycloakPassword)
                .clientId("admin-cli")
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build()).build();
    }
}