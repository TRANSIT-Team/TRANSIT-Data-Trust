package com.transit.backend.datalayers.service.impl;


import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.repository.PackageItemRepository;
import com.transit.backend.datalayers.service.PackageItemPackageClassService;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service

public class PackageItemPackageClassServiceBean implements PackageItemPackageClassService {
	@Autowired
	private PackageItemRepository packageItemRepository;
	
	@Override
	public Optional<PackageClass> readOne(UUID packageItemId, UUID packageClassId) {
		return Optional
				.of(packageItemRepository
						.findById(packageItemId)
						.map(PackageItem::getPackageClass)
						.filter(packagePackageClass -> packagePackageClass.getId().equals(packageClassId))
						.orElseThrow(() -> new NoSuchElementFoundException(PackagePackageProperty.class.getSimpleName(), packageClassId))
				);
		
	}
}

