package com.transit.backend.datalayers.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.transit.backend.config.jackson.JacksonDeserializeDouble;
import com.transit.backend.config.jackson.JacksonOffSerDateTimeMapperSerialize;
import com.transit.backend.config.jackson.JacksonOffsetDateTimeMapper;
import com.transit.backend.config.jackson.JacksonSerializeDouble;
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
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "orders", itemRelation = "order")
public class OrderDTO extends AbstractPropertiesParentDTO<OrderDTO, OrderPropertyDTO> {
	
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
	private IdentifierDTO addressIdFrom;
	@NotNull
	@Valid
	private IdentifierDTO addressIdTo;
	@Valid
	private IdentifierDTO addressIdBilling;
	
	@Null(groups = ValidationGroups.Post.class)
	private List<IdentifierDTO> suborderIds;
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
	private Set<IdentifierDTO> packageItemIds;
	
	@JsonProperty("orderProperties")
	@Valid
	private SortedSet<OrderPropertyDTO> orderProperties;
	@Null
	private IdentifierDTO deliveryMethodId;
	
	
	private boolean attentionFlag;
	
	private boolean suborderType;
	
	private String internalComment;
	
	
	@JsonDeserialize(using = JacksonDeserializeDouble.class)
	@JsonSerialize(using = JacksonSerializeDouble.class)
	private Double price;
	
	private IdentifierDTO contactPersonId;
	
	@Valid
	private Set<IdentifierDTO> parentOrderInformationIds;
	@JsonDeserialize(using = JacksonDeserializeDouble.class)
	@JsonSerialize(using = JacksonSerializeDouble.class)
	private Double packagesPrice;
	
	
	private String deliveryPerson;
	@JsonDeserialize(using = JacksonDeserializeDouble.class)
	@JsonSerialize(using = JacksonSerializeDouble.class)
	private Double outsourceCost;
	@JsonDeserialize(using = JacksonDeserializeDouble.class)
	@JsonSerialize(using = JacksonSerializeDouble.class)
	private Double orderAltPrice;
	
	private String reasonForCancel;
	
	private UUID oldOrderId;
	
	private UUID newOrderId;
	
	private IdentifierDTO customerId;
	
	private OffsetDateTime pickUpDateTo;
	
	
	private OffsetDateTime destinationDateTo;
	
	private String pickUpPerson;
	
	
	private boolean hasNotableSuborderStatus;
	
	private String notableSuborderStatus;
	
	private OffsetDateTime pickUpTimestampLeave;
	
	private OffsetDateTime pickUpTimestampArrive;
	
	private OffsetDateTime deliveryTimestampLeave;
	
	private OffsetDateTime deliveryTimestampArrive;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderDTO orderDTO)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(parentOrderId, orderDTO.parentOrderId) && Objects.equal(destinationDate, orderDTO.destinationDate) && Objects.equal(pickUpDate, orderDTO.pickUpDate) && Objects.equal(addressIdFrom, orderDTO.addressIdFrom) && Objects.equal(addressIdTo, orderDTO.addressIdTo) && Objects.equal(addressIdBilling, orderDTO.addressIdBilling) && Objects.equal(suborderIds, orderDTO.suborderIds) && Objects.equal(orderTypeIds, orderDTO.orderTypeIds) && Objects.equal(companyId, orderDTO.companyId) && Objects.equal(orderRouteIds, orderDTO.orderRouteIds) && Objects.equal(orderStatus, orderDTO.orderStatus) && Objects.equal(paymentId, orderDTO.paymentId) && Objects.equal(packageItemIds, orderDTO.packageItemIds) && Objects.equal(orderProperties, orderDTO.orderProperties) && Objects.equal(deliveryMethodId, orderDTO.deliveryMethodId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), parentOrderId, destinationDate, pickUpDate, addressIdFrom, addressIdTo, addressIdBilling, suborderIds, orderTypeIds, companyId, orderRouteIds, orderStatus, paymentId, packageItemIds, orderProperties, deliveryMethodId);
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
