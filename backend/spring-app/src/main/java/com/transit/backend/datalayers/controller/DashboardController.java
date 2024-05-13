package com.transit.backend.datalayers.controller;

import com.transit.backend.datalayers.controller.assembler.*;
import com.transit.backend.datalayers.controller.dto.OrderStatusCountsDTO;
import com.transit.backend.datalayers.controller.dto.OrdersDaysDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.DashboardService;
import com.transit.backend.datalayers.service.OrderService;
import com.transit.backend.datalayers.service.mapper.OrderMapper;
import com.transit.backend.rightlayers.controller.RightsController;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.PingService;
import com.transit.backend.rightlayers.service.RightsManageService;
import com.transit.backend.security.filterresponse.implementations.OrderFilter;
import com.transit.backend.security.preauthaccess.GetFilterExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Slf4j
public class DashboardController {
	
	@Autowired
	private OrderStatusCountAssembler orderStatusCountAssembler;
	
	@Autowired
	private OrderDaysAssembler orderDaysAssembler;
	
	@Autowired
	private PingService pingService;
	
	@Autowired
	private DashboardService service;
	
	
	@GetMapping(path = "/overview/daily")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CollectionModel<OrdersDaysDTO>> getDailyOrders(@RequestParam(name = "days", defaultValue = "7") int days) {
		pingService.available();
		
		
		var result = service.getOrdersPerDay(days);
		return new ResponseEntity<>(orderDaysAssembler.toCollectionModel(result), HttpStatus.OK);
	}
	
	
	@GetMapping(path = "/overview")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<OrderStatusCountsDTO> readOrderStatus(@RequestParam(name = "notDeleted", defaultValue = "true") boolean notDeleted, @RequestParam(name = "filterCanceled", defaultValue = "true") boolean filterCanceled) {
		pingService.available();
		return new ResponseEntity<>(orderStatusCountAssembler.toModel(service.getOrderStatusOverview(notDeleted, filterCanceled)), HttpStatus.OK);
	}
	
	@GetMapping(path = "/overview/suborders")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<OrderStatusCountsDTO> readOrderStatus(@RequestParam(name = "orderStatus", defaultValue = "OPEN") OrderStatus status) {
		pingService.available();
		return new ResponseEntity<>(orderStatusCountAssembler.toModel(service.getOrderStatusOverviewParentStatus(status)), HttpStatus.OK);
	}
}
