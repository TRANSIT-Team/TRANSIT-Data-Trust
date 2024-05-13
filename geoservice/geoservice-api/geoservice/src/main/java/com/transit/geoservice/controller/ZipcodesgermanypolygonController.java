package com.transit.geoservice.controller;

import com.transit.geoservice.controller.dto.PolylineDTO;
import com.transit.geoservice.domain.Zipcodesgermanypolygon;
import com.transit.geoservice.service.ZipcodesgermanypolygonsInterface;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.*;

import java.util.List;


@RestController
@RequestMapping("/zips")
public class ZipcodesgermanypolygonController {
	
	@Autowired
	private ZipcodesgermanypolygonsInterface zipcodesgermanypolygonsInterface;
	
	
	@GetMapping(path = "/{id}")
	public ResponseEntity<Zipcodesgermanypolygon> readOne(@PathVariable("id") int id) {
		
		var entity = zipcodesgermanypolygonsInterface.getZipCodePolygon(id);
		if (entity.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(entity.get(), HttpStatus.OK);
		
	}
	
	@GetMapping(path = "/zippolygons/{zip}")
	public ResponseEntity<Zipcodesgermanypolygon> readOnePLZ(@PathVariable("zip") String zip) {
		
		var entity = zipcodesgermanypolygonsInterface.getZipCodePolygonByPLZ(zip);
		return entity.map(zipcodesgermanypolygon -> new ResponseEntity<>(zipcodesgermanypolygon, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	@GetMapping(path = "/zippolylines/{zip}")
	public ResponseEntity<PolylineDTO> readOnePLZAsPolyline(@PathVariable("zip") String zip) {
		
		var entity = zipcodesgermanypolygonsInterface.getZipCodesPolygonAsPolyline(zip);
		return entity.map(zipcodesgermanypolygon -> new ResponseEntity<>(new PolylineDTO(zipcodesgermanypolygon), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	@PostMapping(path = "/combinepolygons")
	public ResponseEntity<Geometry> readCombinedPolygon(@RequestBody @Validated @NotEmpty @NotNull List<String> zips) {
		
		var entity = zipcodesgermanypolygonsInterface.getCombinedZipPolygon(zips);
		return entity.map(geometry -> new ResponseEntity<>(geometry, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	@PostMapping(path = "/combinereducepolygons")
	public ResponseEntity<Geometry> readCombinedPolygonReduced(@RequestBody @Validated @NotEmpty @NotNull List<String> zips, @RequestParam(name = "gridSize", defaultValue = "0.0001") float gridSize) {
		var entity = zipcodesgermanypolygonsInterface.getCombinedZipPolygonReduceGridSize(zips, gridSize);
		return entity.map(geometry -> new ResponseEntity<>(geometry, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping(path = "/combinepolylines")
	public ResponseEntity<PolylineDTO> readCombinedPolyline(@RequestBody @Validated @NotEmpty @NotNull List<String> zips) {
		var entity = zipcodesgermanypolygonsInterface.getCombinedZipPolygonAsPolyline(zips);
		return entity.map(geometry -> new ResponseEntity<>(new PolylineDTO(geometry), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping(path = "/combinereducepolylines")
	public ResponseEntity<PolylineDTO> readCombinedReducePolyline(@RequestBody @Validated @NotEmpty @NotNull List<String> zips, @RequestParam(name = "gridSize", defaultValue = "0.0001") float gridSize) {
		var entity = zipcodesgermanypolygonsInterface.getCombinedZipPolygonReduceGridSizeAsPolyline(zips, gridSize);
		return entity.map(geometry -> new ResponseEntity<>(new PolylineDTO(geometry), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	
}
