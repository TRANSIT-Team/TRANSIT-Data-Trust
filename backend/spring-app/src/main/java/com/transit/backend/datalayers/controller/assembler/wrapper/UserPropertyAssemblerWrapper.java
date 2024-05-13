package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.UserController;
import com.transit.backend.datalayers.controller.UserPropertyController;
import com.transit.backend.datalayers.controller.assembler.UserPropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.UserProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserPropertyAssemblerWrapper extends AssemblerWrapperNestedAbstract<UserProperty, UserPropertyDTO, User, UserDTO, UserController, UserPropertyController> {
	
	@Autowired
	UserPropertyAssembler userPropertyAssembler;
	
	
	@Override
	public RepresentationModelAssemblerSupport<UserProperty, UserPropertyDTO> getAssemblerSupport() {
		return this.userPropertyAssembler;
	}
	
	@Override
	public Class<UserPropertyController> getNestedControllerClazz() {
		return UserPropertyController.class;
	}
	
	@Override
	public Class<UserController> getRootControllerClazz() {
		return UserController.class;
	}
	
	@Override
	public Class<User> getDomainNameClazz() {
		return User.class;
	}
}
