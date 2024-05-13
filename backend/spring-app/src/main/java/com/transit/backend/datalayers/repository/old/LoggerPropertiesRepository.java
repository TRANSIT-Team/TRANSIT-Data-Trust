package com.transit.backend.datalayers.repository.old;

import com.transit.backend.datalayers.domain.old.LoggerProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoggerPropertiesRepository extends JpaRepository<LoggerProperties, java.util.UUID> {

}