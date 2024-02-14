package com.project.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class TokenExpirationFilter extends OncePerRequestFilter {

	@Value("${myapp.keycloak.serverUrl}")
	private String keycloakServerUrl;

	@Value("${spring.security.oauth2.client.registration.ssd.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.ssd.client-secret}")
	private String clientSecret;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
	
	/*// Non dovrebbe essere necessario
    @Autowired
    private TokenExpirationFilter(OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
        this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
    }
	 */
	
	// Questo metodo ritrova il token dell'utente dalla sessione e lo invia a keycloak
	// per controllarne la validità. Se il token è scaduto invalida la sessione.
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof OAuth2AuthenticationToken token) {
			final OAuth2AuthorizedClient client =
					oAuth2AuthorizedClientRepository.loadAuthorizedClient(
							token.getAuthorizedClientRegistrationId(),
							authentication,
							request);

			final OAuth2AccessToken accessToken = client.getAccessToken();

			if (!isTokenValid(accessToken.getTokenValue())) {
				SecurityContextHolder.getContext().setAuthentication(null);
				SecurityContextHolder.clearContext();
				final HttpSession httpSession = request.getSession();

				if (httpSession != null) {
					httpSession.invalidate();
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean isTokenValid(String accessToken) throws JsonMappingException, JsonProcessingException {

		String introspectEndpoint = keycloakServerUrl + "/realms/ssd/protocol/openid-connect/token/introspect";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("token", accessToken);
		requestBody.add("client_id", clientId);
		requestBody.add("client_secret", clientSecret);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(introspectEndpoint, request, String.class);

		if (response.getStatusCode() == HttpStatus.OK) {

			// Retrieve "active" field
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(response.getBody());
			boolean isActive = jsonNode.get("active").asBoolean();
			return isActive; 

		}

		return false; // Token validation failed
	}
}