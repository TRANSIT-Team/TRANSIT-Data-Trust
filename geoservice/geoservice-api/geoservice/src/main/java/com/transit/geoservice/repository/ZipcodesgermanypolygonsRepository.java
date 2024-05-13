package com.transit.geoservice.repository;

import com.transit.geoservice.domain.Zipcodesgermanypolygon;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZipcodesgermanypolygonsRepository extends JpaRepository<Zipcodesgermanypolygon, Integer> {
	
	
	Optional<Zipcodesgermanypolygon> findByPlz(String plz);
	
	@Query(" Select geomunion( wkbGeometry ) from Zipcodesgermanypolygon where plz in :plzs ")
	Optional<Geometry> getCombinedGeometry(List<String> plzs);
	
	
	@Query(value = " Select ST_UNION( ST_ReducePrecision(wkb_Geometry, :gridSize) ) from zipcodesgermanypolygons where plz in :plzs ", nativeQuery = true)
	Optional<Geometry> getCombinedGeometryReduceGridSize(List<String> plzs, float gridSize);
	
	@Query(value = " Select GoogleEncodePolygon(ST_UNION( wkb_Geometry )) from zipcodesgermanypolygons where plz in :plzs ", nativeQuery = true)
	Optional<String> getCombinedGeometryAsPolyline(List<String> plzs);
	
	@Query(value = " Select GoogleEncodePolygon(ST_UNION( ST_ReducePrecision(wkb_Geometry,:gridSize) )) from zipcodesgermanypolygons where plz in :plzs ", nativeQuery = true)
	Optional<String> getCombinedGeometryReducePrecisionAsPolyline(List<String> plzs, float gridSize);
	
	@Query(value = " Select GoogleEncodePolygon(wkb_Geometry) from zipcodesgermanypolygons where plz = :plz ", nativeQuery = true)
	Optional<String> findByPlzAsPolyline(String plz);
	
	
}


