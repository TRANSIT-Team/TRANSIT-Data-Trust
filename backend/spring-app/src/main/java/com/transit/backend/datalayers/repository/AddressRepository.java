package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AddressRepository extends AbstractRepository<Address> {

}