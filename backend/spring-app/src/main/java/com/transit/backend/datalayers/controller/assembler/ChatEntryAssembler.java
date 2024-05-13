package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.ChatEntryController;
import com.transit.backend.datalayers.controller.OrderController;
import com.transit.backend.datalayers.controller.dto.ChatEntryDTO;
import com.transit.backend.datalayers.controller.dto.enums.ChatIsMy;
import com.transit.backend.datalayers.domain.ChatEntry;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.service.mapper.ChatEntryMapper;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ChatEntryAssembler extends RepresentationModelAssemblerSupport<ChatEntry, ChatEntryDTO> {
	
	@Autowired
	private ChatEntryMapper mapper;
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	public ChatEntryAssembler() {
		super(ChatEntryController.class, ChatEntryDTO.class);
	}
	
	@Override
	public ChatEntryDTO toModel(ChatEntry entity) {
		var result = mapper
				.toDto(entity)
				.add(linkTo(methodOn(ChatEntryController.class).read("sequenceId==" + entity.getSequenceId(), entity.getOrderId())).withSelfRel())
				.add(linkTo(methodOn(OrderController.class).readOne(entity.getOrderId())).withRel(Order.class.getSimpleName().toLowerCase()));
		if (userHelperFunctions.getCompanyId().equals(entity.getCompanyId())) {
			result.setPerson(ChatIsMy.ME);
		} else {
			result.setPerson(ChatIsMy.YOU);
		}
		return result;
	}
	
	@Override
	public CollectionModel<ChatEntryDTO> toCollectionModel(Iterable<? extends ChatEntry> entities) {
		return super.toCollectionModel(entities);
	}
}
