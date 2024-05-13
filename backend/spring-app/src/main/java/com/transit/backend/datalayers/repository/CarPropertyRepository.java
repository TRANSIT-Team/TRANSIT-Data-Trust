package com.transit.backend.datalayers.repository;


import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarPropertyRepository extends AbstractRepository<CarProperty> {

}