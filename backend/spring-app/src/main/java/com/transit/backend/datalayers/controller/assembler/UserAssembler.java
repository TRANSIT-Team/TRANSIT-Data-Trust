package com.transit.backend.datalayers.controller.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.controller.UserController;
import com.transit.backend.datalayers.controller.UserUserPropertyController;
import com.transit.backend.datalayers.controller.assembler.wrapper.UserUserPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.service.mapper.UserTransferMapper;
import com.transit.backend.transferentities.UserTransferObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static com.transit.backend.config.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserAssembler extends RepresentationModelAssemblerSupport<UserTransferObject, UserDTO> {
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private UserTransferMapper userTranferMapper;
	
	@Autowired
	private UserUserPropertyAssemblerWrapper userUserPropertyAssemblerWrapper;
	
	
	public UserAssembler() {
		super(UserController.class, UserDTO.class);
	}
	
	
	@Override
	public UserDTO toModel(UserTransferObject entity) {
		var dto = this.userTranferMapper
				.toDto(entity)
				.add(linkTo(methodOn(UserController.class).readOne(entity.getUser().getId())).withSelfRel());
		dto.add(linkTo(methodOn(UserUserPropertyController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("userProperties"));
		dto.setProperties(toPropertyDTO(entity.getUser().getId(), entity.getUser().getProperties()));
		return dto;
	}
	
	
	private SortedSet<UserPropertyDTO> toPropertyDTO(UUID rootId, SortedSet<UserProperty> properties) {
		if (properties == null || properties.isEmpty()) {
			return new TreeSet<>();
		} else {
			return new TreeSet<>(userUserPropertyAssemblerWrapper.toCollectionModel(properties, rootId, false).getContent());
		}
	}
	
	
}
