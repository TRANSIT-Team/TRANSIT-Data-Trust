package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LocationRepository extends AbstractRepository<Location> {

}