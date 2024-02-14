package com.project.ChargingStation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.project.repository")
@ComponentScan(basePackages = "com.project")

public class ChargingStationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChargingStationApplication.class, args);
	}
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
