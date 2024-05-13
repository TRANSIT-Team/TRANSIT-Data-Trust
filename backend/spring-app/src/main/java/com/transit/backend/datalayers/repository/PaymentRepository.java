package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentRepository extends AbstractRepository<Payment> {

}