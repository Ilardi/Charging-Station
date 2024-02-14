package com.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    @Autowired
    private KeycloakUserService keycloakUserService;

    // Esegue la funzione checkUserInactivity ogni giorno alle 00:00 AM
    @Scheduled(cron = "0 0 0 * * ?") // espressione cron per eseguire ogni giorno alle 00:00 AM
    public void performUserInactivityCheck() {
        System.out.println("Scheduler Start!");
        keycloakUserService.checkUserInactivity();
    }
}
