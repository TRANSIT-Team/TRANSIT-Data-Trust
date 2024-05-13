package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.CompanyIDToCompanyOID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyIdToCompanyOIDRepository extends JpaRepository<CompanyIDToCompanyOID, UUID>, JpaSpecificationExecutor<CompanyIDToCompanyOID>, QuerydslPredicateExecutor<CompanyIDToCompanyOID> {
	
	
	public Optional<CompanyIDToCompanyOID> findCompanyIDToCompanyOIDByCompanyOID(UUID companyOId);
}
