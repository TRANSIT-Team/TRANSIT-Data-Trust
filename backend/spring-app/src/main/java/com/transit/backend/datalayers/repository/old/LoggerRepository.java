package com.transit.backend.datalayers.repository.old;

import com.transit.backend.datalayers.domain.old.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoggerRepository extends JpaRepository<Logger, java.util.UUID> {

}