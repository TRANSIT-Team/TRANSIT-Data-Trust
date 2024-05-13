package com.transit.backend.datalayers.domain;


import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Order")
@Table(name = "orders")
@Audited(withModifiedFlag = true)
@Cache(region = "orderCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Order extends AbstractParentEntity<OrderProperty> implements Serializable {
	
	private String idString;
	@ManyToOne
	private Order parentOrder;
	
	private OffsetDateTime destinationDate;
	
	
	private OffsetDateTime pickUpDate;
	@OneToOne
	private Address addressFrom;
	
	@OneToOne
	private Address addressTo;
	
	@OneToOne
	private Address addressBilling;
	
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true, mappedBy = "parentOrder")
	
	private List<Order> suborders;
	
	@OneToMany
	private Set<OrderType> orderTypes;
	
	@ManyToOne
	private Company company;
	
	
	@OneToMany
	private List<OrderRoute> orderRoutes;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "orderStatus")
	private OrderStatus orderStatus;
	
	
	@OneToOne
	private Payment payment;
	
	
	@OneToOne
	private Cost cost;
	
	@ManyToMany
	private Set<PackageItem> packageItems;
	
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean suborderType;
	
	
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean attentionFlag;
	
	@Type(type = "text")
	private String internalComment;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "order")
	@OrderBy("key ASC")
	private SortedSet<OrderProperty> orderProperties;
	
	@ManyToOne
	private DeliveryMethod deliveryMethod;
	
	
	@OneToOne
	private ContactPerson contactPerson;
	@Column(nullable = false, columnDefinition = "double precision")
	private double price;
	
	
	@Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
	private long messageCounter;
	
	
	@Column(nullable = false, columnDefinition = "double precision")
	private double packagesPrice;
	
	
	@Column(nullable = false, columnDefinition = "double precision")
	private double outsourceCost;
	
	
	@Column(nullable = false, columnDefinition = "double precision")
	private double orderAltPrice;
	
	
	@Type(type = "text")
	private String reasonForCancel;
	
	
	private UUID oldOrderId;
	
	private UUID newOrderId;
	
	private UUID customerId;
	
	private OffsetDateTime pickUpDateTo;
	
	
	private OffsetDateTime destinationDateTo;
	@Type(type = "text")
	private String pickUpPerson;
	
	private OffsetDateTime pickUpTimestampLeave;
	
	private OffsetDateTime pickUpTimestampArrive;
	
	private OffsetDateTime deliveryTimestampLeave;
	
	private OffsetDateTime deliveryTimestampArrive;
	
	@Column(name = "deliveryPerson")
	@Type(type = "text")
	private String deliveryPerson;
	
	
	@Override
	public SortedSet<OrderProperty> getProperties() {
		return this.orderProperties;
	}
	
	@Override
	public void setProperties(SortedSet<OrderProperty> orderProperties) {
		this.setOrderProperties(orderProperties);
	}
	
	public void setOrderProperties(SortedSet<OrderProperty> orderProperties) {
		if (this.orderProperties == null) {
			this.orderProperties = new TreeSet<>();
		}
		if (!this.orderProperties.equals(orderProperties)) {
			this.orderProperties.clear();
			if (orderProperties != null) {
				this.orderProperties.addAll(orderProperties);
			}
		}
	}
	
	@Override
	public void addProperty(OrderProperty orderProperty) {
		this.addOrderProperties(orderProperty);
	}
	
	public void addOrderProperties(OrderProperty orderProperty) {
		if (this.orderProperties == null) {
			this.orderProperties = new TreeSet<>();
		}
		this.orderProperties.add(orderProperty);
		orderProperty.setOrder(this);
	}
	
	public void setSuborders(List<Order> suborders) {
		if (this.suborders == null) {
			this.suborders = new ArrayList<>();
		}
		this.suborders.clear();
		if (suborders != null) {
			this.suborders.addAll(suborders);
		}
	}
	
	public void setPackageItem(Set<PackageItem> values) {
		if (this.packageItems == null) {
			this.packageItems = new HashSet<>();
		}
		this.packageItems.clear();
		if (values != null) {
			this.packageItems.addAll(values);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Order order = (Order) o;
		return Objects.equal(addressFrom, order.addressFrom) && Objects.equal(addressTo, order.addressTo) && Objects.equal(addressBilling, order.addressBilling) && Objects.equal(orderTypes, order.orderTypes) && Objects.equal(orderRoutes, order.orderRoutes) && Objects.equal(orderStatus, order.orderStatus) && Objects.equal(payment, order.payment) && Objects.equal(packageItems, order.packageItems) && Objects.equal(orderProperties, order.orderProperties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), addressFrom, addressTo, addressBilling, orderTypes, orderRoutes, orderStatus, payment, packageItems, orderProperties);
	}
	
	@PrePersist
	@PreUpdate
	private void save() {
		if (this.orderProperties == null) {
			this.orderProperties = new TreeSet<>();
		}
		if (this.suborders == null) {
			this.suborders = new ArrayList<>();
		}
		if (this.orderTypes == null) {
			this.orderTypes = new HashSet<>();
		}
		if (this.orderRoutes == null) {
			this.orderRoutes = new ArrayList<>();
		}
		if (this.packageItems == null) {
			this.packageItems = new HashSet<>();
		}
		if (!Double.isNaN(price) && price == 0.0) {
			price = Double.NaN;
		}
		if (!Double.isNaN(packagesPrice) && packagesPrice == 0.0) {
			packagesPrice = Double.NaN;
		}
		if (!Double.isNaN(outsourceCost) && outsourceCost == 0.0) {
			outsourceCost = Double.NaN;
		}
		if (!Double.isNaN(orderAltPrice) && orderAltPrice == 0.0) {
			orderAltPrice = Double.NaN;
		}
		if (getId() != null) {
			this.idString = getId().toString();
		}
	}
	
	public Order copyOrder() {
		var order = new Order();
		order.setDestinationDate(this.destinationDate);
		order.setPickUpDate(this.pickUpDate);
		order.setDestinationDateTo(this.destinationDateTo);
		order.setDestinationDateTo(this.pickUpDateTo);
		order.setAddressFrom(this.addressFrom);
		order.setAddressTo(this.addressTo);
		order.setAddressBilling(this.addressBilling);
		order.setOrderStatus(OrderStatus.CREATED);
		order.setOrderTypes(this.orderTypes);
		order.setCompany(this.company);
		order.setOrderRoutes(this.orderRoutes);
		order.setPayment(this.payment);
		order.setCost(this.cost);
		
		order.setDeliveryMethod(this.deliveryMethod);
		order.setContactPerson(this.contactPerson);
		
		order.setPrice(this.price);
		order.setMessageCounter(0);
		
		order.setPackagesPrice(this.packagesPrice);
		
		order.setOrderAltPrice(this.orderAltPrice);
		
		order.setOldOrderId(this.getId());
		
		if (order.isSuborderType()) {
			order.setSuborderType(this.suborderType);
			order.setParentOrder(this.parentOrder);
			order.setOrderStatus(OrderStatus.OPEN);
		}
		return order;
		
	}
	
	public void setOrderTypes(Set<OrderType> values) {
		if (this.orderTypes == null) {
			this.orderTypes = new HashSet<OrderType>();
		}
		this.orderTypes.clear();
		if (values != null) {
			this.orderTypes.addAll(values);
		}
	}
	
	public void setOrderRoutes(List<OrderRoute> values) {
		if (this.orderRoutes == null) {
			this.orderRoutes = new ArrayList<>();
		}
		this.orderRoutes.clear();
		if (values != null) {
			this.orderRoutes.addAll(values);
		}
	}
	
	
}
