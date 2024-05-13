package com.transit.geoservice.service.impl;

import com.transit.geoservice.domain.IdCombineMapping;
import com.transit.geoservice.domain.IdCombineMappingTemp;
import com.transit.geoservice.domain.ZipCodesCombinedPolygon;
import com.transit.geoservice.domain.Zipcodesgermanypolygon;
import com.transit.geoservice.helper.IdListHelper;
import com.transit.geoservice.repository.IdMappingRepository;
import com.transit.geoservice.repository.IdMappingTempRepository;
import com.transit.geoservice.repository.ZipCodesGermanPolygonsCombinedRepository;
import com.transit.geoservice.repository.ZipcodesgermanypolygonsRepository;
import com.transit.geoservice.service.ZipcodesgermanypolygonsInterface;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZipcodesgermanypolygonsInterfaceImplementation implements ZipcodesgermanypolygonsInterface {
	
	@Autowired
	private ZipcodesgermanypolygonsRepository zipcodesgermanypolygonsRepository;
	
	@Autowired
	private IdListHelper idListHelper;
	
	@Autowired
	private ZipCodesGermanPolygonsCombinedRepository zipCodesGermanPolygonsCombinedRepository;
	
	@Autowired
	private IdMappingRepository idMappingRepository;
	
	@Autowired
	private IdMappingTempRepository idMappingTempRepository;
	
	@Override
	public Optional<Zipcodesgermanypolygon> getZipCodePolygon(int id) {
		return zipcodesgermanypolygonsRepository.findById(Integer.valueOf(id));
	}
	
	@Override
	public Optional<Zipcodesgermanypolygon> getZipCodePolygonByPLZ(String zip) {
		return zipcodesgermanypolygonsRepository.findByPlz(zip);
	}
	
	@Override
	public Optional<Geometry> getCombinedZipPolygon(List<String> zips) {
		var id = idMappingRepository.findByZips(idListHelper.getId(zips));
		if (id.isPresent()) {
			var result = zipCodesGermanPolygonsCombinedRepository.findById(id.get().getId().toString());
			if (result.isPresent()) {
				return result.map(ZipCodesCombinedPolygon::getGeometry);
			}
		}
		IdCombineMappingTemp temp = new IdCombineMappingTemp();
		temp.setZips(idListHelper.getId(zips));
		temp = idMappingTempRepository.saveAndFlush(temp);
		var returnValue = zipcodesgermanypolygonsRepository.getCombinedGeometry(zips);
		if (returnValue.isPresent()) {
			var idCombined = new IdCombineMapping();
			if (!idMappingRepository.existsByZips(idListHelper.getId(zips))) {
				idCombined.setZips(idListHelper.getId(zips));
				idCombined.setId(temp.getId());
				idCombined = idMappingRepository.saveAndFlush(idCombined);
			} else {
				idCombined = idMappingRepository.findByZips(idListHelper.getId(zips)).get();
			}
			var polygonToSave = new ZipCodesCombinedPolygon();
			polygonToSave.setIdString(idCombined.getId().toString());
			polygonToSave.setGeometry(returnValue.get());
			zipCodesGermanPolygonsCombinedRepository.saveAndFlush(polygonToSave);
			idMappingTempRepository.deleteById(temp.getId());
		}
		return returnValue;
	}
	
	@Override
	public Optional<Geometry> getCombinedZipPolygonReduceGridSize(List<String> zips, float gridSize) {
		return zipcodesgermanypolygonsRepository.getCombinedGeometryReduceGridSize(zips, gridSize);
	}
	
	@Override
	public Optional<String> getCombinedZipPolygonAsPolyline(List<String> zips) {
		return zipcodesgermanypolygonsRepository.getCombinedGeometryAsPolyline(zips);
	}
	
	@Override
	public Optional<String> getCombinedZipPolygonReduceGridSizeAsPolyline(List<String> zips, float gridSize) {
		return zipcodesgermanypolygonsRepository.getCombinedGeometryReducePrecisionAsPolyline(zips, gridSize);
	}
	
	@Override
	public Optional<String> getZipCodesPolygonAsPolyline(String zip) {
		return zipcodesgermanypolygonsRepository.findByPlzAsPolyline(zip);
	}
}
