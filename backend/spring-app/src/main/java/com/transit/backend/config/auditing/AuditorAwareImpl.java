package com.transit.backend.config.auditing;

import com.transit.backend.datalayers.repository.UserRepository;
import com.transit.backend.security.oauth2.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
	
	@Autowired
	UserRepository userRepository;
	
	
	@Override
	public Optional<String> getCurrentAuditor() {
		
		var keycloakUserName = SecurityUtils.extractEmailUserName(SecurityContextHolder.getContext().getAuthentication());
		return Optional.ofNullable(keycloakUserName);
	}
}
