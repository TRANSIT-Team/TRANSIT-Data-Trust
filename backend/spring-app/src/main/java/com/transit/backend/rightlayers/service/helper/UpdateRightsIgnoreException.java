package com.transit.backend.rightlayers.service.helper;

import com.transit.backend.rightlayers.controller.dto.RIghtsDtoCoreProperties;
import com.transit.backend.rightlayers.controller.dto.RightsDtoCore;
import com.transit.backend.rightlayers.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Component
public class UpdateRightsIgnoreException {
	
	@Autowired
	private AccessService rightsService;
	
	public RightsDtoCore updateRightsIgnoreException(UUID id, UUID subCompanyIdSubOrder, List<String> readProperties, List<String> writeProperties, UUID parentCompanyId) {
		try {
			rightsService.getAccess(id, subCompanyIdSubOrder, parentCompanyId);
			if (parentCompanyId == null) {
				rightsService.updateConnection(new HashSet<>(readProperties), new HashSet<>(writeProperties), id, subCompanyIdSubOrder);
			} else {
				rightsService.updateConnection(new HashSet<>(readProperties), new HashSet<>(writeProperties), id, subCompanyIdSubOrder, parentCompanyId);
			}
			//this.updateEntityConnectionByPUT(id, subCompanyIdSubOrder, readProperties, writeProperties);
			RightsDtoCore response = new RightsDtoCore();
			response.setCompanyId(subCompanyIdSubOrder);
			response.setEntityId(id);
			response.setProperties(new RIghtsDtoCoreProperties(readProperties, writeProperties));
			return response;
		} catch (Exception ex) {
			RightsDtoCore response = new RightsDtoCore();
			response.setCompanyId(subCompanyIdSubOrder);
			response.setEntityId(id);
			return response;
		}
	}
	
}
