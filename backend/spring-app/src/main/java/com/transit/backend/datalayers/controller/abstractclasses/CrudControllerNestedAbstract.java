package com.transit.backend.datalayers.controller.abstractclasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerNested;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTOIdCanBeNull;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.PingService;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import com.transit.backend.transferentities.UserTransferObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.UUID;

@Component
public abstract class CrudControllerNestedAbstract<test, Key, testDTO extends AbstractDTOIdCanBeNull<testDTO>, testRoot, testRootDTO, rootController extends CrudControllerExtend<testRootDTO, UUID>, nestedController extends CrudControllerNested<testDTO, UUID>> {
	
	@Autowired
	private AccessService rightsService;
	@Autowired
	private PingService pingService;
	
	public ResponseEntity<testDTO> update(Key primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) testDTO dto) throws ClassNotFoundException {
		pingService.available();
		test entity = getMapper().toEntity(dto);
		
		
		Optional<AccessResponseDTO> rightsEntry = this.rightsService.getAccess((UUID) primaryKey);
		
		entity = getService().update(primaryKey, entity);
		
		return getTestDTOResponseEntity(entity);
	}
	
	public abstract AbstractMapper<test, testDTO> getMapper();
	
	public abstract CrudServiceNested<test, Key> getService();
	
	@NotNull
	private ResponseEntity<testDTO> getTestDTOResponseEntity(test entity) {
		pingService.available();
		testDTO responseEntity = null;
		if (getFilter()) {
			entity = getFilterHelper().filterEntities(entity, getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(entity)));
		}
		if (entity instanceof AbstractPropertyEntity abstractProperty) {
			responseEntity = getAssemblerWrapper().toModel(entity, abstractProperty.getParentId(), true);
		} else if (entity instanceof UserTransferObject abstractProperty) {
			responseEntity = getAssemblerWrapper().toModel(entity, abstractProperty.getUser().getId(), true);
		} else {
			throw new BadRequestException("Element has no Parent.");
		}
		
		return new ResponseEntity<>(responseEntity, HttpStatus.OK);
	}
	
	public abstract boolean getFilter();
	
	public abstract EntityFilterHelper<test, ?> getFilterHelper();
	
	public abstract AssemblerWrapperNestedAbstract<test, testDTO, testRoot, testRootDTO, rootController, nestedController> getAssemblerWrapper();
	
	public ResponseEntity<testDTO> partialUpdate(Key primaryKey, JsonNode node) throws JsonPatchException, JsonProcessingException {
		pingService.available();
		var patch = JsonMergePatch.fromJson(node);
		test entity = getService().partialUpdate(primaryKey, patch);
		return getTestDTOResponseEntity(entity);
	}
	
	
	public ResponseEntity<testDTO> readOne(Key primaryKey) {
		pingService.available();
		Optional<test> test = getService().readOne(primaryKey);
		if (getFilter() && test.isPresent()) {
			test = Optional.of(getFilterHelper().filterEntities(test.get(), getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(test.get()))));
		}
		return test.map(this::getTestDTOResponseEntity).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	
	public ResponseEntity delete(Key primaryKey) {
		pingService.available();
		getService().delete(primaryKey);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}