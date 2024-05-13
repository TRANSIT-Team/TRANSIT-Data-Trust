package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.ChatEntry;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.SortedSet;
import java.util.UUID;

@Repository
public interface ChatEntryRepository extends AbstractRepository<ChatEntry> {
	
	SortedSet<ChatEntry> findChatEntriesByOrderIdOrderBySequenceId(UUID orderId);
	
	
}
