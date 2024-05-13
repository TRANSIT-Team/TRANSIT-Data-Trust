package com.transit.backend.rightlayers.service.helper;

import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.OrderService;
import com.transit.backend.rightlayers.controller.dto.RightsDtoCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RemoveRightsCompany {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CanChangeAddressRights canChangeAddressRights;
	
	@Autowired
	private MethodXAddress methodXAddress;
	
	@Autowired
	private UpdateRightsIgnoreException updateRightsIgnoreException;
	@Autowired
	private OrderRepository orderRepository;
	
	public void removeRightsOfCompany(Order suborder, List<RightsDtoCore> responseTemp, UUID subCompanyIdSubOrder, List<String> onlyIdAndOrderStatusProperties, List<String> emptyProperties, OrderStatus orderStatus, UUID parentCompanyId) throws MessagingException, ClassNotFoundException, InterruptedException {
		removeRightsOfCompany(suborder, responseTemp, subCompanyIdSubOrder, onlyIdAndOrderStatusProperties, emptyProperties, orderStatus, true, parentCompanyId);
	}
	
	public void removeRightsOfCompany(Order suborder, List<RightsDtoCore> responseTemp, UUID subCompanyIdSubOrder, List<String> onlyIdAndOrderStatusProperties, List<String> emptyProperties, OrderStatus orderStatus, boolean outsideCLass, UUID parentOrderCompanyId) throws MessagingException, ClassNotFoundException, InterruptedException {
		List<CompletableFuture<RightsDtoCore>> futures = new ArrayList<>();
		
		
		CompletableFuture<RightsDtoCore> updatedOrderRights = CompletableFuture.supplyAsync(() -> {
			return updateRightsIgnoreException.updateRightsIgnoreException(suborder.getId(), subCompanyIdSubOrder, onlyIdAndOrderStatusProperties, emptyProperties, parentOrderCompanyId);
		});
		futures.add(updatedOrderRights);
		if (!outsideCLass) {
			suborder.setOrderStatus(orderStatus);
			orderService.saveOrder(suborder);
			//orderService.updateOrderStatus(suborder.getId(), orderStatus.toString(), parentOrderCompanyId);
		}
		CompletableFuture<RightsDtoCore> updateCompanyAddressFrom = CompletableFuture.supplyAsync(() -> {
			if (suborder.getAddressFrom() != null) {
				AtomicBoolean canChange = new AtomicBoolean(true);
				canChangeAddressRights.canChangeAddressRights(suborder.getAddressFrom(), suborder, canChange);
				if (canChange.get()) {
					return updateRightsIgnoreException.updateRightsIgnoreException(suborder.getAddressFrom().getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
				} else {
					Address newAddress = null;
					try {
						newAddress = methodXAddress.methodXAddress(suborder.getAddressFrom().getId(), suborder.getId());
					} catch (MessagingException | ClassNotFoundException | InterruptedException e) {
						throw new RuntimeException(e);
					}
					return updateRightsIgnoreException.updateRightsIgnoreException(newAddress.getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
				}
			}
			return null;
		});
		futures.add(updateCompanyAddressFrom);
		CompletableFuture<RightsDtoCore> updateCompanyAddressTo = CompletableFuture.supplyAsync(() -> {
			if (suborder.getAddressFrom() != null) {
				AtomicBoolean canChange = new AtomicBoolean(true);
				canChangeAddressRights.canChangeAddressRights(suborder.getAddressTo(), suborder, canChange);
				if (canChange.get()) {
					return updateRightsIgnoreException.updateRightsIgnoreException(suborder.getAddressTo().getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
				} else {
					Address newAddress = null;
					try {
						newAddress = methodXAddress.methodXAddress(suborder.getAddressTo().getId(), suborder.getId());
					} catch (MessagingException | ClassNotFoundException | InterruptedException e) {
						throw new RuntimeException(e);
					}
					return updateRightsIgnoreException.updateRightsIgnoreException(newAddress.getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
				}
			}
			return null;
		});
		futures.add(updateCompanyAddressTo);
		CompletableFuture<RightsDtoCore> updateCompanyAddressBilling = CompletableFuture.supplyAsync(() -> {
			if (suborder.getAddressBilling() != null) {
				AtomicBoolean canChange = new AtomicBoolean(true);
				canChangeAddressRights.canChangeAddressRights(suborder.getAddressBilling(), suborder, canChange);
				if (canChange.get()) {
					return updateRightsIgnoreException.updateRightsIgnoreException(suborder.getAddressBilling().getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
				} else {
					Address newAddress = null;
					try {
						newAddress = methodXAddress.methodXAddress(suborder.getAddressBilling().getId(), suborder.getId());
					} catch (MessagingException | ClassNotFoundException | InterruptedException e) {
						throw new RuntimeException(e);
					}
					return updateRightsIgnoreException.updateRightsIgnoreException(newAddress.getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
				}
			}
			return null;
		});
		futures.add(updateCompanyAddressBilling);
		
		
		if (suborder.getProperties() != null && !suborder.getProperties().isEmpty()) {
			suborder.getProperties().forEach(prop -> {
				CompletableFuture<RightsDtoCore> updateProp = CompletableFuture.supplyAsync(() -> {
					return updateRightsIgnoreException.updateRightsIgnoreException(prop.getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
				});
				futures.add(updateProp);
			});
		}
		
		if (suborder.getPackageItems() != null && !suborder.getPackageItems().isEmpty()) {
			suborder.getPackageItems().forEach(packageItem -> {
				CompletableFuture<RightsDtoCore> updateProp = CompletableFuture.supplyAsync(() -> updateRightsIgnoreException.updateRightsIgnoreException(packageItem.getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId));
				futures.add(updateProp);
				if (packageItem.getPackagePackageProperties() != null && !packageItem.getPackagePackageProperties().isEmpty()) {
					packageItem.getPackagePackageProperties().forEach(packagePackageProperty -> {
						CompletableFuture<RightsDtoCore> updatePropIntern = CompletableFuture.supplyAsync(() -> updateRightsIgnoreException.updateRightsIgnoreException(packagePackageProperty.getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId));
						futures.add(updatePropIntern);
					});
				}
			});
		}
		if (suborder.getOrderRoutes() != null && !suborder.getOrderRoutes().isEmpty()) {
			suborder.getOrderRoutes().forEach(orderRoute -> {
				CompletableFuture<RightsDtoCore> updatePropIntern = CompletableFuture.supplyAsync(() -> {
					return updateRightsIgnoreException.updateRightsIgnoreException(orderRoute.getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
				});
				futures.add(updatePropIntern);
			});
		}
		if (suborder.getPayment() != null) {
			CompletableFuture<RightsDtoCore> updatePropIntern = CompletableFuture.supplyAsync(() -> {
				return updateRightsIgnoreException.updateRightsIgnoreException(suborder.getPayment().getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
			});
			futures.add(updatePropIntern);
			
		}
		if (suborder.getCost() != null) {
			CompletableFuture<RightsDtoCore> updatePropIntern = CompletableFuture.supplyAsync(() -> {
				return updateRightsIgnoreException.updateRightsIgnoreException(suborder.getCost().getId(), subCompanyIdSubOrder, emptyProperties, emptyProperties, parentOrderCompanyId);
			});
			futures.add(updatePropIntern);
		}
		
		
		CompletableFuture<Void> allCompleted = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
		
		try {
			allCompleted.get();
			futures.forEach(entry -> {
				try {
					responseTemp.add(entry.get());
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		
		
	}
}
