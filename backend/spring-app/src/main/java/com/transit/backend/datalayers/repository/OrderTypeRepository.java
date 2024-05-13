package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.OrderType;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderTypeRepository extends AbstractRepository<OrderType> {

}