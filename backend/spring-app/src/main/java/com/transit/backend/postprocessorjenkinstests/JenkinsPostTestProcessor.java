package com.transit.backend.postprocessorjenkinstests;

import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JenkinsPostTestProcessor implements EnvironmentPostProcessor {
	
	private final Log log;
	
	public JenkinsPostTestProcessor(Log log) {
		this.log = log;
	}
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		log.info("CustomEnvironmentPostProcessor!!");
		Map<String, String> properties;
		Resource resourceLocal = new FileSystemResource("src/main/resources/application-local-security.properties");
		Resource resourceLocal2 = new FileSystemResource("src/main/resources/application-local.properties");
		Resource resourceJenkins = new FileSystemResource("/run/secrets/TRANSIT-TEST-SECURITY-CONFIG-BACKEND");
		Resource resourceall = new FileSystemResource("src/main/resources/application.properties");
		log.info("check file exists");
		try {
			log.info(resourceLocal.getFile().getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (resourceLocal.exists()) {
			try {
				properties = this.readProperties(resourceLocal.getFile());
				addProperties(properties, environment);
				properties = this.readProperties(resourceLocal2.getFile());
				addProperties(properties, environment);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else if (resourceJenkins.exists() && !resourceLocal.exists()) {
			try {
				properties = this.readProperties(resourceJenkins.getFile());
				addProperties(properties, environment);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			
			log.info("Cannot find properties file.");
		}
		if (resourceall.exists()) {
			try {
				properties = this.readProperties(resourceall.getFile());
				addProperties(properties, environment);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	private Map<String, String> readProperties(File file) {
		Map<String, String> properties = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("=");
				if (parts.length == 2) {
					properties.put(parts[0], parts[1]);
				} else {
					log.info("line are not 2 elements after split");
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return properties;
	}
	
	private void addProperties(Map<String, String> properties, ConfigurableEnvironment configurableEnvironment) {
		for (var property : properties.entrySet()) {
			//	environment.getSystemEnvironment().put("","");
			//	environment.getSystemProperties().put("","");
			configurableEnvironment.getSystemProperties().put(property.getKey(), property.getValue());
		}
	}
	
}
