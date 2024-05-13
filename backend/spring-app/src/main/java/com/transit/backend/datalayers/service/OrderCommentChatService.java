package com.transit.backend.datalayers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.domain.OrderCommentChat;

import java.util.Collection;
import java.util.UUID;

public interface OrderCommentChatService {
	
	public OrderCommentChat create(OrderCommentChat entity);
	
	
	Collection<OrderCommentChat> read(UUID orderId);
	
	
	OrderCommentChat update(UUID orderId, UUID id, OrderCommentChat entity);
	
	OrderCommentChat patch(UUID orderId, UUID id, JsonNode entity) throws JsonPatchException, JsonProcessingException;
	
	void delete(UUID orderId, UUID id);
}
