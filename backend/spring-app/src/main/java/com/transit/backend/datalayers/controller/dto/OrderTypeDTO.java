package com.transit.backend.datalayers.controller.dto;

import lombok.Data;

import java.util.Date;


@Data
public class OrderTypeDTO {
	
	private java.util.UUID id;
	private java.lang.String typename;
	private java.util.Set orderTypeProperties;
	private Date createDate;
	private Date modifyDate;
}
