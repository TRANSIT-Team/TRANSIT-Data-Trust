package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.config.mail.SendMail;
import com.transit.backend.datalayers.controller.dto.OrderStatusCountDTO;
import com.transit.backend.datalayers.controller.dto.OrderStatusCountsDTO;
import com.transit.backend.datalayers.controller.dto.OrdersDaysDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.enums.OrderStatus;

import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.DashboardService;

import com.transit.backend.rightlayers.service.AccessService;

import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.transferentities.OrderDaily;
import com.transit.backend.transferentities.OrderStatusProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import java.util.stream.Stream;

import static com.transit.backend.datalayers.domain.enums.OrderStatus.*;
import static com.transit.backend.datalayers.domain.enums.OrderStatus.CREATED;

@Service
public class DashboardServiceBean implements DashboardService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private AccessService rightsService;
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	
	@Override
	@Transactional
	public OrderStatusCountsDTO getOrderStatusOverviewParentStatus(OrderStatus statusSearch) {
		OrderStatusCountsDTO result = new OrderStatusCountsDTO();
		OrderStatusCountDTO ownOrders = new OrderStatusCountDTO();
		List<UUID> ids = new ArrayList<>();
		rightsService.getAccessClazz(Order.class.getSimpleName(), false).getObjects().forEach(entry -> {
			ids.add(entry.getObjectId());
		});
		if (ids.isEmpty()) {
			result.setOwnOrders(ownOrders);
			return result;
		}
		Stream<OrderStatusProjection> orderStatusStream = orderRepository.findAllByParentOrderOrderStatusAndDeletedAndIdIn(statusSearch, false, ids);
		
		orderStatusStream.forEach(statusObject -> {
			var status = statusObject.getOrderStatus();
			if (!statusObject.isSuborderType()) {
				statusCount(ownOrders, status);
			} else {
				statusCount(ownOrders, status);
			}
		});
		result.setOwnOrders(ownOrders);
		return result;
	}
	
	@Override
	@Transactional
	public List<OrdersDaysDTO> getOrdersPerDay(long days) {
		
		List<OrdersDaysDTO> ordersDaysList = new ArrayList<>();
		List<UUID> ids = new ArrayList<>();
		
		rightsService.getAccessClazz(Order.class.getSimpleName(), false).getObjects().forEach(entry -> {
			ids.add(entry.getObjectId());
		});
		
		Stream<OrderDaily> stream = orderRepository.findAllByDeletedAndIdIn(false, ids);
		
		var now = OffsetDateTime.now();
		
		stream.forEach(entry -> {
			
			var pickupDate = entry.getPickUpDate();
			// Convert Date to LocalDate
			var pickupDateCleanTmp = pickupDate.toInstant()
					.atZone(ZoneId.systemDefault())
					.toLocalDate();
			
			// Convert LocalDate to Date
			Date pickupDateClean = Date.from(pickupDateCleanTmp.atStartOfDay(ZoneId.systemDefault()).toInstant());
			
			
			var orderStatus = entry.getOrderStatus();
			
			if (now.minusDays(days).isBefore(pickupDate)) {
				
				long ordersOpen = 0;
				long ordersProgress = 0;
				long ordersCanceled = 0;
				long ordersCreated = 0;
				long ordersComplete = 0;
				
				if (orderStatus.equals(OPEN.toString())) {
					ordersOpen = 1;
				}
				if (orderStatus.equals(PROCESSING.toString())) {
					ordersProgress = 1;
				}
				if (orderStatus.equals(CANCELED.toString())) {
					ordersCanceled = 1;
				}
				if (orderStatus.equals(CREATED.toString())) {
					ordersCreated = 1;
				}
				if (orderStatus.equals(COMPLETE.toString())) {
					ordersComplete = 1;
				}
				
				if (ordersDaysList.isEmpty()) {
					
					ordersDaysList.add(new OrdersDaysDTO(pickupDateClean, ordersOpen, ordersProgress, ordersCanceled, ordersCreated, ordersComplete));
				} else {
					if (!incrementOrderCountByDateAndStatus(ordersDaysList, pickupDateClean, ordersOpen, ordersProgress, ordersCanceled, ordersCreated, ordersComplete)) {
						ordersDaysList.add(new OrdersDaysDTO(pickupDateClean, ordersOpen, ordersProgress, ordersCanceled, ordersCreated, ordersComplete));
					}
				}
			}
		});
		
		
		return ordersDaysList;
	}
	
	private boolean incrementOrderCountByDateAndStatus(List<OrdersDaysDTO> ordersDaysList, Date targetDate, long ordersOpen, long ordersProgress, long ordersCanceled, long ordersCreated, long ordersComplete) {
		for (OrdersDaysDTO dto : ordersDaysList) {
			if (dto.getOrderDate().equals(targetDate)) {
				
				dto.setOrdersOpen(dto.getOrdersOpen() + ordersOpen);
				dto.setOrdersProgress(dto.getOrdersProgress() + ordersProgress);
				dto.setOrdersCanceled(dto.getOrdersCanceled() + ordersCanceled);
				dto.setOrdersCreated(dto.getOrdersCreated() + ordersCreated);
				dto.setOrdersComplete(dto.getOrdersComplete() + ordersComplete);
				return true; // Exit the method after incrementing to avoid unnecessary iterations
			}
		}
		
		// If no matching entry is found, optionally handle this case (e.g., log a message or throw an exception)
		return false;
	}
	
	@Override
	@Transactional
	public OrderStatusCountsDTO getOrderStatusOverview(boolean notDeleted, boolean filterCanceled) {
		OrderStatusCountsDTO result = new OrderStatusCountsDTO();
		OrderStatusCountDTO ownOrders = new OrderStatusCountDTO();
		OrderStatusCountDTO ownSuborders = new OrderStatusCountDTO();
		OrderStatusCountDTO acceptedSuborders = new OrderStatusCountDTO();
		List<UUID> ids = new ArrayList<>();
		rightsService.getAccessClazz(Order.class.getSimpleName(), false).getObjects().forEach(entry -> {
			ids.add(entry.getObjectId());
		});
		if (ids.isEmpty()) {
			result.setOwnOrders(ownOrders);
			result.setOwnSuborders(ownSuborders);
			result.setAcceptedSubOrdes(acceptedSuborders);
			return result;
		}
		Stream<OrderStatusProjection> orderStatusStream;
		if (notDeleted) {
			orderStatusStream = orderRepository.findAllByIdInAndDeleted(ids, false);
		} else {
			orderStatusStream = orderRepository.findAllByIdIn(ids);
		}
		orderStatusStream.forEach(statusObject -> {
			var status = statusObject.getOrderStatus();
			if (filterCanceled) {
				if (status.equals(CANCELED.toString()) && statusObject.getNewOrderId() != null) {
					return;
				}
			}
			if (!statusObject.isSuborderType()) {
				statusCount(ownOrders, status);
			} else {
				if (statusObject.getCompanyId().equals(userHelperFunctions.getCompanyId())) {
					statusCount(acceptedSuborders, status);
					
				} else {
					statusCount(ownSuborders, status);
				}
			}
		});
		result.setOwnOrders(ownOrders);
		result.setOwnSuborders(ownSuborders);
		result.setAcceptedSubOrdes(acceptedSuborders);
		
		return result;
	}
	
	
	private void statusCount(OrderStatusCountDTO statusCountList, String status) {
		if (status.equals(ACCEPTED.toString())) {
			statusCountList.setACCEPTED(statusCountList.getACCEPTED() + 1);
		} else if (status.equals(OPEN.toString())) {
			statusCountList.setOPEN(statusCountList.getOPEN() + 1);
		} else if (status.equals(PROCESSING.toString())) {
			statusCountList.setPROCESSING(statusCountList.getPROCESSING() + 1);
		} else if (status.equals(COMPLETE.toString())) {
			statusCountList.setCOMPLETE(statusCountList.getCOMPLETE() + 1);
		} else if (status.equals(CANCELED.toString())) {
			statusCountList.setCANCELED(statusCountList.getCANCELED() + 1);
		} else if (status.equals(REJECTED.toString())) {
			statusCountList.setREJECTED(statusCountList.getREJECTED() + 1);
		} else if (status.equals(REQUESTED.toString())) {
			statusCountList.setREQUESTED(statusCountList.getREQUESTED() + 1);
		} else if (status.equals(REVOKED.toString())) {
			statusCountList.setREVOKED(statusCountList.getREVOKED() + 1);
		} else if (status.equals(CREATED.toString())) {
			statusCountList.setCREATED(statusCountList.getCREATED() + 1);
		}
	}
	
	
}
