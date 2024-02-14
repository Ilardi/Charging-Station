package com.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
public class SecurityConfig {
	
	@Autowired
    private KeycloakLogoutHandler keycloakLogoutHandler;	
	
	@Autowired
	private TokenExpirationFilter tokenExpirationFilter;
	
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            var authority = authorities.iterator().next();

            if (authority instanceof OidcUserAuthority) {
                var oidcUserAuthority = (OidcUserAuthority) authority;
                var userInfo = oidcUserAuthority.getUserInfo();             
                var roles = (Collection<String>) userInfo.getClaimAsStringList("roles");
                mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                
            }
                       
            return mappedAuthorities;
        };
    }

    Collection<GrantedAuthority> generateAuthoritiesFromClaim(Collection<String> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
    }


	@Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
              		
		//Controlla se la sessione è scaduta prima di verificare il resto.
		http.addFilterBefore(tokenExpirationFilter, BasicAuthenticationFilter.class);		
		
		http
			.oauth2Client(Customizer.withDefaults())
            .oauth2Login(Customizer.withDefaults());

        http
        	.logout(t -> t
        		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        		.permitAll()
	    		.addLogoutHandler(keycloakLogoutHandler)	
	            .logoutSuccessUrl("/ChargingStation/Welcome")
	        );
        
        http
            .sessionManagement(t -> t
        		.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            );
        
        //XSS protection
        http.headers(headers -> headers
               .xssProtection(xss -> xss
            		   .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
               .contentSecurityPolicy(cps -> cps.
            		   policyDirectives("script-src 'self' https://unpkg.com https://code.jquery.com;"))
        );

        http
            .authorizeHttpRequests(t -> t
                .requestMatchers("/ChargingStation/Welcome").permitAll()
                .requestMatchers("/ChargingStation/Welcome/prenotazioni").hasRole("user")
                .requestMatchers("/ChargingStation/Welcome/prenotazioni/utente/{EmailUtente}").hasRole("user")
                .requestMatchers("/ChargingStation/Welcome/prenotazioni/utente/show").hasRole("user")
                .requestMatchers("/ChargingStation/Welcome/prenotazioni/utente/delete/{prenotazioneId}").hasRole("user")
                .requestMatchers("/ChargingStation/Welcome/prenotazioni/colonnina/{idColonnina}").hasRole("admin")
                .requestMatchers("/ChargingStation/Welcome/prenotazioni/colonnina/{idColonnina}/show").hasRole("admin")
                .requestMatchers("/delete/{prenotazioneId}").hasRole("user")
                .requestMatchers("/ChargingStation/Welcome/Logged").hasRole("user")
                .requestMatchers("/ChargingStation/Welcome/Admin").hasRole("admin")
                .requestMatchers("/ChargingStations/markers").permitAll()
                .requestMatchers("/script.js").permitAll()
                .requestMatchers("/ChargingStation/{id}").hasRole("user")  
                //.requestMatchers("/ChargingStations/{province}").hasRole("user")  
                .requestMatchers("/Error-403").permitAll()
                .requestMatchers("/ChargingStation/modStatusStation/{id}").hasRole("admin")
                .requestMatchers("/addChargingStation").hasRole("admin")
                .requestMatchers("/deleteChargingStation/{id}").hasRole("admin")
                .requestMatchers("/ChargingStation/Welcome/Admin/InfoUtenti").hasRole("admin")
                .requestMatchers("/ChargingStation/Welcome/Admin/users").hasRole("admin")
                .requestMatchers("/ChargingStation/Welcome/Admin/deleteUser").hasRole("admin")
                .requestMatchers("/ChargingStation/Welcome/Admin/statusUser").hasRole("admin")
                .requestMatchers("/ChargingStation/Welcome/Admin/checkNewEvents").hasRole("admin")
                .requestMatchers("/ChargingStation/Welcome/Admin/events").hasRole("admin")


                //.requestMatchers("/ChargingStations/{province}").hasRole("admin")
                .anyRequest().authenticated()
            );     
        
        // CSRF Protection
    	CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    	XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
    	// set the name of the attribute the CsrfToken will be populated on
    	delegate.setCsrfRequestAttributeName("_csrf");
    	// Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
    	// default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
    	CsrfTokenRequestHandler requestHandler = delegate::handle;
    	http
    		.csrf((csrf) -> csrf
    			.csrfTokenRepository(tokenRepository)
    			.csrfTokenRequestHandler(requestHandler)
    		);

        return http.build();
    }
			
}
