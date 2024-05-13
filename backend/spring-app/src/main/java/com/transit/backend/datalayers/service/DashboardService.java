package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.controller.dto.OrderStatusCountsDTO;
import com.transit.backend.datalayers.controller.dto.OrdersDaysDTO;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DashboardService {
	@Transactional
	OrderStatusCountsDTO getOrderStatusOverviewParentStatus(OrderStatus statusSearch);
	
	@Transactional
	List<OrdersDaysDTO> getOrdersPerDay(long days);
	
	@Transactional
	OrderStatusCountsDTO getOrderStatusOverview(boolean notDeleted, boolean filterCanceled);
}
