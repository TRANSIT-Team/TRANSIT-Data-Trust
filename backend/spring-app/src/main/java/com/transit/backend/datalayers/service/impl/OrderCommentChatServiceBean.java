package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.dto.OrderCommentChatDTO;
import com.transit.backend.datalayers.domain.OrderCommentChat;
import com.transit.backend.datalayers.domain.QOrderCommentChat;
import com.transit.backend.datalayers.repository.OrderCommentChatRepository;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.OrderCommentChatService;
import com.transit.backend.datalayers.service.mapper.OrderCommentChatMapper;
import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class OrderCommentChatServiceBean implements OrderCommentChatService {
	
	@Autowired
	private OrderCommentChatRepository orderCommentChatRepository;
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderCommentChatMapper orderCommentChatMapper;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public OrderCommentChat create(OrderCommentChat entity) {
		entity.setCompanyId(userHelperFunctions.getCompanyId());
		return orderCommentChatRepository.save(entity);
	}
	
	@Override
	public Collection<OrderCommentChat> read(UUID orderId) {
		var ordertemp = orderRepository.findById(orderId);
		if (ordertemp.isEmpty()) {
			return new HashSet<>();
		}
		var order = ordertemp.get();
		String query = "orderId==" + orderId + ";deleted==false";
		
		var spec = RSQLQueryDslSupport.toPredicate(query, QOrderCommentChat.orderCommentChat);
		var comments = new ArrayList<>(StreamSupport
				.stream(orderCommentChatRepository.findAll(spec).spliterator(), false)
				.toList());
		if (order.getSuborders() != null && !order.getSuborders().isEmpty()) {
			for (var sub : order.getSuborders()) {
				query = "orderId==" + sub.getId() + ";postParent==true;deleted==false";
				spec = RSQLQueryDslSupport.toPredicate(query, QOrderCommentChat.orderCommentChat);
				comments.addAll(StreamSupport
						.stream(orderCommentChatRepository.findAll(spec).spliterator(), false)
						.toList());
			}
		}
		if (order.getParentOrder() != null) {
			query = "orderId==" + order.getParentOrder().getId() + ";postChild==true;deleted==false";
			spec = RSQLQueryDslSupport.toPredicate(query, QOrderCommentChat.orderCommentChat);
			comments.addAll(StreamSupport
					.stream(orderCommentChatRepository.findAll(spec).spliterator(), false)
					.toList());
		}
		Collections.sort(comments);
		
		return comments;
	}
	
	@Override
	public OrderCommentChat update(UUID orderId, UUID id, OrderCommentChat entity) {
		var oldEntry = orderCommentChatRepository.findById(id);
		if (oldEntry.isEmpty()) {
			throw new BadRequestException("Order Comment id not found");
		}
		if (!oldEntry.get().getCompanyId().equals(userHelperFunctions.getCompanyId())) {
			throw new BadRequestException("Order Comment is not from your company.");
		}
		oldEntry.get().setComment(entity.getComment());
		oldEntry.get().setPostChild(entity.isPostChild());
		oldEntry.get().setPostParent(entity.isPostParent());
		
		
		return orderCommentChatRepository.saveAndFlush(oldEntry.get());
	}
	
	@Override
	public OrderCommentChat patch(UUID orderId, UUID id, JsonNode patch) throws JsonPatchException, JsonProcessingException {
		var oldEntry = orderCommentChatRepository.findById(id);
		if (oldEntry.isEmpty()) {
			throw new BadRequestException("Order Comment id not found");
		}
		if (!oldEntry.get().getCompanyId().equals(userHelperFunctions.getCompanyId())) {
			throw new BadRequestException("Order Comment is not from your company.");
		}
		
		var dto = orderCommentChatMapper.toDto(oldEntry.get());
		JsonNode original = objectMapper.valueToTree(dto);
		JsonNode patched = JsonMergePatch.fromJson(patch).apply(original);
		var response = objectMapper.treeToValue(patched, OrderCommentChatDTO.class);
		var entity = orderCommentChatMapper.toEntity(response);
		
		oldEntry.get().setComment(entity.getComment());
		oldEntry.get().setPostChild(entity.isPostChild());
		oldEntry.get().setPostParent(entity.isPostParent());
		return orderCommentChatRepository.saveAndFlush(oldEntry.get());
	}
	
	@Override
	public void delete(UUID orderId, UUID id) {
		var oldEntry = orderCommentChatRepository.findById(id);
		if (oldEntry.isEmpty()) {
			throw new BadRequestException("Order Comment id not found");
		}
		if (!oldEntry.get().getCompanyId().equals(userHelperFunctions.getCompanyId())) {
			throw new BadRequestException("Order Comment is not from your company.");
		}
		oldEntry.get().setDeleted(true);
		orderCommentChatRepository.saveAndFlush(oldEntry.get());
	}
	
	
}
