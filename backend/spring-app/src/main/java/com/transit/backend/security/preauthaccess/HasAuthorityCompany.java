package com.transit.backend.security.preauthaccess;


import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("securityCompanyService")
public class HasAuthorityCompany {
	
	@Autowired
	WebClient webClient;
	
	
	@Autowired
	private AddRightsEntry addRightsEntry;
	
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	@Autowired
	private AccessService rightsService;
	
	public final boolean hasAnyAuthority(String... authorities) {
		List<String> roleList = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		boolean hasRole = false;
		for (String role : authorities) {
			if (roleList.contains(role)) {
				hasRole = true;
				break;
			}
		}
		if (!hasRole) {
			return false;
		}
		var servletBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
		var requestOpt = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
				.filter(ServletRequestAttributes.class::isInstance)
				.map(ServletRequestAttributes.class::cast)
				.map(ServletRequestAttributes::getRequest);
		if (requestOpt.isEmpty()) {
			return false;
		}
		var request = requestOpt.get();
		
		var uriComponents = servletBuilder.build();
		
		var lastUriComponent = uriComponents.getPathSegments().get(uriComponents.getPathSegments().size() - 1);
		
		var x = request.getParameterMap();
		
		try {
			//falls eine Entität (PUT,PATCH,GET)
			var entityUUID = UUID.fromString(lastUriComponent);
			var companyId = userHelperFunctions.getCompanyId();
			var rights = Optional.of(new AccessResponseDTO());
			try {
				rights = this.rightsService.getAccess(userHelperFunctions.getOIdFromCompany(entityUUID), companyId);
			} catch (Exception ex) {
				return false;
			}
			if (companyId == null) {
				return false;
			}
			
			if (rights.isEmpty()) {
				//kein Eintrag vorhanden
				var isGenerated = addRightsEntry.addEntry(entityUUID);
				if (isGenerated) {
					try {
						rights = this.rightsService.getAccess(userHelperFunctions.getOIdFromCompany(entityUUID), companyId);
						return true;
					} catch (Exception ex) {
						return false;
					}
				} else {
					return false;
				}
			}
			
			
			return true;
		} catch (NullPointerException e) {
			return false;
		} catch (
				IllegalArgumentException e) {
			
			return request.getMethod().equals(HttpMethod.GET.toString());
			
		}
		
	}
}
