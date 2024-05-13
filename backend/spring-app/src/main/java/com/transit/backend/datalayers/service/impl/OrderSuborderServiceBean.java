package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderProperty;
import com.transit.backend.datalayers.domain.QOrder;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.OrderOrderPropertyService;
import com.transit.backend.datalayers.service.OrderService;
import com.transit.backend.datalayers.service.OrderSuborderService;
import com.transit.backend.datalayers.service.mapper.OrderMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.LongestConditionName;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.transferentities.FilterExtra;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Optional;
import java.util.SortedSet;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class OrderSuborderServiceBean implements OrderSuborderService {
	
	@Inject
	Validator validator;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderOrderPropertyService orderOrderPropertyService;
	@Autowired
	private OrderMapper mapper;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private OrderService orderService;
	
	@Override
	public Order create(UUID uuid, Order entity) {
		var parentOrder = orderRepository.findById(uuid);
		if (parentOrder.isEmpty()) {
			throw new NoSuchElementFoundException(Order.class.getSimpleName(), uuid);
		}
		entity.setParentOrder(parentOrder.get());
		return orderService.createInternal(entity, false, uuid);
	}
	
	@Override
	public Order update(UUID uuid, UUID uuid2, Order entity) {
		var parentOrder = orderRepository.findById(uuid);
		var oldSubOrder = orderRepository.findById(uuid2);
		if (parentOrder.isEmpty()) {
			throw new NoSuchElementFoundException(Order.class.getSimpleName(), uuid);
		} else if (oldSubOrder.isEmpty()) {
			throw new NoSuchElementFoundException(Order.class.getSimpleName(), uuid2);
		} else checkParentOrderId(oldSubOrder, entity);
		
		entity.setParentOrder(parentOrder.get());
		return orderService.update(uuid2, entity);
	}
	
	@Override
	public Order partialUpdate(UUID uuid, UUID uuid2, JsonMergePatch patch) {
		var oldSubOrder = orderRepository.findById(uuid2);
		if (oldSubOrder.isEmpty()) {
			throw new NoSuchElementFoundException(Order.class.getSimpleName(), uuid2);
		}
		var dto = mapper.toDto(oldSubOrder.get());
		try {
			JsonNode original = objectMapper.valueToTree(dto);
			JsonNode patched = patch.apply(original);
			var patchedDTO = objectMapper.treeToValue(patched, OrderDTO.class);
			var entity = mapper.toEntity(patchedDTO);
			
			checkParentOrderId(oldSubOrder, entity);
			
			
			return orderService.partialUpdate(uuid2, patch);
		} catch (Exception e) {
			throw new UnprocessableEntityExeption(e.getMessage());
		}
	}
	
	@Override
	public Collection<Order> read(UUID uuid, String query, FilterExtra FilterExtra) {
		String queryNew = "";
		int finalLongestConditionName = LongestConditionName.longestConditonInQuery(query);
		if (query.trim().isBlank()) {
			queryNew += "parentOrder.id==" + uuid;
		} else {
			queryNew = "( " + query + " ) and parentOrder.id==" + uuid;
		}
		var spec = RSQLQueryDslSupport.toPredicate(queryNew, QOrder.order);
		orderRepository.findById(uuid).orElseThrow(() -> new NoSuchElementFoundException(Order.class.getSimpleName(), uuid));
		return StreamSupport
				.stream(orderRepository.findAll(spec).spliterator(), false)
				.map(suborder -> {
					if (suborder.getProperties() == null || suborder.getProperties().isEmpty()) {
						return suborder;
					}
					String replaceQuery = QueryRewrite.queryRewriteOrderToOrderProperties(QueryRewrite.queryDefaultMatcher(query, finalLongestConditionName));
					suborder.setProperties((SortedSet<OrderProperty>) orderOrderPropertyService.read(suborder.getId(), replaceQuery, FilterExtra));
					return orderService.filterExtend(suborder);
				})
				.toList();
	}
	
	@Override
	public Optional<Order> readOne(UUID uuid, UUID uuid2) {
		var entity = orderService.readOne(uuid2);
		
		if (entity.isPresent() && !(entity.get().getParentOrder().getId().equals(uuid))) {
			throw new UnprocessableEntityExeption("Order with Id: " + uuid2 + " is not Suborder of Order with id: " + uuid);
		}
		
		return entity;
	}
	
	@Override
	public void delete(UUID uuid, UUID uuid2) {
		orderService.delete(uuid2);
	}
	
	private void checkParentOrderId(Optional<Order> oldSubOrder, Order entity) {
		if (oldSubOrder.get().getParentOrder() != null && entity.getParentOrder() != null &&
				!oldSubOrder.get().getParentOrder().getId().equals(entity.getParentOrder().getId())) {
			throw new UnprocessableEntityExeption("Not Same ParentId");
		} else if (oldSubOrder.get().getParentOrder() != null && entity.getParentOrder() == null) {
			throw new UnprocessableEntityExeption("Cannot update ParentId");
		} else if (oldSubOrder.get().getParentOrder() == null && entity.getParentOrder() != null) {
			throw new UnprocessableEntityExeption("ParentId was null and should be null");
		}
	}
}
