package com.transit.backend.rightlayers.service.helper;

import com.transit.backend.config.mail.SendMail;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.OrderService;
import com.transit.backend.exeptions.exeption.ForbiddenException;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.rightlayers.controller.dto.RightsDtoCore;
import com.transit.backend.rightlayers.controller.dto.RightsDtoCoreList;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.RightsManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.transit.backend.rightlayers.service.helper.DefaultListHelper.getEmptyList;
import static com.transit.backend.rightlayers.service.helper.DefaultListHelper.getListIdAndOrderStatus;

@Component
public class UpdateRightsOrderAssigment {
	
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private AccessService rightsService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private RemoveRightsCompany removeRightsCompany;
	
	@Autowired
	private RightsManageService rightsManageService;
	
	@Autowired
	private SendMail sendMail;
	
	public RightsDtoCoreList updateRightsForOrderAssignment(UUID parentOrderId, UUID subOrderId, RightsDtoCoreList targetDtos) throws ClassNotFoundException, MessagingException, InterruptedException {
		List<RightsDtoCore> responseTemp = new ArrayList<>();
		List<RightsDtoCore> response = new ArrayList<>();
		List<UUID> revokedSuborders = new ArrayList<>();
		var parentOrder = orderRepository.findById(parentOrderId);
		if (parentOrder.isEmpty()) {
			throw new UnprocessableEntityExeption("Cannot found ParentOrder in DB");
		}
		var subOrderTest = orderRepository.findById(subOrderId);
		if (subOrderTest.isEmpty()) {
			throw new UnprocessableEntityExeption("Cannot found SubOrder in DB");
		}
		final UUID[] subCompanyId = {new UUID(0, 0)};
		
		targetDtos.getEntries().forEach(entry -> {
			if (entry.getCompanyId() == null) {
				throw new UnprocessableEntityExeption("One CompanyId is null");
			}
			if (entry.getEntityId() == null) {
				throw new UnprocessableEntityExeption("One EntityId is null");
			}
			if (subCompanyId[0].equals(new UUID(0, 0))) {
				subCompanyId[0] = entry.getCompanyId();
			} else if (!subCompanyId[0].equals(entry.getCompanyId())) {
				throw new UnprocessableEntityExeption("Different CompanyIds " + subCompanyId + " ," + entry.getCompanyId());
			}
		});
		if (rightsService.getAccess(parentOrderId).isEmpty() || rightsService.getAccess(subOrderId, targetDtos.getEntries().get(0).getCompanyId()).isEmpty()) {
			throw new ForbiddenException("No Access on this entities or their exists no rights for suborder.");
		}
		
		var emptyProperties = getEmptyList();
		var onlyIdAndOrderStatusProperties = getListIdAndOrderStatus();
		
		
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		//Remove Rights for all other suborders
		for (Order suborder : parentOrder.get().getSuborders()) {
			var subCompanyIdSubOrder = suborder.getCompany().getId();
			if (!subOrderId.equals(suborder.getId()) &&
					!suborder.getOrderStatus().equals(OrderStatus.REVOKED) &&
					!suborder.getOrderStatus().equals(OrderStatus.REJECTED)) {
				CompletableFuture<Void> updateRightsCompany = CompletableFuture.supplyAsync(() -> {
							try {
								removeRightsCompany.removeRightsOfCompany(suborder, responseTemp, subCompanyIdSubOrder, onlyIdAndOrderStatusProperties, emptyProperties, OrderStatus.REVOKED, false, parentOrder.get().getCompany().getId());
							} catch (MessagingException | ClassNotFoundException | InterruptedException e) {
								throw new RuntimeException(e);
							}
							return null;
						}
				);
				futures.add(updateRightsCompany);
				revokedSuborders.add(suborder.getId());
			}
		}
		
		CompletableFuture<Void> allCompleted = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
		final int[] count = {0};
		try {
			allCompleted.get();
			futures.forEach(entry -> {
				try {
					entry.get();
					sendMail.sendMailForRevoked(revokedSuborders.get(count[0]));
					count[0] = count[0] + 1;
				} catch (InterruptedException | ExecutionException | MessagingException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		List<CompletableFuture<RightsDtoCore>> futuresSetRights = new ArrayList<>();
		
		//Add Rights for Target Entity
		for (var dto : targetDtos.getEntries()) {
			CompletableFuture<RightsDtoCore> updatedOrderRights = CompletableFuture.supplyAsync(() -> {
				var readProperties = dto.getProperties().getReadProperties();
				var writeProperties = dto.getProperties().getWriteProperties();
				try {
					rightsManageService.updateEntityConnectionByPUT(dto.getEntityId(), dto.getCompanyId(), subOrderId, readProperties, writeProperties, parentOrder.get().getCompany().getId());
				} catch (ClassNotFoundException | MessagingException | InterruptedException e) {
					throw new RuntimeException(e);
				}
				return dto;
			});
			futuresSetRights.add(updatedOrderRights);
			responseTemp.add(dto);
		}
		
		CompletableFuture<Void> allCompletedRights = CompletableFuture.allOf(futuresSetRights.toArray(CompletableFuture[]::new));
		
		try {
			allCompletedRights.get();
			futuresSetRights.forEach(entry -> {
				try {
					entry.get();
					
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		
		
		//Update Order Status of Accepted Order
		orderService.updateOrderStatus(subOrderId, OrderStatus.OPEN.toString());
		
		
		for (var entry : responseTemp) {
			if (!entry.getEntityId().equals(new UUID(0, 0))) {
				response.add(entry);
			}
		}
		RightsDtoCoreList list = new RightsDtoCoreList();
		list.setEntries(response);
		
		return list;
		
	}
}
