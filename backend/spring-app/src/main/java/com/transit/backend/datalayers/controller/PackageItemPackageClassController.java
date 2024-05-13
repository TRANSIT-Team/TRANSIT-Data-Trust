package com.transit.backend.datalayers.controller;

import com.transit.backend.datalayers.controller.assembler.wrapper.PackageItemPackageClassAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.PackageClassDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.service.PackageItemPackageClassService;
import com.transit.backend.rightlayers.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/packageitems/{packageItemId}/packageclasses")
public class PackageItemPackageClassController {
	
	@Autowired
	private PackageItemPackageClassService service;
	
	
	@Autowired
	private PackageItemPackageClassAssemblerWrapper packageItemPackageClassAssembler;
	
	@Autowired
	private PingService pingService;
	
	@GetMapping("/{packageClassId}")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).MANAGER_WAREHOUSE.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).WORKER_WAREHOUSE.stringValue" +
					")"
	)
	
	public ResponseEntity<PackageClassDTO> readOne(@PathVariable UUID packageItemId, @PathVariable UUID packageClassId) {
		pingService.available();
		Optional<PackageClass> packageClass = service.readOne(packageItemId, packageClassId);
		if (packageClass.isPresent()) {
			var packageClassDTO = packageItemPackageClassAssembler.toModel(packageClass.get(), packageItemId);
			return new ResponseEntity<>(packageClassDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
}
