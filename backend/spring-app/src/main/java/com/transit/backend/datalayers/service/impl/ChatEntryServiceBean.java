package com.transit.backend.datalayers.service.impl;

import com.transit.backend.datalayers.domain.ChatEntry;
import com.transit.backend.datalayers.domain.QChatEntry;
import com.transit.backend.datalayers.domain.QOrder;
import com.transit.backend.datalayers.repository.ChatEntryRepository;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.ChatEntryService;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ChatEntryServiceBean implements ChatEntryService {
	
	@Autowired
	private ChatEntryRepository chatEntryRepository;
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	public ChatEntry create(ChatEntry entity) {
		final long[] maxvalue = {0};
		chatEntryRepository.findChatEntriesByOrderIdOrderBySequenceId(entity.getOrderId()).forEach(entry -> {
			if (entry.getSequenceId() > maxvalue[0]) {
				maxvalue[0] = entry.getSequenceId();
			}
		});
		entity.setSequenceId(maxvalue[0] + 1);
		entity.setCompanyId(userHelperFunctions.getCompanyId());
		entity.setReadStatus(false);
		
		// new message Counter
		var order = orderRepository.findById(entity.getOrderId());
		if (order.isPresent()) {
			order.get().setMessageCounter(entity.getSequenceId());
		}
		orderRepository.saveAndFlush(order.get());
		return chatEntryRepository.save(entity);
	}
	
	@Override
	public Collection<ChatEntry> read(UUID orderId, String query) {
		if (query != null && !query.isBlank()) {
			query = "orderId==" + orderId + ";(" + query + ")";
		} else {
			query = "orderId==" + orderId;
		}
		
		var spec = RSQLQueryDslSupport.toPredicate(query, QChatEntry.chatEntry);
		return StreamSupport
				.stream(chatEntryRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "sequenceId")).spliterator(), false)
				.collect(Collectors.toCollection(TreeSet::new));
	}
	
	@Override
	public boolean readUpdate(UUID orderId) {
		var companyId = userHelperFunctions.getCompanyId();
		
		String query = "orderId==" + orderId + " and companyId=out=(" + companyId + ")";
		
		
		var spec = RSQLQueryDslSupport.toPredicate(query, QChatEntry.chatEntry);
		var result = StreamSupport.stream(chatEntryRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "sequenceId")).spliterator(), false).collect(Collectors.toCollection(TreeSet::new));
		
		result.stream().forEach(cE -> {
			cE.setReadStatus(true);
			chatEntryRepository.saveAndFlush(cE);
		});
		return true;
	}
	
	@Override
	public Page<ChatEntry> readFilter(Pageable pageable, String query) {
		if (query != null && !query.isBlank()) {
			query = "( " + query + ") and suborderType==true;messageCounter>0";
		} else {
			return Page.empty();
		}
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "modifyDate"));
		var spec = RSQLQueryDslSupport.toPredicate(query, QOrder.order);
		var result = StreamSupport.stream(orderRepository.findAll(spec, pageable).spliterator(), false).map(order -> {
			String queryNew = "orderId==" + order.getId();
			var specNew = RSQLQueryDslSupport.toPredicate(queryNew, QChatEntry.chatEntry);
			return StreamSupport.stream(chatEntryRepository.findAll(specNew, Sort.by(Sort.Direction.DESC, "sequenceId")).spliterator(), false).findFirst();
		}).map(entry -> entry.get()).collect(Collectors.toList());
		return new PageImpl<>(result, pageable, orderRepository.count(spec));
	}
	
	
}
