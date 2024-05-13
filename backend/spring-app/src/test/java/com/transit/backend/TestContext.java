package com.transit.backend;


import com.transit.backend.security.authmodel.KeycloakServiceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestContext extends BaseIntegrationTest {
	
	public TestContext(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
	}
	
	@BeforeEach
	public void setup() {
		super.updateRestTemplate(true);
	}
	
	@Test
	public void contextLoads() {
	}
	
}


