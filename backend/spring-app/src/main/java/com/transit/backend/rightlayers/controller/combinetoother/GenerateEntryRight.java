//package com.transit.backend.controller.rights.combinetoother;
//
//import com.transit.backend.controller.rights.dto.extern.IdentifierCompanyDTO;
//import com.transit.backend.controller.rights.dto.itern.RightsDTOInternal;
//import com.transit.backend.service.rights.RightsEntityService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//@Component
//public class GenerateEntryRight {
//
//	@Autowired
//	private RightsEntityService rightsEntityService;
//
//
//
//
//	public void generateEntry(UUID entityId, String typeClazz) {
//
//		var userId= this.rightsEntityService.getUserID();
//		var dto = new RightsDTOInternal();
//		dto.setEntityId(entityId);
//
//		dto.setCreatorCompany(new IdentifierCompanyDTO(this.rightsEntityService.getUser(userId).getCompany().getId()));
//		dto.setTypeClazz(typeClazz);
//		rightsEntityService.create(dto);
//
//	}
//	@Transactional
//	public void generateEntry(UUID entityId,String typeClazz,UUID userIdLocalInitialize) {
//		var userId= userIdLocalInitialize;
//		var dto = new RightsDTOInternal();
//		dto.setEntityId(entityId);
//
//		dto.setCreatorCompany(new IdentifierCompanyDTO(this.rightsEntityService.getUser(userId).getCompany().getId()));
//		dto.setTypeClazz(typeClazz);
//		rightsEntityService.create(dto);
//
//	}
//
//
//}
