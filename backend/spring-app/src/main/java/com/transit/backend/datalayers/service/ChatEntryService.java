package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.ChatEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.UUID;

public interface ChatEntryService {
	
	ChatEntry create(ChatEntry entity);
	
	Collection<ChatEntry> read(UUID orderId, String query);
	
	boolean readUpdate(UUID orderId);
	
	
	Page<ChatEntry> readFilter(Pageable pageable, String query);
	
	
}
