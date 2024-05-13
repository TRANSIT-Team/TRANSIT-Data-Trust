package com.transit.backend.datalayers.controller.dto;

import lombok.Data;

import java.util.Date;


@Data
public class LoggerPropertiesDTO {
	
	private java.util.UUID id;
	private java.lang.String key;
	private java.lang.String value;
	private Date createDate;
	private Date modifyDate;
}
