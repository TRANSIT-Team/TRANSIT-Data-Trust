package com.transit.backend.security.authmodel;

import lombok.Getter;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KeycloakServiceManager {
	
	private final Keycloak kc;
	
	private final RealmResource realmResource;
	
	private final UsersResource usersResource;
	
	
	public KeycloakServiceManager(Environment env) {
		this.kc = KeycloakBuilder.builder()
				.serverUrl(env.getProperty("keycloak.admin.client.url"))
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
				.realm(env.getProperty("keycloak.admin.client.realm"))
				.clientId(env.getProperty("keycloak.admin.client.id"))
				.clientSecret(env.getProperty("keycloak.admin.client.secret"))
				.build();
		this.realmResource = this.kc.realm(env.getProperty("keycloak.admin.client.realm"));
		this.usersResource = this.realmResource.users();
	}
}
