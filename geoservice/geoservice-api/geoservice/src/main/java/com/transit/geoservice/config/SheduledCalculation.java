package com.transit.geoservice.config;

import com.transit.geoservice.domain.IdCombineMapping;
import com.transit.geoservice.domain.IdCombineMappingTemp;
import com.transit.geoservice.domain.ZipCodesCombinedPolygon;
import com.transit.geoservice.helper.IdListHelper;
import com.transit.geoservice.repository.IdMappingRepository;
import com.transit.geoservice.repository.IdMappingTempRepository;
import com.transit.geoservice.repository.ZipCodesGermanPolygonsCombinedRepository;
import com.transit.geoservice.repository.ZipcodesgermanypolygonsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SheduledCalculation {
	
	@Autowired
	private IdMappingTempRepository idMappingTempRepository;
	
	@Autowired
	private IdMappingRepository idMappingRepository;
	
	@Autowired
	private ZipCodesGermanPolygonsCombinedRepository zipCodesGermanPolygonsCombinedRepository;
	
	@Autowired
	private ZipcodesgermanypolygonsRepository zipcodesgermanypolygonsRepository;
	
	@Autowired
	private IdListHelper idListHelper;
	
	@Scheduled(fixedDelay = 3000)
	public void sheduleCombinePolygon() {
		idMappingTempRepository.findAll().forEach(entry -> {
			//in Temp Und Mapping sowie berechnung
			if (idMappingRepository.findByZips(entry.getZips()).isPresent() && zipCodesGermanPolygonsCombinedRepository.findById(idMappingRepository.findByZips(entry.getZips()).get().getId().toString()).isPresent()) {
				idMappingTempRepository.deleteById(entry.getId());
				//IN temp und mapping, aber keine Berechnung
			} else if (idMappingRepository.findByZips(entry.getZips()).isPresent() && zipCodesGermanPolygonsCombinedRepository.findById(idMappingRepository.findByZips(entry.getZips()).get().getId().toString()).isEmpty()) {
				var combinedPolygon = zipcodesgermanypolygonsRepository.getCombinedGeometry(idListHelper.getZipCodes(idMappingRepository.findByZips(entry.getZips()).get().getZips()));
				if (combinedPolygon.isPresent()) {
					var combinedEntry = new ZipCodesCombinedPolygon();
					combinedEntry.setIdString(idMappingRepository.findByZips(entry.getZips()).get().getId().toString());
					combinedEntry.setGeometry(combinedPolygon.get());
					zipCodesGermanPolygonsCombinedRepository.saveAndFlush(combinedEntry);
				}
				idMappingTempRepository.deleteById(entry.getId());
			} else {
				//in Temp und sonst nichts
				var combinedPolygon = zipcodesgermanypolygonsRepository.getCombinedGeometry(idListHelper.getZipCodes(entry.getZips()));
				if (combinedPolygon.isPresent()) {
					var idCombineMapping = new IdCombineMapping();
					idCombineMapping.setId(entry.getId());
					idCombineMapping.setZips(entry.getZips());
					idMappingRepository.saveAndFlush(idCombineMapping);
					var combinedEntry = new ZipCodesCombinedPolygon();
					combinedEntry.setIdString(idMappingRepository.findByZips(entry.getZips()).get().getId().toString());
					combinedEntry.setGeometry(combinedPolygon.get());
					zipCodesGermanPolygonsCombinedRepository.saveAndFlush(combinedEntry);
				}
				idMappingTempRepository.deleteById(entry.getId());
			}
			
		});
		
	}
}
