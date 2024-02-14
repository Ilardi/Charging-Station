package com.project.controller;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public ResponseEntity<String> handleError() throws IOException {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
   	 	Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
   	 	
   	 	System.out.println(roles);
        
	   	boolean isUser = roles.stream().anyMatch(role -> role.getAuthority().equals("ROLE_user"));
	    boolean isAdmin = roles.stream().anyMatch(role -> role.getAuthority().equals("ROLE_admin"));
   	 
	    Resource resource = null;
	    	    
	    if(isUser == true)
	    	resource = new ClassPathResource("templates/UserError-403.html");
	    if(isAdmin == true)
	    	resource = new ClassPathResource("templates/AdminError-403.html");
	    
        InputStream inputStream = resource.getInputStream();
        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);   

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
        
    }
}

