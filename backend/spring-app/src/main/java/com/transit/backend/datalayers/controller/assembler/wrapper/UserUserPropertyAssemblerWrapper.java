package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.UserController;
import com.transit.backend.datalayers.controller.UserUserPropertyController;
import com.transit.backend.datalayers.controller.assembler.UserPropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.transferentities.UserTransferObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserUserPropertyAssemblerWrapper extends AssemblerWrapperSubAbstract<UserProperty, UserPropertyDTO, UserTransferObject, UserDTO, UserController, UserUserPropertyController> {
	@Autowired
	UserPropertyAssembler userPropertyAssembler;
	
	public UserPropertyDTO toModel(UserProperty entity, UUID userId, boolean backwardLink) {
		return super.toModel(entity, userId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<UserProperty, UserPropertyDTO> getAssemblerSupport() {
		return this.userPropertyAssembler;
	}
	
	public UserPropertyDTO addLinks(UserPropertyDTO dto, UUID userId, boolean backwardLink) {
		return super.addLinks(dto, userId, backwardLink);
	}
	
	@Override
	public Class<UserUserPropertyController> getNestedControllerClazz() {
		return UserUserPropertyController.class;
	}
	
	@Override
	public Class<UserController> getRootControllerClazz() {
		return UserController.class;
	}
	
	@Override
	public Class<UserTransferObject> getDomainNameClazz() {
		return UserTransferObject.class;
	}
	
	public CollectionModel<UserPropertyDTO> toCollectionModel(Iterable<? extends UserProperty> entities, UUID userId, boolean backwardLink) {
		return super.toCollectionModel(entities, userId, backwardLink);
	}
}