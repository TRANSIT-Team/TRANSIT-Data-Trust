package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserPropertyRepository extends AbstractRepository<UserProperty> {

	
}