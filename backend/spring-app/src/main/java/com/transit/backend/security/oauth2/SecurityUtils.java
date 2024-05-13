package com.transit.backend.security.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class SecurityUtils {
	
	
	private SecurityUtils() {
	}
	
	/**
	 * Get the login of the current user.
	 *
	 * @return the login of the current user.
	 */
	public static Optional<String> getCurrentUserLogin() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
	}
	
	private static String extractPrincipal(Authentication authentication) {
		if (authentication == null) {
			return null;
		} else if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
			return springSecurityUser.getUsername();
		} else if (authentication instanceof JwtAuthenticationToken) {
			return (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get("preferred_username");
		} else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
			Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
			if (attributes.containsKey("preferred_username")) {
				return (String) attributes.get("preferred_username");
			}
		} else if (authentication.getPrincipal() instanceof String) {
			return (String) authentication.getPrincipal();
		}
		return null;
	}
	
	public static UUID extractId(Authentication authentication) {
		if (authentication == null) {
			return null;
		} else if (authentication instanceof JwtAuthenticationToken jwtToken) {
			return UUID.fromString((String) jwtToken.getToken().getClaims().get("sub"));
		} else if (authentication.getPrincipal() instanceof DefaultOidcUser oidcUser) {
			Map<String, Object> attributes = oidcUser.getAttributes();
			if (attributes.containsKey("sub")) {
				return UUID.fromString((String) attributes.get("sub"));
			}
		}
		return null;
	}
	
	public static String extractEmailUserName(Authentication authentication) {
		String email = "email";
		if (authentication == null) {
			return null;
		} else if (authentication instanceof JwtAuthenticationToken jwtToken) {
			return String.valueOf(jwtToken.getToken().getClaims().get(email));
		} else if (authentication.getPrincipal() instanceof DefaultOidcUser oidcUser) {
			Map<String, Object> attributes = oidcUser.getAttributes();
			if (attributes.containsKey(email)) {
				return String.valueOf(attributes.get(email));
			}
		}
		return null;
	}
	
	/**
	 * Check if a user is authenticated.
	 *
	 * @return true if the user is authenticated, false otherwise.
	 */
	public static boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null;
	}
	
	/**
	 * Checks if the current user has none of the authorities.
	 *
	 * @param authorities the authorities to check.
	 * @return true if the current user has none of the authorities, false otherwise.
	 */
	public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
		return !hasCurrentUserAnyOfAuthorities(authorities);
	}
	
	/**
	 * Checks if the current user has any of the authorities.
	 *
	 * @param authorities the authorities to check.
	 * @return true if the current user has any of the authorities, false otherwise.
	 */
	public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (
				authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority))
		);
	}
	
	private static Stream<String> getAuthorities(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication instanceof JwtAuthenticationToken
				? extractAuthorityFromClaims(((JwtAuthenticationToken) authentication).getToken().getClaims())
				: authentication.getAuthorities();
		return authorities.stream().map(GrantedAuthority::getAuthority);
	}
	
	public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
		return mapRolesToGrantedAuthorities(getRolesFromClaims(claims));
	}
	
	private static List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
		return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}
	
	private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
		
		Map<String, Object> rolesMap = (Map<String, Object>) claims.getOrDefault("realm_access", new HashMap<>());
		if (rolesMap.isEmpty()) {
			return new ArrayList<>();
		} else {
			return (List<String>) rolesMap.getOrDefault("roles", new ArrayList<>());
			
		}
	}
	
	/**
	 * Checks if the current user has a specific authority.
	 *
	 * @param authority the authority to check.
	 * @return true if the current user has the authority, false otherwise.
	 */
	public static boolean hasCurrentUserThisAuthority(String authority) {
		return hasCurrentUserAnyOfAuthorities(authority);
	}
}
