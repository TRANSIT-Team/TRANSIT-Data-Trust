package com.transit.backend.rightlayers.service.helper;


import com.transit.backend.datalayers.domain.QCompanyIDToCompanyOID;
import com.transit.backend.datalayers.repository.CompanyIdToCompanyOIDRepository;
import com.transit.backend.helper.QueryRewrite;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChangeIdCompany {
	
	@Autowired
	private CompanyIdToCompanyOIDRepository companyIdToCompanyOIDRepository;
	
	public UUID toCompanyID(UUID oaId) {
		var object = companyIdToCompanyOIDRepository.findCompanyIDToCompanyOIDByCompanyOID(oaId);
		if (object.isPresent()) {
			return object.get().getCompanyId();
		} else {
			return null;
		}
	}
	
	public UUID toOACompanyId(UUID companyID) {
		var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById((UUID) companyID), QCompanyIDToCompanyOID.companyIDToCompanyOID);
		var object =
				companyIdToCompanyOIDRepository.findOne(spec);
		if (object.isPresent()) {
			return object.get().getCompanyOID();
		} else {
			return null;
		}
		
	}
	
}
