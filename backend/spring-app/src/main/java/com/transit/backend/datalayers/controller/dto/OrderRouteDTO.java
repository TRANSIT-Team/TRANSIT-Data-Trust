package com.transit.backend.datalayers.controller.dto;

import lombok.Data;

import java.util.Date;


@Data
public class OrderRouteDTO {
	
	private java.util.UUID id;
	private java.util.List orderLegs;
	private Date createDate;
	private Date modifyDate;
}
