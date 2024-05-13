package com.transit.geoservice.service;

import com.transit.geoservice.domain.Zipcodesgermanypolygon;
import org.locationtech.jts.geom.Geometry;

import java.util.List;
import java.util.Optional;

public interface ZipcodesgermanypolygonsInterface {

   Optional<Zipcodesgermanypolygon> getZipCodePolygon(int id);
   
   Optional<Zipcodesgermanypolygon> getZipCodePolygonByPLZ(String zip);

   Optional<Geometry> getCombinedZipPolygon(List<String> zips);
   
   Optional<Geometry> getCombinedZipPolygonReduceGridSize(List<String> zips, float gridSize);
   
   Optional<String> getCombinedZipPolygonAsPolyline(List<String> zips);
   
   Optional<String> getCombinedZipPolygonReduceGridSizeAsPolyline(List<String> zips, float gridSize);
   
   
   Optional<String> getZipCodesPolygonAsPolyline(String zip);
}
