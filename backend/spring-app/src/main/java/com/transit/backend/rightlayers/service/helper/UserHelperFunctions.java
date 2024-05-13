package com.transit.backend.rightlayers.service.helper;


import com.transit.backend.datalayers.domain.QCompanyIDToCompanyOID;
import com.transit.backend.datalayers.domain.QUser;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.repository.CompanyIdToCompanyOIDRepository;
import com.transit.backend.datalayers.repository.UserRepository;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.security.oauth2.SecurityUtils;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserHelperFunctions {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CompanyIdToCompanyOIDRepository companyIdToCompanyOIDRepository;
	
	public UUID getCompanyId() {
		return getCompanyId(getUserID());
		
	}
	
	public UUID getCompanyId(UUID userId) {
		return this.getUser(userId).getCompany().getId();
		
	}
	
	public UUID getUserID() {
		
		var user = this.userRepository.getByKeycloakId(SecurityUtils.extractId(SecurityContextHolder.getContext().getAuthentication()));
		return user.getId();
		
	}
	
	public User getUser(UUID id) {
		if (id == null) {
			id = getUserID();
		}
		var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById(id), QUser.user);
		var opt = this.userRepository.findOne(spec);
		;
		if (opt.isPresent()) {
			return opt.get();
		} else {
			throw new RuntimeException("Cannot find user in DB");
		}
		
	}
	
	public UUID getOIdFromCompany(UUID companyID) {
		var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById(companyID), QCompanyIDToCompanyOID.companyIDToCompanyOID);
		return companyIdToCompanyOIDRepository.findOne(spec).get().getCompanyOID();
		
	}
	
}