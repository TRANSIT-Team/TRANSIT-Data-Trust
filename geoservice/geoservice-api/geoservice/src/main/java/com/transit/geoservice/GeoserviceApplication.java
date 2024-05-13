package com.transit.geoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GeoserviceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(GeoserviceApplication.class, args);
	}
	
}
