package com.transit.backend.datalayers.controller.abstractclasses.abstractmethods;


import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.RightsManageService;
import com.transit.backend.transferentities.UserTransferObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateExtraEntries<test> {
	
	
	@Autowired
	private AccessService rightsService;
	
	@Autowired
	private RightsManageService rightsManageService;
	
	public void updateExtraEntries(test entity) {
		if (entity instanceof AbstractParentEntity<?> propertyRights) {
			if (propertyRights.getProperties() != null && !propertyRights.getProperties().isEmpty()) {
				propertyRights.getProperties().forEach(prop -> updateWriteRights(prop, prop.getClass()));
			}
		}
		if (entity instanceof Car propertyRights) {
			if (propertyRights.getLocations() != null && !propertyRights.getLocations().isEmpty()) {
				propertyRights.getLocations().forEach(prop -> updateWriteRights(prop, prop.getClass()));
			}
		}
		if (entity instanceof UserTransferObject propertyRights) {
			if (propertyRights.getUser().getProperties() != null && !propertyRights.getUser().getProperties().isEmpty()) {
				propertyRights.getUser().getProperties().forEach(prop -> updateWriteRights(prop, prop.getClass()));
			}
			
		}
		
	}
	
	private void updateWriteRights(AbstractEntity prop, Class<?> clazz) {
		try {
			var result = this.rightsService.getAccess(prop.getId());
			if (result.isEmpty()) {
				rightsManageService.createEntityAndConnectIt(prop.getId(), clazz.getSimpleName(), clazz);
				
			}
		} catch (Exception e) {
			rightsManageService.createEntityAndConnectIt(prop.getId(), clazz.getSimpleName(), clazz);
			
		}
	}
	
	
}
