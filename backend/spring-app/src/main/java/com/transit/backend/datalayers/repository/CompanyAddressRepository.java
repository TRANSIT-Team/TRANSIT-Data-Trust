package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.CompanyAddressId;
import com.transit.backend.transferentities.Ids;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface CompanyAddressRepository extends JpaRepository<CompanyAddress, CompanyAddressId>, JpaSpecificationExecutor<CompanyAddress>, QuerydslPredicateExecutor<CompanyAddress> {
	Optional<CompanyAddress> findByIdAndDeleted(CompanyAddressId id, boolean deleted);
	
	Optional<CompanyAddress> findByAddress_Id(UUID id);
	
	public boolean existsByAddress_Id(UUID addressId);
	
	
	public boolean deleteByAddress_Id(UUID addressId);
	
	
	public boolean deleteByCompany_Id(UUID companyId);
	
	public List<Ids> findAllProjectedBy();
	
}