package com.transit.backend.security.preauthaccess;


import com.transit.backend.config.EndpointsByPath;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
public class GetFilterExpression {
	
	
	@Autowired
	WebClient webClient;
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	@Autowired
	private EndpointsByPath endpointsByPath;
	
	@Autowired
	private AccessService rightsService;
	
	public String overwriteQueryWithEntityId(String query, boolean createdByMyCompany) {
		return overwriteQueryWithEntityId(query, null, createdByMyCompany);
	}
	
	
	public String overwriteQueryWithEntityId(String query, String extraIdPath, boolean createdByMyCompany) {
		return overwriteQueryWithEntityIdWithURINumber(query, extraIdPath, 1, createdByMyCompany);
	}
	
	public String overwriteQueryWithEntityIdWithURINumber(String query, String extraIdPath, int whichLastUriComponent, boolean createdByMyCompany) {
		var servletBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
		
		var uriComponents = servletBuilder.build();
		
		var lastUriComponent = uriComponents.getPathSegments().get(uriComponents.getPathSegments().size() - whichLastUriComponent);
		var companyId = userHelperFunctions.getCompanyId();
		if (companyId == null) {
			return null;
		}
		String typeClazz = endpointsByPath.readOne(lastUriComponent);
		return overwriteQueryWithEntityIdWithURINumber(query, extraIdPath, createdByMyCompany, companyId, typeClazz);
	}
	
	public String overwriteQueryWithEntityIdWithURINumber(String query, String extraIdPath, boolean createdByMyCompany, UUID companyId, String typeClazz) {
		
		
		var rightsForEntity = this.rightsService.getAccessClazz(Objects.requireNonNull(typeClazz), createdByMyCompany, companyId);
		
		var values = new StringBuilder();
		if (rightsForEntity.getObjects().isEmpty()) {
			return null;
		}
		if (extraIdPath != null) {
			values.append(extraIdPath).append(".");
		}
		
		values.append("id=in=(");
		Set<UUID> nodeIds = new HashSet<>();
		for (var right : rightsForEntity.getObjects()) {
			nodeIds.add(right.getObjectId());
		}
		for (var node : nodeIds) {
			values.append(node);
			values.append(",");
		}
		
		if (values.toString().endsWith(",")) {
			values.deleteCharAt(values.length() - 1);
		}
		values.append(")");
		//hve to implement more
		if (query.isBlank()) {
			return values.toString();
		} else {
			return "(" + query + ") and " + values;
		}
		
	}
}
	

