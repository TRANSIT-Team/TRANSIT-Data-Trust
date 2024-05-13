package com.transit.backend;


import com.transit.backend.security.authmodel.KeycloakServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;

;


@Slf4j

public class CleanUP extends BaseIntegrationTest {
	
	
	private UsersResource userResource;
	
	public CleanUP(@Autowired KeycloakServiceManager keycloakServiceManager) {
		super(keycloakServiceManager);
		
		this.userResource = keycloakServiceManager.getUsersResource();
		
	}

//} implements AfterAllCallback {
//
//
//
//
//	@Override
//	public void afterAll(ExtensionContext context) throws Exception {
//		log.error("Starting Keycloak CleanUp");
//		if (!this.userResource.search("dummymy.dum*").isEmpty()) {
//			for (var user : this.userResource.search("dummymy.dum*")) {
//				this.userResource.delete(user.getId());
//			}
//		}
//		if (!this.userResource.search("dummymy2.dum*").isEmpty()) {
//			for (var user : this.userResource.search("dummymy2.dum*")) {
//				this.userResource.delete(user.getId());
//			}
//		}
//		if (!this.userResource.search("dum.dum.dummy*").isEmpty()) {
//			for (var user : this.userResource.search("dum.dum.dummy*")) {
//				this.userResource.delete(user.getId());
//			}
//		}
//
//		log.error("Ending Keycloak CleanUp");
//	}
	
	@Test
	public void after() throws Exception {
		log.error("Starting Keycloak CleanUp");
		log.error(String.valueOf(this.userResource.list().size()));
		for (var usKey : this.userResource.list(0, this.userResource.count())) {
			if (usKey.getEmail().startsWith("dummymy.dum") || usKey.getEmail().startsWith("dummymy2.dum") || usKey.getEmail().startsWith("dum.dum.dummy")) {
				this.userResource.delete(usKey.getId());
				
			}
		}
		
		log.error("Ending Keycloak CleanUp");
	}
}

