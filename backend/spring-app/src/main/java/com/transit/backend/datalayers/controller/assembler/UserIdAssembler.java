package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.UserController;
import com.transit.backend.datalayers.controller.dto.UserIdDTO;
import com.transit.backend.datalayers.service.mapper.UserIdMapper;
import com.transit.backend.transferentities.UserIdTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserIdAssembler extends RepresentationModelAssemblerSupport<UserIdTransfer, UserIdDTO> {
	
	@Autowired
	private UserIdMapper mapper;
	
	public UserIdAssembler() {
		super(UserController.class, UserIdDTO.class);
	}
	
	@Override
	public UserIdDTO toModel(UserIdTransfer entity) {
		return this.mapper
				.toDto(entity)
				.add(linkTo(methodOn(UserController.class).readOne(entity.getUserId())).withSelfRel());
	}
}

