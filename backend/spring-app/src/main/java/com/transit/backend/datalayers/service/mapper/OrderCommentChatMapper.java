package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.OrderCommentChatDTO;
import com.transit.backend.datalayers.domain.OrderCommentChat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderCommentChatMapper extends AbstractMapper<OrderCommentChat, OrderCommentChatDTO> {
}
