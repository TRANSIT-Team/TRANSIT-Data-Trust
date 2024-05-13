package com.transit.backend.datalayers.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.transit.backend.config.jackson.JacksonOffSerDateTimeMapperSerialize;
import com.transit.backend.config.jackson.JacksonOffsetDateTimeMapper;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import com.transit.backend.helper.contraints.OrderStatusConstraint;
import com.transit.backend.helper.verification.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.OffsetDateTime;
import java.util.*;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "orders", itemRelation = "order")
public class OrderFullDTO extends AbstractPropertiesParentDTO<OrderDTO, OrderPropertyDTO> {
	
	
	private UUID shortId;
	
	private IdentifierDTO parentOrderId;
	
	@JsonDeserialize(using = JacksonOffsetDateTimeMapper.class)
	@JsonSerialize(using = JacksonOffSerDateTimeMapperSerialize.class)
	private OffsetDateTime destinationDate;
	
	
	@JsonDeserialize(using = JacksonOffsetDateTimeMapper.class)
	@JsonSerialize(using = JacksonOffSerDateTimeMapperSerialize.class)
	private OffsetDateTime pickUpDate;
	
	@NotNull
	@Valid
	private AddressDTO addressFrom;
	@NotNull
	@Valid
	private AddressDTO addressTo;
	@Valid
	private AddressDTO addressBilling;
	
	@Null(groups = ValidationGroups.Post.class)
	private List<OrderPartDTO> suborders;
	@Null(groups = ValidationGroups.Post.class)
	@Null(groups = ValidationGroups.Put.class)
	private Set<IdentifierDTO> orderTypeIds;
	
	@Valid
	private IdentifierDTO companyId;
	
	
	@Null(groups = ValidationGroups.Post.class)
	@Null(groups = ValidationGroups.Put.class)
	private List<IdentifierDTO> orderRouteIds;
	@NotBlank(groups = ValidationGroups.Post.class)
	@NotBlank(groups = ValidationGroups.Put.class)
	@NotBlank(groups = ValidationGroups.Patch.class)
	@OrderStatusConstraint
	private String orderStatus;
	
	@Valid
	private IdentifierDTO paymentId;
	
	@Valid
	
	private IdentifierDTO costId;
	
	@Valid
	private Set<PackageItemDTO> packageItems;
	
	@JsonProperty("orderProperties")
	@Valid
	private SortedSet<OrderPropertyDTO> orderProperties;
	@Null
	private IdentifierDTO deliveryMethodId;
	
	
	private boolean attentionFlag;
	
	private boolean suborderType;
	
	private String internalComment;
	
	private double price;
	
	private ContactPersonDTO contactPerson;
	
	
	private double packagesPrice;
	
	
	private String deliveryPerson;
	
	private double outsourceCost;
	
	private double orderAltPrice;
	
	private UUID oldOrderId;
	
	private UUID newOrderId;
	
	private IdentifierDTO customerId;
	
	
	private OffsetDateTime pickUpDateTo;
	
	
	private OffsetDateTime destinationDateTo;
	
	private String pickUpPerson;
	
	
	private OffsetDateTime pickUpTimestampLeave;
	
	private OffsetDateTime pickUpTimestampArrive;
	
	private OffsetDateTime deliveryTimestampLeave;
	
	private OffsetDateTime deliveryTimestampArrive;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderFullDTO that)) return false;
		if (!super.equals(o)) return false;
		return attentionFlag == that.attentionFlag && suborderType == that.suborderType && Double.compare(price, that.price) == 0 && Double.compare(packagesPrice, that.packagesPrice) == 0 && Double.compare(outsourceCost, that.outsourceCost) == 0 && Objects.equals(parentOrderId, that.parentOrderId) && Objects.equals(destinationDate, that.destinationDate) && Objects.equals(pickUpDate, that.pickUpDate) && Objects.equals(addressFrom, that.addressFrom) && Objects.equals(addressTo, that.addressTo) && Objects.equals(addressBilling, that.addressBilling) && Objects.equals(suborders, that.suborders) && Objects.equals(orderTypeIds, that.orderTypeIds) && Objects.equals(companyId, that.companyId) && Objects.equals(orderRouteIds, that.orderRouteIds) && Objects.equals(orderStatus, that.orderStatus) && Objects.equals(paymentId, that.paymentId) && Objects.equals(costId, that.costId) && Objects.equals(packageItems, that.packageItems) && Objects.equals(orderProperties, that.orderProperties) && Objects.equals(deliveryMethodId, that.deliveryMethodId) && Objects.equals(internalComment, that.internalComment) && Objects.equals(contactPerson, that.contactPerson) && Objects.equals(deliveryPerson, that.deliveryPerson);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parentOrderId, destinationDate, pickUpDate, addressFrom, addressTo, addressBilling, suborders, orderTypeIds, companyId, orderRouteIds, orderStatus, paymentId, costId, packageItems, orderProperties, deliveryMethodId, attentionFlag, suborderType, internalComment, price, contactPerson, packagesPrice, deliveryPerson, outsourceCost);
	}
	
	@Override
	public SortedSet<OrderPropertyDTO> getProperties() {
		return this.orderProperties;
	}
	
	@Override
	public void setProperties(SortedSet<OrderPropertyDTO> properties) {
		this.orderProperties = properties;
	}
}
