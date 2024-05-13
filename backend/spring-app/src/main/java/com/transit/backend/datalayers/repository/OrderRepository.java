package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.transferentities.OrderStatusProjection;
import com.transit.backend.transferentities.OrderDaily;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;


@Repository
public interface OrderRepository extends AbstractRepository<Order> {
	
	public boolean existsOrdersByAddressBillingIdOrAddressFromIdOrAddressToId(UUID addressBillingId, UUID addressFromId, UUID addressToId);
	
	public Stream<Order> findOrdersByAddressBillingIdOrAddressFromIdOrAddressToId(UUID addressBillingId, UUID addressFromId, UUID addressToId);
	
	public Stream<OrderStatusProjection> findAllByIdIn(List<UUID> ids);
	
	public Stream<OrderStatusProjection> findAllByIdInAndDeleted(List<UUID> ids, boolean deleted);
	
	
	public Stream<OrderStatusProjection> findAllByParentOrderOrderStatusAndDeletedAndIdIn(OrderStatus status, boolean deleted, List<UUID> ids);
	
	public Stream<OrderDaily> findAllByDeletedAndIdIn(boolean deleted, List<UUID> ids);
	
	public Stream<Order> findAllByCompanyId(UUID companyId);
	
	
}