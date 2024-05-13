package com.transit.backend.security.preauthregistration;

import com.transit.backend.security.authmodel.TransitAuthorities;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("securityRegistrationService")

public class Registration {
	
	
	public final boolean hasAnyAuthority(String... authorities) {
		List<String> roleList = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		
		for (String role : authorities) {
			if (roleList.contains(role)) {
				return true;
			}
		}
		String[] rolesTransit = TransitAuthorities.ADMIN_GLOBAL.getStringValues();
		for (String role : rolesTransit) {
			if (roleList.contains(role)) {
				return false;
			}
		}
		
		return true;
	}
	
	
}
