package com.transit.geoservice.repository;

import com.transit.geoservice.domain.ZipCodesCombinedPolygon;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZipCodesGermanPolygonsCombinedRepository extends JpaRepository<ZipCodesCombinedPolygon, String> {
	

}
