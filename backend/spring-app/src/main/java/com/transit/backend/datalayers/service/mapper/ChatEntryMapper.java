package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.ChatEntryDTO;
import com.transit.backend.datalayers.domain.ChatEntry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatEntryMapper extends AbstractMapper<ChatEntry, ChatEntryDTO> {
}
