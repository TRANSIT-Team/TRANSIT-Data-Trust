package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CarRepository extends AbstractRepository<Car> {

}