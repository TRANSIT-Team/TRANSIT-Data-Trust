package com.transit.geoservice.repository;

import com.transit.geoservice.domain.Zipcodesgermanystates;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZipcodesgermanystatesRepository extends JpaRepository<Zipcodesgermanystates, Integer> {
		
	
		Optional<Zipcodesgermanystates> findByZipcode(String plz);

		List<Zipcodesgermanystates> findByStateIgnoreCase(String name);
;
		@Query(" Select zipcode from Zipcodesgermanystates")
		Optional<Geometry> getCombinedGeometry(List<String> plzs);


}


