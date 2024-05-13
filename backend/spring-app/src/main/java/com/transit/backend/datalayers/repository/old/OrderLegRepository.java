package com.transit.backend.datalayers.repository.old;

import com.transit.backend.datalayers.domain.OrderLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderLegRepository extends JpaRepository<OrderLeg, java.util.UUID> {

}