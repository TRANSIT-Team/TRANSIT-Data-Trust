package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WarehouseRepository extends AbstractRepository<Warehouse> {

}