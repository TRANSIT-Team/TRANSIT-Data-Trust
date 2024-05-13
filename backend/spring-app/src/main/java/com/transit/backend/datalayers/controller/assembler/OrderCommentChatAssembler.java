package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.OrderCommentChatController;
import com.transit.backend.datalayers.controller.OrderController;
import com.transit.backend.datalayers.controller.dto.OrderCommentChatDTO;
import com.transit.backend.datalayers.controller.dto.enums.ChatIsMy;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderCommentChat;
import com.transit.backend.datalayers.service.mapper.OrderCommentChatMapper;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderCommentChatAssembler extends RepresentationModelAssemblerSupport<OrderCommentChat, OrderCommentChatDTO> {
	
	@Autowired
	private OrderCommentChatMapper mapper;
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	public OrderCommentChatAssembler() {
		super(OrderCommentChatController.class, OrderCommentChatDTO.class);
	}
	
	@Override
	public OrderCommentChatDTO toModel(OrderCommentChat entity) {
		var result = mapper
				.toDto(entity)
				.add(linkTo(methodOn(OrderCommentChatController.class).read(entity.getOrderId())).withRel("orderCommentChat"))
				.add(linkTo(methodOn(OrderController.class).readOne(entity.getOrderId())).withRel(Order.class.getSimpleName().toLowerCase()));
		if (userHelperFunctions.getCompanyId().equals(entity.getCompanyId())) {
			result.setPerson(ChatIsMy.ME);
		} else {
			result.setPerson(ChatIsMy.YOU);
		}
		return result;
	}
	
	@Override
	public CollectionModel<OrderCommentChatDTO> toCollectionModel(Iterable<? extends OrderCommentChat> entities) {
		return super.toCollectionModel(entities);
	}
}
