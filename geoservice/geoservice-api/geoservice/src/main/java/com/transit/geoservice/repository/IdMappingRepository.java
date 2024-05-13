package com.transit.geoservice.repository;

import com.transit.geoservice.domain.IdCombineMapping;
import com.transit.geoservice.domain.ZipCodesCombinedPolygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdMappingRepository extends JpaRepository<IdCombineMapping, UUID> {
	
	
	Optional<IdCombineMapping> findByZips(String zips);
	
	boolean existsByZips(String zips);
	
}
