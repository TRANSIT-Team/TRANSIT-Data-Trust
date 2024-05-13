package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.Customer;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface CompanyCustomerRepository extends AbstractRepository<Customer> {
	
	
	public boolean existsByAddressId(UUID addressId);
	
	
	public List<Customer> findAllByAddressId(UUID addressId);
}
